import java.util.Random;

public class ValidatorThread implements Runnable {

    private Cluster cluster;
    private Random random;

    public ValidatorThread(Cluster cluster) {
        this.cluster = cluster;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Job job = cluster.tomarJobEnCola(); // espera si no hay

                if (job == null) {

                    if (cluster.isSchedulerTerminado()) {
                        // espera un poco por si quedan jobs en tránsito
//                        Thread.sleep(50);

                        // reintenta
                        job = cluster.tomarJobEnCola();

                        if (job == null) {
                            break; // ahora sí termina
                        }
                    } else {
                        continue;
                    }
                }

                Nodo nodo = job.getNodoAsignado();

                int prob = random.nextInt(100);

                if (prob < 85) {
                    // ✅ válido
                    nodo.liberar();
                    cluster.moverAEjecucion(job);

                } else {
                    // ❌ inválido
                    nodo.marcarFueraDeServicio();
                    cluster.moverAFallidos(job);
                }

                // Simula tiempo de validación
                Thread.sleep(80);


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }

    }
}