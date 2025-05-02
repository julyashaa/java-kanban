import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private int id;
    private Status status;

    public Task(String title, String description, Status status){
        this.title = title;
        this.description = description;
        this.status = status;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return (id == task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode(){
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
    public String toString(){
        return id + ". " + title + " [" + status + "]";
    }
}