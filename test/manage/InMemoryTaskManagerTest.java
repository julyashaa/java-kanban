package manage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void updateEpicStatus() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.NEW,
                Duration.ZERO, null, epic.getId()));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.DONE,
                Duration.ZERO, null, epic.getId()));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика с разными статусами подзадач должен быть IN_PROGRESS");
    }

    @Test
    public void epicStatusAllNew() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.NEW,
                Duration.ZERO, null, epic.getId()));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.NEW,
                Duration.ZERO, null, epic.getId()));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(Status.NEW, updatedEpic.getStatus(),
                "Все подзадачи NEW, эпик тоже должен быть NEW");
    }

    @Test
    public void epicStatusAllDone() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.DONE,
                Duration.ZERO, null, epic.getId()));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.DONE,
                Duration.ZERO, null, epic.getId()));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(Status.DONE, updatedEpic.getStatus(),
                "Все подзадачи DONE, эпик тоже должен быть DONE");
    }

    @Test
    public void epicStatusAllInProgress() {
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.IN_PROGRESS,
                Duration.ZERO, null, epic.getId()));
        taskManager.createSubtask(new Subtask("Title", "Desc", Status.IN_PROGRESS,
                Duration.ZERO, null, epic.getId()));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(),
                "Все подзадачи IN_PROGRESS, эпик тоже должен быть IN_PROGRESS");
    }

    @Test
    public void epicDurationIsSumOfSubtasksDuration() {
        Epic epic = taskManager.createEpic(new Epic("Эпик", "Описание"));

        taskManager.createSubtask(new Subtask("Подзадача 1", "Описание 1", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 1, 1, 8, 0), epic.getId()));
        taskManager.createSubtask(new Subtask("Подзадача 2", "Описание 2", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 9, 0), epic.getId()));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Duration.ofMinutes(45), updatedEpic.getDuration(), "Длительность эпика — сумма подзадач");
        assertEquals(LocalDateTime.of(2024, 1, 1, 8, 0), updatedEpic.getStartTime(),
                "startTime для Эпика должен быть минимальным стартом среди подзадач");
        assertEquals(LocalDateTime.of(2024, 1, 1, 9, 30), updatedEpic.getEndTime(),
                "endTime для Эпика должен быть максимальным концом среди подзадач");
    }

    @Test
    public void canNotCreateOverlappingTask() {
        taskManager.createTask(new Task("Title", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 10, 0)));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(new Task("Title2", "Desc2", Status.NEW,
                    Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 10, 15)));
        });
        assertTrue(exception.getMessage().contains("пересекается"),
                "Должно быть исключение о пересечении");
    }

    @Test
    public void canCreateNONOverlappingTask() {
        taskManager.createTask(new Task("Title", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 10, 0)));
        assertDoesNotThrow(() -> {
            taskManager.createTask(new Task("Title", "Desc", Status.NEW,
                    Duration.ofMinutes(10), LocalDateTime.of(2024, 1, 1, 12, 0)));
        });
    }
    }