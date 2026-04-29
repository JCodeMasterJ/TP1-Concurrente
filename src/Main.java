import java.util.*;

public class Main {

    public static void main(String[] args) {

        long timer = System.currentTimeMillis();
        // ========================
        // 1. Crear nodos
        // ========================
        List<Nodo> nodos = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            nodos.add(new Nodo(i));
        }

        // ========================
        // 2. Crear jobs
        // ========================
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            jobs.add(new Job(i));
        }

        // ========================
        // 3. Crear cluster
        // ========================
        Cluster cluster = new Cluster(nodos);

        // ========================
        // 4. Crear threads
        // ========================

        // Scheduler
        int numSchedulers = 3;
        int jobsPorScheduler = jobs.size() / numSchedulers;
        List<Thread> schedulers = new ArrayList<>();

        for (int i = 0; i < numSchedulers; i++) {

            int inicio = i * jobsPorScheduler;
            int fin = (i == numSchedulers - 1) ? jobs.size() : inicio + jobsPorScheduler;

            List<Job> subLista = new ArrayList<>(jobs.subList(inicio, fin));

            Thread t = new Thread(new SchedulerThread(cluster, subLista));
            schedulers.add(t);
            t.start();
        }

        // Validator
        for (int i = 0; i < 2; i++) {
            new Thread(new ValidatorThread(cluster)).start();
        }

        // Worker
        for (int i = 0; i < 3; i++) {
            new Thread(new WorkerThread(cluster)).start();
        }

        // Auditor
        for (int i = 0; i < 2; i++) {
            new Thread(new AuditorThread(cluster)).start();
        }

        // Logger
        new Thread(new LoggerThread(cluster, timer)).start();

        //Hilo aparte para los schedulers
        new Thread(() -> {
            for (Thread t : schedulers) {
                try {
                    t.join(); // espera a que TODOS los schedulers terminen
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cluster.setSchedulerTerminado(); // 🔥 ahora sí correcto
        }).start();
    }
}