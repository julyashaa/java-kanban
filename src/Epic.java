import java.util.ArrayList;
import java.util.Objects;

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
        subtaskId.add(id);
    }

    public void removeSubtask(int id) {
        subtaskId.remove((Integer) id);
    }

    public void clearSubtasks() {
        subtaskId.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Epic epic = (Epic) obj;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31;
        if (subtaskId != null) {
            hash = hash + subtaskId.hashCode();
        }
        return hash;
    }

    @Override
    public String toString(){
        return super.toString() + ", subtaskId=" + subtaskId;
    }
}
