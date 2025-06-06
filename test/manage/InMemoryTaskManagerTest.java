package manage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach(){
        taskManager = Manager.getDefault();
    }

    @Test
    public void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final Task savedTask = taskManager.createTask(task);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void createAndFindTasksById(){
        Task task = new Task("tasks.Task", "Desc", Status.NEW);
        Task createTask = taskManager.createTask(task);

        Epic epic = new Epic("tasks.Epic", "Desc");
        Epic createEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("tasks.Subtask", "Desc", Status.NEW, createEpic.getId());
        Subtask createSubtask = taskManager.createSubtask(subtask);

        assertEquals(createTask, taskManager.getTaskById(createTask.getId()));
        assertEquals(createEpic, taskManager.getEpicById(createEpic.getId()));
        assertEquals(createSubtask, taskManager.getSubtaskById(createSubtask.getId()));
    }

    @Test
    public void notConflictBetweenGivenIdAndGeneratedId(){
        Task givenTask = new Task("Given Id", "Desc", Status.NEW);
        givenTask.setId(3);

        taskManager.updateTask(givenTask);

        Task generatedTask = taskManager.createTask(new Task("Generated Id",
                "Desc", Status.NEW));

        assertNotEquals(givenTask, generatedTask, "Id должны различаться");
        assertEquals(givenTask, taskManager.getTaskById(3));
        assertEquals(generatedTask, taskManager.getTaskById(generatedTask.getId()));
    }

    @Test
    public void taskDoesNotChangeWhenAddedToTheManager(){
        Task task = new Task("tasks.Task", "Desc", Status.NEW);

        Task createdTask = taskManager.createTask(task);

        assertEquals("tasks.Task", createdTask.getTitle());
        assertEquals("Desc", createdTask.getDescription());
        Assertions.assertEquals(Status.NEW, createdTask.getStatus());
    }

    @Test
    public void updateEpicStatus(){
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        Subtask subtask1 = new Subtask("Title", "Desc", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Title", "Desc", Status.DONE, epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика с разными статусами подзадач должен быть IN_PROGRESS");
    }

    @Test
    public void deleteTaskById(){
        Task task = taskManager.createTask(new Task("Title", "Desc", Status.NEW));

        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void deleteEpicById(){
        Epic epic = taskManager.createEpic(new Epic("tasks.Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("tasks.Subtask", "Desc",
                Status.NEW, epic.getId()));

        taskManager.deleteEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не был удален");
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача не была удалена");
    }

    @Test
    public void returnAllTasks(){
        taskManager.createTask(new Task("Task1", "Desc1", Status.NEW));
        taskManager.createTask(new Task("Task2", "Desc2", Status.NEW));

        ArrayList<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Выводятся не все задачи");
    }
}