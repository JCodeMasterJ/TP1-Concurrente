import java.util.Random;

public class WorkerThread implements Runnable {

    private Cluster cluster;
    private Random random;

    public WorkerThread(Cluster cluster) {
        this.cluster = cluster;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {

                Job job = cluster.tomarJobEnEjecucion();
//                if (job == null) break; // Si recibe null, el hilo termina limpiamente
                if (job == null) {
                    if (cluster.isSchedulerTerminado() && cluster.getTotalFinal() >= 500) {
                        break;
                    }
                    Thread.sleep(50);
                    continue;
                }

                int prob = random.nextInt(100);

                if (prob < 90) {
                    // ✅ éxito
                    cluster.moverAFinalizados(job);
                } else {
                    // ❌ error
                    cluster.moverAFallidos(job);
                }

                // Simula tiempo de ejecución (más largo)
                Thread.sleep(120);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}