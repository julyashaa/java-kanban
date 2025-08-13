package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskId = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic() {
        super();
        subtaskId = new ArrayList<>();
        setDuration(Duration.ZERO);
    }

    public Epic(String title, String description) {
        super(title, description, Status.NEW, Duration.ZERO, null);

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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
    public String toString() {
        return String.format("%s , subtaskId = %s", super.toString(), subtaskId);
    }
}
