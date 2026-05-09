package monitor;

import model.Node;
import model.NodeStatus;

import java.util.Random;

public class ClusterMonitor {
    private final Node[][] matrix;
    private final int rows;
    private final int cols;
    private final Random random;

    public ClusterMonitor(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new Node[rows][cols];
        this.random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = new Node(i, j);
            }
        }
    }

    /**
     * Busca de manera bloqueante y sincronizada un nodo libre.
     * Si no hay nodos libres, el hilo espera (se duerme) hasta que uno se libere.
     */
    public synchronized Node acquireFreeNode() throws InterruptedException {
        Node freeNode = findFreeNode();
        while (freeNode == null) {
            // Esperar a que algún nodo sea liberado
            this.wait();
            freeNode = findFreeNode();
        }
        
        freeNode.setStatus(NodeStatus.BUSY);
        freeNode.incrementExecutionCount();
        return freeNode;
    }

    /**
     * Busca un nodo libre aleatoriamente de manera heurística.
     * Si tras varios intentos aleatorios no encuentra, hace búsqueda secuencial.
     */
    private Node findFreeNode() {
        // Intento aleatorio
        for (int i = 0; i < 10; i++) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (matrix[r][c].getStatus() == NodeStatus.FREE) {
                return matrix[r][c];
            }
        }
        
        // Búsqueda secuencial exhaustiva
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j].getStatus() == NodeStatus.FREE) {
                    return matrix[i][j];
                }
            }
        }
        
        return null; // No hay nodos libres
    }

    public synchronized void freeNode(Node node) {
        node.setStatus(NodeStatus.FREE);
        // Notificar a los que esperan por un nodo libre
        this.notifyAll();
    }

    public synchronized void markNodeOutOfService(Node node) {
        node.setStatus(NodeStatus.OUT_OF_SERVICE);
    }
    
    public Node[][] getMatrix() {
        return matrix;
    }
}
