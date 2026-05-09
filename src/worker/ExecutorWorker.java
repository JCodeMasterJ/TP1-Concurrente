package worker;

import model.Job;
import monitor.JobQueueMonitor;

import java.util.Random;

public class ExecutorWorker extends Thread {
    private final JobQueueMonitor queueMonitor;
    private final int iterDelay;
    private final Random random = new Random();

    public ExecutorWorker(JobQueueMonitor queueMonitor, int iterDelay) {
        this.queueMonitor = queueMonitor;
        this.iterDelay = iterDelay;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = queueMonitor.popFromExecuting();
                if (job == null) break;

                // 90% éxito, 10% error
                int chance = random.nextInt(100);
                if (chance < 90) {
                    queueMonitor.pushToFinished(job);
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
