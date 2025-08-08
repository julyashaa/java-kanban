package manage;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int id = task.getId();
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }

        Task copy = new Task(task.getTitle(), task.getDescription(), task.getStatus(), task.getDuration(), task.getStartTime());
        copy.setId(task.getId());

        linkLast(copy);

        nodeMap.put(id, tail);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            historyList.add(current.getTask());
            current = current.getNext();
        }
        return historyList;
    }

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(task, oldTail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
        }
    }
}