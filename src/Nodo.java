public class Nodo {

    private int id;
    private EstadoNodo estado;
    private int contadorEjecuciones;

    public Nodo(int id) {
        this.id = id;
        this.estado = EstadoNodo.LIBRE;
        this.contadorEjecuciones = 0;
    }

    public int getId() {
        return id;
    }

    public synchronized EstadoNodo getEstado() {
        return estado;
    }

    public synchronized boolean estaLibre() {
        return estado == EstadoNodo.LIBRE;
    }

    public synchronized boolean asignarJob() {
        if (estado == EstadoNodo.LIBRE) {
            estado = EstadoNodo.OCUPADO;
            contadorEjecuciones++;
            return true;
        }
        return false; // Si el nodo no esta libre devuelve falso
    }
    public synchronized void liberar() {
        if (estado != EstadoNodo.FUERA_DE_SERVICIO) {
            estado = EstadoNodo.LIBRE; //Libera el nodo
        }
    }

    public synchronized void marcarFueraDeServicio() {
        estado = EstadoNodo.FUERA_DE_SERVICIO;
    }

    public synchronized int getContadorEjecuciones() {
        return contadorEjecuciones;
    }
}