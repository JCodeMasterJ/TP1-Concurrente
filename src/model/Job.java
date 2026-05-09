package model;

public class Job {
    private int id;
    private Node assignedNode;

    public Job(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Node getAssignedNode() {
        return assignedNode;
    }

    public void setAssignedNode(Node assignedNode) {
        this.assignedNode = assignedNode;
    }

    @Override
    public String toString() {
        return "Job#" + id;
    }
}
