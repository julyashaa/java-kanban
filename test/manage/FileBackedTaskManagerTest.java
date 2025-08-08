package manage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;
    private FileBackedTaskManager manager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
            manager = new FileBackedTaskManager(file);
            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
    }

    @Test
    void saveAndLoadEmptyFile() {

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty(), "Должно быть 0 задач.");
        assertTrue(loaded.getAllEpics().isEmpty(), "Должно быть 0 эпиков.");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Должно быть 0 подзадач.");
        assertTrue(file.length() > 0, "Файл должен содержать хотя бы заголовок");
    }

    @Test
    void saveMultipleTasksToFile() throws IOException {
        manager.createTask(new Task("title1", "desc1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now()));
        manager.createTask(new Task("title2", "desc2", Status.DONE,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(1)));
        manager.createTask(new Task("title3", "desc3", Status.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(2)));

        String content = Files.readString(file.toPath());

        assertTrue(file.length() > 0, "Файл должен содержать сохраненные задачи");

        assertTrue(content.contains("title1"), "Файл должен содержать название первой задачи");
        assertTrue(content.contains("desc1"), "Файл должен содержать описание первой задачи");
        assertTrue(content.contains("NEW"), "Файл должен содержать статус первой задачи");

        assertTrue(content.contains("title2"), "Файл должен содержать название второй задачи");
        assertTrue(content.contains("desc2"), "Файл должен содержать описание второй задачи");
        assertTrue(content.contains("DONE"), "Файл должен содержать статус второй задачи");

        assertTrue(content.contains("title3"), "Файл должен содержать название третьей задачи");
        assertTrue(content.contains("desc3"), "Файл должен содержать описание третьей задачи");
        assertTrue(content.contains("IN_PROGRESS"), "Файл должен содержать статус третьей задачи");
    }

    @Test
    void loadMultipleTasksFromFile() {
        manager.createTask(new Task("title1", "desc1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now()));
        manager.createTask(new Task("title2", "desc2", Status.DONE,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(1)));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = loaded.getAllTasks();

        assertEquals(2, tasks.size(), "Ожидалось 2 загруженные задачи");
        assertEquals("title1", tasks.get(0).getTitle());
        assertEquals("title2", tasks.get(1).getTitle());
        assertEquals("desc1", tasks.get(0).getDescription());
        assertEquals("desc2", tasks.get(1).getDescription());
        assertEquals(Status.NEW, tasks.get(0).getStatus());
        assertEquals(Status.DONE, tasks.get(1).getStatus());
    }

    @Test
    void saveAndLoadEpicWithSubtasks() {
        Epic epic = manager.createEpic(new Epic("epic1", "desc1"));
        Subtask s1 = manager.createSubtask(new Subtask("sub1", "sub desc 1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), epic.getId()));
        Subtask s2 = manager.createSubtask(new Subtask("sub2", "sub desc 2", Status.DONE,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(1), epic.getId()));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Epic loadedEpic = loaded.getEpicById(epic.getId());
        List<Subtask> loadedSub = loaded.getSubtasksByEpicId(epic.getId());

        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(2, loadedSub.size());
    }

    @Test
    public void savEndLoadDoesNotThrowOnWalidFile() {
        assertDoesNotThrow(() -> {
            manager.save();
            FileBackedTaskManager.loadFromFile(file);
        }, "При корректной работе не должно быть исключений");
    }
}