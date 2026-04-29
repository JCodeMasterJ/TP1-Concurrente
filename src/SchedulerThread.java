import java.util.List;

public class SchedulerThread implements Runnable {

    private Cluster cluster;
    private List<Job> jobs;

    public SchedulerThread(Cluster cluster, List<Job> jobs) {
        this.cluster = cluster;
        this.jobs = jobs;
    }

    @Override
    public void run() {
        for (Job job : jobs) {
            boolean asignado = false;
            while (!asignado) {
                // El Cluster ya se encargó de buscarlo Y reservarlo (marcarlo OCUPADO)
                Nodo nodo = cluster.buscarNodoLibre();

                if (nodo != null) {
                    // Ya no preguntamos if(nodo.asignarJob()), porque ya se hizo dentro de buscarNodoLibre
                    job.asignarNodo(nodo);
                    cluster.moverACola(job);
                    asignado = true;
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return; // Importante salir si se interrumpe
                    }
                }
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}