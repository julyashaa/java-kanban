package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskId;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtask(int id) {
        if (id != getId()) {
        subtaskId.add(id);
        }
    }

    public void removeSubtask(int id) {
        subtaskId.remove((Integer) id);
    }

    public void clearSubtasks() {
        subtaskId.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString(){
        return super.toString() + ", subtaskId=" + subtaskId;
    }
}