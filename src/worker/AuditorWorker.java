package worker;

import model.Job;
import monitor.JobQueueMonitor;

import java.util.Random;

public class AuditorWorker implements Runnable {
    private final JobQueueMonitor queueMonitor;
    private final int iterDelay;
    private final Random random = new Random();

    public AuditorWorker(JobQueueMonitor queueMonitor, int iterDelay) {
        this.queueMonitor = queueMonitor;
        this.iterDelay = iterDelay;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = queueMonitor.popFromFinished();
                if (job == null) break;

                // 95% correcto, 5% inconsistente
                int chance = random.nextInt(100);
                if (chance < 95) {
                    queueMonitor.pushToValidated(job);
                } else {
                    queueMonitor.pushToFailed(job);
                }

                Thread.sleep(iterDelay);
            }
        } catch (InterruptedException e) {
            // Hilo interrumpido para finalizar
        }
    }
}
