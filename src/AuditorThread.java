import java.util.Random;

public class AuditorThread implements Runnable {

    private Cluster cluster;
    private Random random;

    public AuditorThread(Cluster cluster) {
        this.cluster = cluster;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Job job = cluster.tomarJobFinalizado();
                if (job == null) break; // Si recibe null, el hilo termina limpiamente

                int prob = random.nextInt(100);

                if (prob < 95) {
                    // ✅ correcto
                    cluster.moverAValidados(job);
                } else {
                    // ❌ inconsistente
                    cluster.moverAFallidos(job);
                }

                // Simula tiempo de auditoría
                Thread.sleep(70);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}