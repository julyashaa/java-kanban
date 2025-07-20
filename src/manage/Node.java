package manage;

import java.util.Objects;
import tasks.Task;

public class Node {
    private final Task task;
    private Node prev;
    private Node next;

    public Node(Task task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    public Task getTask() {
        return task;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(prev, node.prev) &&
                Objects.equals(next, node.next) &&
                Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (prev != null) {
            hash = hash * 31 + prev.hashCode();
        }
        hash = hash * 31;

        if (task != null) {
            hash = hash * 31 + task.hashCode();
        }
        hash = hash * 31;

        if (next != null) {
            hash = hash * 31 + next.hashCode();
        }
        return hash;
    }
}