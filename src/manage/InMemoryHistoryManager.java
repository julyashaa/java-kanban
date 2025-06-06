package manage;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        Task historyTask = new Task(task.getTitle(), task.getDescription(), task.getStatus());
        historyTask.setId(task.getId());
        history.add(historyTask);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory(){
        return new ArrayList<>(history);
    }
}