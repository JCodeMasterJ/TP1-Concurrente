package worker;

import model.Node;
import monitor.ClusterMonitor;
import monitor.JobQueueMonitor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LoggerWorker implements Runnable {
    private final JobQueueMonitor queueMonitor;
    private final ClusterMonitor clusterMonitor;
    private final int totalJobs;
    private final long startTime;
    private final List<Thread> workers;
    private boolean finished = false;

    public LoggerWorker(JobQueueMonitor queueMonitor, ClusterMonitor clusterMonitor, int totalJobs, long startTime,
            List<Thread> workers) {
        this.queueMonitor = queueMonitor;
        this.clusterMonitor = clusterMonitor;
        this.totalJobs = totalJobs;
        this.startTime = startTime;
        this.workers = workers;
    }

    @Override
    public void run() {
        try (FileWriter writer = new FileWriter("log.txt")) {
            while (!finished) {
                int failed = queueMonitor.getFailedSize();
                int validated = queueMonitor.getValidatedSize();

                String logLine = "Time: " + (System.currentTimeMillis() - startTime) + "ms | Failed: " + failed
                        + " | Validated: " + validated + "\n";
                writer.write(logLine);
                writer.flush();

                if (failed + validated == totalJobs) {
                    finished = true;
                    printFinalStats();
                    shutdownWorkers();
                    break;
                }

                Thread.sleep(200);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printFinalStats() {
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("============== ESTADISTICAS FINALES ==============");
        System.out.println("Tiempo Total: " + totalTime + " ms (" + (totalTime / 1000.0) + " segundos)");
        System.out.println("Jobs Fallidos: " + queueMonitor.getFailedSize());
        System.out.println("Jobs Validados: " + queueMonitor.getValidatedSize());
        System.out.println("----------------- Nodos -----------------");

        Node[][] matrix = clusterMonitor.getMatrix();
        int free = 0;
        int oos = 0;
        int busy = 0;
        long totalExecutions = 0;

        for (Node[] row : matrix) {
            for (Node node : row) {
                totalExecutions += node.getExecutionCount();
                switch (node.getStatus()) {
                    case FREE:
                        free++;
                        break;
                    case BUSY:
                        busy++;
                        break;
                    case OUT_OF_SERVICE:
                        oos++;
                        break;
                }
            }
        }

        System.out.println("Nodos Libres: " + free);
        System.out.println("Nodos Ocupados: " + busy);
        System.out.println("Nodos Fuera de Servicio: " + oos);
        System.out.println("Total de Ejecuciones Asignadas: " + totalExecutions);
        System.out.println("==================================================");
    }

    private void shutdownWorkers() {
        queueMonitor.shutdown();
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }
}
