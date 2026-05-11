import model.Job;
import monitor.ClusterMonitor;
import monitor.JobQueueMonitor;
import worker.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Simulador de Cluster...");

        // Configuración
        int rows = 20;
        int cols = 10;
        int totalJobs = 500;

        // Monitores (Recursos compartidos protegidos)
        ClusterMonitor clusterMonitor = new ClusterMonitor(rows, cols);
        JobQueueMonitor queueMonitor = new JobQueueMonitor();

        // Inicializar Jobs
        for (int i = 1; i <= totalJobs; i++) {
            queueMonitor.pushToInitialPool(new Job(i));
        }

        long startTime = System.currentTimeMillis();

        // Tiempos de demora fija.
        int schedulerDelay = 10;
        int validatorDelay = 20;
        int executorDelay = 120;
        int auditorDelay = 30;

        List<Thread> workers = new ArrayList<>();

        // 3 Schedulers
        for (int i = 0; i < 3; i++) {
            workers.add(new SchedulerWorker(queueMonitor, clusterMonitor, schedulerDelay));
        }

        // 2 Validators
        for (int i = 0; i < 2; i++) {
            workers.add(new ValidatorWorker(queueMonitor, clusterMonitor, validatorDelay));
        }

        // 3 Executors
        for (int i = 0; i < 3; i++) {
            workers.add(new ExecutorWorker(queueMonitor, executorDelay));
        }

        // 2 Auditors
        for (int i = 0; i < 2; i++) {
            workers.add(new AuditorWorker(queueMonitor, auditorDelay));
        }

        // Lanzar todos los hilos
        for (Thread worker : workers) {
            worker.start();
        }

        // Logger Worker (Manejador de Fin de Ejecución)
        LoggerWorker loggerWorker = new LoggerWorker(queueMonitor, clusterMonitor, totalJobs, startTime, workers);
        loggerWorker.start();

        // Esperar a que termine el logger
        try {
            loggerWorker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Ejecución finalizada.");
    }
}
