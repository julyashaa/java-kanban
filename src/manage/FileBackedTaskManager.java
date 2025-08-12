package manage;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,duration,startTime,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + file.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                Task task = fromString(line);

                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    fileManager.createEpic(epic);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    fileManager.createSubtask(subtask);
                } else {
                    fileManager.createTask(task);
                }

                if (task.getId() >= fileManager.getNextId()) {
                    fileManager.setNextId(task.getId() + 1);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + file.getName(), e);
        }

        return fileManager;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");

        if (task instanceof Subtask) {
            sb.append(TaskType.SUBTASK).append(",");
        } else if (task instanceof Epic) {
            sb.append(TaskType.EPIC).append(",");
        } else {
            sb.append(TaskType.TASK).append(",");
        }

        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        long durationInMinutes = 0;
        if (task.getDuration() != null) {
            durationInMinutes = task.getDuration().toMinutes();
        }

        String startTimeStr = "";
        if (task.getStartTime() != null) {
            startTimeStr = task.getStartTime().toString();
        }

        sb.append(durationInMinutes).append(",");
        sb.append(startTimeStr);

        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            sb.append(",").append(subtask.getEpicId());
        }

        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String title = values[2];
        Status status = Status.valueOf(values[3]);
        String desc = values[4];

        Duration duration;
        if (values[5].isEmpty()) {
            duration = Duration.ZERO;
        } else {
            duration = Duration.ofMinutes(Long.parseLong(values[5]));
        }

        LocalDateTime startTime;
        if (values[6].isEmpty()) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(values[6]);
        }

        switch (type) {
            case TASK:
                Task task = new Task(title, desc, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(title, desc);
                epic.setId(id);
                epic.setStatus(status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(values[7]);
                Subtask subtask = new Subtask(title, desc, status, duration, startTime, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static void main(String[] args) {
        File file = new File("save.csv");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

        Task task1 = fileManager.createTask(new Task("title1", "desc1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now()));
        Task task2 = fileManager.createTask(new Task("title2", "desc2", Status.DONE,
                Duration.ofMinutes(60), LocalDateTime.now().plusHours(1)));

        Epic epic = fileManager.createEpic(new Epic("title2", "desc2"));

        Subtask subtask1 = fileManager.createSubtask(new Subtask("title1", "desc1", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now().plusHours(2), epic.getId()));
        Subtask subtask2 = fileManager.createSubtask(new Subtask("title2", "desc2", Status.DONE,
                Duration.ofMinutes(40), LocalDateTime.now().plusHours(3), epic.getId()));

        System.out.println("Исходный менеджер:");
        System.out.println("Задачи: " + fileManager.getAllTasks());
        System.out.println("Эпики: " + fileManager.getAllEpics());
        System.out.println("Подзадачи: " + fileManager.getAllSubtasks());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Загруженный менеджер:");
        System.out.println("Задачи: " + loaded.getAllTasks());
        System.out.println("Эпики: " + loaded.getAllEpics());
        System.out.println("Подзадачи: " + loaded.getAllSubtasks());
    }
}