package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task() {
    }

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash + id;
        hash = hash * 31;
        if (title != null) {
            hash = title.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = description.hashCode();
        }
        hash = hash * 31;
        if (status != null) {
            hash = status.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        Duration dr = getDuration();
        LocalDateTime st = getStartTime();
        LocalDateTime end = getEndTime();

        String durationStr;
        if (dr != null) {
            durationStr = dr.toString();
        } else {
            durationStr = "длительность не задана";
        }

        String startTimeStr;
        if (st != null) {
            startTimeStr = st.format(formatter);
        } else {
            startTimeStr = "дата и время не заданы";
        }

        String endTimeStr;
        if (end != null) {
            endTimeStr = end.format(formatter);
        } else {
            endTimeStr = "длительность не задана";
        }

        return String.format("%d. %s [%s] %s %s - %s", id, title, status, durationStr, startTimeStr,
                endTimeStr);
    }
}