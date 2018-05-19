package utils;

public class Node implements Comparable<Node>{

    private final int value;
    private final int priority;

    public Node(int v) {
        this.value = v;
        this.priority = v;
    }

    public Node(int x, int y) {
        this.value = x;
        this.priority = y;
    }

    public int getValue() {
        return this.value;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "[ v: " + this.getValue() + ", p: " + this.getPriority() + " ]";
    }

    @Override
    public int compareTo(Node tuple) {
        return Integer.compare(this.getPriority(), tuple.getPriority());
    }
}
