import java.util.*;

public class Cluster {

    private List<Nodo> nodos;

    private Queue<Job> jobsEnCola;
    private Queue<Job> jobsEnEjecucion;
    private Queue<Job> jobsFinalizados;

    private List<Job> jobsFallidos;
    private List<Job> jobsValidados;

    public Cluster(List<Nodo> nodos) {
        this.nodos = nodos;

        this.jobsEnCola = new LinkedList<>();
        this.jobsEnEjecucion = new LinkedList<>();
        this.jobsFinalizados = new LinkedList<>();

        this.jobsFallidos = new ArrayList<>();
        this.jobsValidados = new ArrayList<>();
    }

    // INICIO DEL PROCESAMIENTO
    private final Random random = new Random();
    public synchronized Nodo buscarNodoLibre() {
        int totalNodos = nodos.size();
        // 1. Elegimos un punto de inicio aleatorio
        int inicio = random.nextInt(totalNodos);

        // 2. Recorremos circularmente para asegurar que revisamos todos los nodos
        for (int i = 0; i < totalNodos; i++) {
            int idx = (inicio + i) % totalNodos;
            Nodo n = nodos.get(idx);

            // 3. Verificamos disponibilidad
            if (n.estaLibre()) {
                // CRUCIAL: asignarJob() debe ser synchronized en la clase Nodo
                // y debe poner el estado en OCUPADO y retornar true.
                if (n.asignarJob()) {
                    return n;
                }
            }
        }
        // Si después de dar toda la vuelta no hay ninguno
        return null;
    }

    // METODOS PARA COLA
    public synchronized void moverACola(Job job) {
        jobsEnCola.add(job);
        job.setEstado(EstadoJob.EN_COLA);
        notifyAll();
    }
    public synchronized Job tomarJobEnCola() throws InterruptedException {
        while (jobsEnCola.isEmpty()) {
            if (schedulerTerminado && jobsEnCola.isEmpty()) {
                notifyAll(); // 🔥 DESPERTADOR: Avisa a otros hilos de esta misma etapa
                return null;
            }
            wait();
        }
        return jobsEnCola.poll();
    }

    // METODOS PARA EJECUCIÓN
    public synchronized void moverAEjecucion(Job job) {
        jobsEnEjecucion.add(job);
        job.setEstado(EstadoJob.EN_EJECUCION);
        notifyAll();
    }
    public synchronized Job tomarJobEnEjecucion() throws InterruptedException {
        while (jobsEnEjecucion.isEmpty()) {
            if (schedulerTerminado && jobsEnCola.isEmpty() && jobsEnEjecucion.isEmpty()) {
                notifyAll(); // 🔥 DESPERTADOR: Avisa a otros hilos de esta misma etapa
                return null; // El Worker ahora sabe que puede morir
            }
            wait();
        }
        return jobsEnEjecucion.poll();
    }

    // METODOS PARA FINALIZADOS
    public synchronized void moverAFinalizados(Job job) {
        jobsFinalizados.add(job);
        job.setEstado(EstadoJob.FINALIZADO);
        notifyAll();
    }
    public synchronized Job tomarJobFinalizado() throws InterruptedException {
        while (jobsFinalizados.isEmpty()) {
            if (schedulerTerminado && jobsEnCola.isEmpty() && jobsEnEjecucion.isEmpty()&&jobsFinalizados.isEmpty()) {
                notifyAll(); // 🔥 DESPERTADOR: Avisa a otros hilos de esta misma etapa
                return null; // El Worker ahora sabe que puede morir
            }
            wait();
        }
        return jobsFinalizados.poll();
    }

    // METODO PARA FALLIDOS
    public synchronized void moverAFallidos(Job job) {
        jobsFallidos.add(job);
        job.setEstado(EstadoJob.FALLIDO);
    }

    // METODO PARA VALIDADOS
    public synchronized void moverAValidados(Job job) {
        jobsValidados.add(job);
        job.setEstado(EstadoJob.VALIDADO);
    }

    // GETTERS
    public synchronized int getColaSize() {
        return jobsEnCola.size();
    }

    public synchronized int getEjecucionSize() {
        return jobsEnEjecucion.size();
    }

    public synchronized int getFinalizadosSize() {
        return jobsFinalizados.size();
    }

    public synchronized int getCantidadFallidos() {
        return jobsFallidos.size();
    }

    public synchronized int getCantidadValidados() {
        return jobsValidados.size();
    }

    public synchronized int getTotalFinal() {
        return jobsFallidos.size() + jobsValidados.size();
    }

    // FIN DEL PROCESAMIENTO
    public synchronized boolean sistemaVacio() {
        return jobsEnCola.isEmpty()&& jobsEnEjecucion.isEmpty() && jobsFinalizados.isEmpty();
    }

    private boolean schedulerTerminado = false;
    public synchronized void setSchedulerTerminado() {
        schedulerTerminado = true;
        notifyAll();
    }
    public synchronized boolean isSchedulerTerminado() {
        return schedulerTerminado;
    }

    public synchronized String obtenerEstadisticasNodos() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ESTADÍSTICAS DE USO POR NODO ===\n");
        for (Nodo n : nodos) {
            sb.append(String.format("Nodo ID: %3d | Ejecuciones: %d | Estado Final: %s\n",
                    n.getId(), n.getContadorEjecuciones(), n.getEstado()));
        }
        return sb.toString();
    }

}
