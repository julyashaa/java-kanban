package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskId;
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, null, null);
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
    public Duration getDuration() {
        return duration;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        return super.toString() + ", subtaskId=" + subtaskId;
    }
}