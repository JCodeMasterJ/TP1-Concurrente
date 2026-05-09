package model;

public class Node {
    private int x;
    private int y;
    private NodeStatus status;
    private int executionCount;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.status = NodeStatus.FREE;
        this.executionCount = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void incrementExecutionCount() {
        this.executionCount++;
    }

    @Override
    public String toString() {
        return "Node[" + x + "][" + y + "] (Execs: " + executionCount + ", Status: " + status + ")";
    }
}
