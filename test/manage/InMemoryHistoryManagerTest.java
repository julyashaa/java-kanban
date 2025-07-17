package manage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void taskSavedPreviousVersion() {
        Task task = new Task("History", "Desc", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        task.setStatus(Status.DONE);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task newTask = history.getFirst();
        assertEquals("History", newTask.getTitle());
        assertEquals("Desc", newTask.getDescription());
        Assertions.assertEquals(Status.NEW, newTask.getStatus());
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

    @Test
    public void taskIsNotDuplicated() {
        Task task = new Task("History", "Desc", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Повторное добавление не должно дублировать задачу.");
    }

    @Test
    public void addingAgainMovesTheTaskToTheEnd() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(task2, history.get(0), "Task2 должен остаться первым");
        assertEquals(task1, history.get(1), "Task1 должен быть перемещён в конец");
    }

    @Test
    public void removeTaskByIdInHistory() {
        Task task = new Task("History", "Desc", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void removeTaskFromBeginning() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Title3", "Desc3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "После удаления должно остаться две задачи.");
        assertEquals(2, history.getFirst().getId(), "После удаления Task2 должен остаться первым");
    }

    @Test
    public void removeTaskFromEnd() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Title3", "Desc3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "После удаления должно остаться две задачи.");
        assertEquals(2, history.getLast().getId(), "В конце списка должен быть task2.");
    }

    @Test
    public void removeTaskFromMiddle() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Title3", "Desc3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "После удаления должно остаться две задачи.");
        assertEquals(1, history.getFirst().getId(), "После удаления Task1 должен остаться первым");
        assertEquals(3, history.getLast().getId(), "В конце списка должен быть task2.");
    }

    @Test
    public void getHistoryReturnInCorrect() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Title3", "Desc3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Вернулись не все добавленные задачи");
        assertEquals(1, history.get(0).getId(), "Task1 должен быть первым");
        assertEquals(2, history.get(1).getId(), "Task2 должен быть вторым");
        assertEquals(3, history.get(2).getId(), "Task3 должен быть третьим");
    }

    @Test
    public void historyDoesNotContainsNull() {
        Task task1 = new Task("Title1", "Desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(null), "История не должна содержать null");
    }
}