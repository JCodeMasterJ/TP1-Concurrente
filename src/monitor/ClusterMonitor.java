package monitor;

import model.Node;
import model.NodeStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClusterMonitor {
    private final Node[][] matrix;
    private final int rows;
    private final int cols;
    private final Random random;
    private final List<Node> freeNodes;

    public ClusterMonitor(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new Node[rows][cols];
        this.random = new Random();
        this.freeNodes = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = new Node(i, j);
                freeNodes.add(matrix[i][j]);
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
        if (freeNodes.isEmpty()) {
            return null; // no hay libres
        }

        // Tomamos un indice al azar de los que sabemos que estan libres
        int randomIndex = random.nextInt(freeNodes.size());
        // Lo sacamos de la lista que otro no lo tome
        return freeNodes.remove(randomIndex);
    }

    public synchronized void freeNode(Node node) {
        node.setStatus(NodeStatus.FREE);
        freeNodes.add(node);
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
