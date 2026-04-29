public class Job {

    private int id;
    private EstadoJob estado;
    private Nodo nodoAsignado;

    public Job(int id) {
        this.id = id;
        this.estado = EstadoJob.NUEVO;
        this.nodoAsignado = null;
    }

    synchronized void setEstado(EstadoJob estado) {
        this.estado = estado;
    }

    public synchronized Nodo getNodoAsignado() {
        return nodoAsignado;
    }

    synchronized void asignarNodo(Nodo nodo) {
        this.nodoAsignado = nodo;
    }
}