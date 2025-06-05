import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach(){
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void taskSavedPreviousVersion(){
        Task task = new Task("History", "Desc", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        task.setStatus(Status.DONE);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task newTask = history.get(0);
        assertEquals("History", newTask.getTitle());
        assertEquals("Desc", newTask.getDescription());
        assertEquals(Status.NEW, newTask.getStatus());
        assertEquals(1, newTask.getId());
    }

    @Test
    public void add() {
        Task task = new Task("History", "Desc", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}
