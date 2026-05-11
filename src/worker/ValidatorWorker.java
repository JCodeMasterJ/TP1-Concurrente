package worker;

import model.Job;
import monitor.ClusterMonitor;
import monitor.JobQueueMonitor;

import java.util.Random;

public class ValidatorWorker implements Runnable {
    private final JobQueueMonitor queueMonitor;
    private final ClusterMonitor clusterMonitor;
    private final int iterDelay;
    private final Random random = new Random();

    public ValidatorWorker(JobQueueMonitor queueMonitor, ClusterMonitor clusterMonitor, int iterDelay) {
        this.queueMonitor = queueMonitor;
        this.clusterMonitor = clusterMonitor;
        this.iterDelay = iterDelay;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = queueMonitor.popFromQueued();
                if (job == null) break;

                // 85% válido, 15% inválido
                int chance = random.nextInt(100);
                if (chance < 85) {
                    // Válido
                    clusterMonitor.freeNode(job.getAssignedNode());
                    queueMonitor.pushToExecuting(job);
                } else {
                    // Inválido
                    clusterMonitor.markNodeOutOfService(job.getAssignedNode());
                    queueMonitor.pushToFailed(job);
                }

                Thread.sleep(iterDelay);
            }
        } catch (InterruptedException e) {
            // Hilo interrumpido para finalizar
        }
    }
}
