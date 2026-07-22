package manage;
import exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 8, 0));
        final Task savedTask = taskManager.createTask(task);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void notConflictBetweenGivenIdAndGeneratedId() {
        Task givenTask = new Task("Given Id", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 9, 0));
        givenTask.setId(3);

        Task savedGiven = taskManager.createTask(givenTask);

        assertNotEquals(3, savedGiven.getId(), "Менеджер должен игнорировать внешний id и генерировать свой");

        Task generatedTask = taskManager.createTask(new Task("Generated Id",
                "Desc", Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2025, 1, 1, 10, 0)));

        assertNotEquals(savedGiven.getId(), generatedTask.getId(), "Id должны различаться");

        assertThrows(NotFoundException.class, () -> taskManager.getTaskById(3));
    }

    @Test
    public void taskDoesNotChangeWhenAddedToTheManager() {
        Task task = new Task("tasks.Task", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 11, 0));

        Task createdTask = taskManager.createTask(task);

        assertEquals("tasks.Task", createdTask.getTitle());
        assertEquals("Desc", createdTask.getDescription());
        Assertions.assertEquals(Status.NEW, createdTask.getStatus());
    }

    @Test
    public void createAndFindTasksById() {
        Task task = new Task("tasks.Task", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 12, 0));
        Task createTask = taskManager.createTask(task);

        Epic epic = new Epic("tasks.Epic", "Desc");
        Epic createEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("tasks.Subtask", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now(), createEpic.getId());
        Subtask createSubtask = taskManager.createSubtask(subtask);

        assertEquals(createTask, taskManager.getTaskById(createTask.getId()));
        assertEquals(createEpic, taskManager.getEpicById(createEpic.getId()));
        assertEquals(createSubtask, taskManager.getSubtaskById(createSubtask.getId()));
    }

    @Test
    public void createAndUpdatedTask() {
        Task task = taskManager.createTask(new Task("tasks.Task", "Desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 12, 0)));
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(Status.DONE, taskManager.getTaskById(task.getId()).getStatus());

    }

    @Test
    public void deleteTaskById() {
        Task task = taskManager.createTask(new Task("Title", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 13, 0)));

        taskManager.deleteTaskById(task.getId());

        assertThrows(NotFoundException.class,
                () -> taskManager.getTaskById(task.getId()),
                "После удаления getTaskById должен кидать NotFoundException");
    }

    @Test
    public void deleteEpicById() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("tasks.Subtask", "Desc",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 14, 0), epic.getId()));

        taskManager.deleteEpicById(epic.getId());

        assertThrows(NotFoundException.class,
                () -> taskManager.getEpicById(epic.getId()),
                "После удаления getEpicById должен кидать NotFoundException");

        assertThrows(NotFoundException.class,
                () -> taskManager.getSubtaskById(subtask.getId()),
                "Подзадачи эпика должны удаляться");
    }

    @Test
    public void returnAllTasks() {
        taskManager.createTask(new Task("Task1", "Desc1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 15, 0)));
        taskManager.createTask(new Task("Task2", "Desc2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 16, 0)));

        ArrayList<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Выводятся не все задачи");
    }

    @Test
    public void deletedSubtaskIdsInEpic() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Title", "Desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 17, 0), epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Title", "Desc", Status.DONE,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 18, 0), epic.getId()));

        taskManager.deleteTaskById(subtask1.getId());
        Epic updateEpic = taskManager.getEpicById(epic.getId());
        List<Integer> subtasksId = updateEpic.getSubtaskId();

        assertFalse(subtasksId.contains(subtask1.getId()));
        assertTrue(subtasksId.contains(subtask2.getId()));
    }

    @Test
    public void getHistoryReturnInCorrect() {
        Task task1 = taskManager.createTask(new Task("Title1", "Desc1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 19, 0)));
        Task task2 = taskManager.createTask(new Task("Title2", "Desc2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 20, 0)));
        Task task3 = taskManager.createTask(new Task("Title3", "Desc3", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 21, 0)));

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Вернулись не все добавленные задачи");
        assertEquals(1, history.get(0).getId(), "Task1 должен быть первым");
        assertEquals(2, history.get(1).getId(), "Task2 должен быть вторым");
        assertEquals(3, history.get(2).getId(), "Task3 должен быть третьим");
    }
}