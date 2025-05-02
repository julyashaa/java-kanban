import java.util.Objects;

public class Subtask extends Task{
    private final int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Subtask subtask = (Subtask) obj;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31;
        hash = hash + epicId;
        return hash;
    }

    @Override
    public String toString(){
        return super.toString() + " (epicId=" + epicId + ")";
    }
}