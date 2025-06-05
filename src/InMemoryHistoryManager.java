import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        Task historyTask = new Task(task.getTitle(), task.getDescription(), task.getStatus());
        historyTask.setId(task.getId());
        history.add(historyTask);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory(){
        return new ArrayList<>(history);
    }
}