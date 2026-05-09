package worker;

import model.Job;
import model.Node;
import monitor.ClusterMonitor;
import monitor.JobQueueMonitor;

public class SchedulerWorker extends Thread {
    private final JobQueueMonitor queueMonitor;
    private final ClusterMonitor clusterMonitor;
    private final int iterDelay;

    public SchedulerWorker(JobQueueMonitor queueMonitor, ClusterMonitor clusterMonitor, int iterDelay) {
        this.queueMonitor = queueMonitor;
        this.clusterMonitor = clusterMonitor;
        this.iterDelay = iterDelay;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = queueMonitor.popFromInitialPool();
                if (job == null) break; // Fin de sistema

                // Obtener nodo libre de forma bloqueante
                Node assignedNode = clusterMonitor.acquireFreeNode();
                
                // Asignar y encolar
                job.setAssignedNode(assignedNode);
                queueMonitor.pushToQueued(job);
                
                Thread.sleep(iterDelay);
            }
        } catch (InterruptedException e) {
            // Hilo interrumpido para finalizar
        }
    }
}
