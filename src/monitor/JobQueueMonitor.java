package monitor;

import model.Job;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class JobQueueMonitor {
    private final List<Job> initialPool = new LinkedList<>();
    private final List<Job> queued = new LinkedList<>();
    private final List<Job> executing = new LinkedList<>();
    private final List<Job> finished = new LinkedList<>();
    private final List<Job> failed = new LinkedList<>();
    private final List<Job> validated = new LinkedList<>();

    private boolean isSystemActive = true;
    private final Random random = new Random();

    // Métodos genéricos de ayuda para encolar y desencolar.

    private void pushTo(List<Job> list, Job job) {
        synchronized (list) {
            list.add(job);
            list.notifyAll();
        }
    }

    private Job popFrom(List<Job> list) throws InterruptedException {
        synchronized (list) {
            while (list.isEmpty()) {
                if (!isSystemActive) {
                    return null;
                }
                list.wait();
            }
            return list.remove(0);
        }
    }

    private Job popRandomFrom(List<Job> list) throws InterruptedException {
        synchronized (list) {
            while (list.isEmpty()) {
                if (!isSystemActive) {
                    return null;
                }
                list.wait();
            }
            int randomIndex = random.nextInt(list.size());
            return list.remove(randomIndex);
        }
    }

    // --- INITIAL POOL ---
    public void pushToInitialPool(Job job) {
        pushTo(initialPool, job);
    }

    public Job popFromInitialPool() throws InterruptedException {
        return popFrom(initialPool);
    }

    public int getInitialPoolSize() {
        synchronized (initialPool) {
            return initialPool.size();
        }
    }

    // --- QUEUED ---
    public void pushToQueued(Job job) {
        pushTo(queued, job);
    }

    public Job popFromQueued() throws InterruptedException {
        return popRandomFrom(queued);
    }

    // --- EXECUTING ---
    public void pushToExecuting(Job job) {
        pushTo(executing, job);
    }

    public Job popFromExecuting() throws InterruptedException {
        return popFrom(executing);
    }

    // --- FINISHED ---
    public void pushToFinished(Job job) {
        pushTo(finished, job);
    }

    public Job popFromFinished() throws InterruptedException {
        return popFrom(finished);
    }

    // --- FAILED ---
    public void pushToFailed(Job job) {
        synchronized (failed) {
            failed.add(job);
        }
    }

    public int getFailedSize() {
        synchronized (failed) {
            return failed.size();
        }
    }

    // --- VALIDATED ---
    public void pushToValidated(Job job) {
        synchronized (validated) {
            validated.add(job);
        }
    }

    public int getValidatedSize() {
        synchronized (validated) {
            return validated.size();
        }
    }

    // --- CONTROL ---
    /**
     * Interrumpe todos los waits para finalizar el programa
     */
    public void shutdown() {
        isSystemActive = false;
        synchronized (initialPool) {
            initialPool.notifyAll();
        }
        synchronized (queued) {
            queued.notifyAll();
        }
        synchronized (executing) {
            executing.notifyAll();
        }
        synchronized (finished) {
            finished.notifyAll();
        }
    }
}
