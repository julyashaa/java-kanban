package app;

import manage.HistoryManager;
import manage.InMemoryHistoryManager;
import manage.Manager;
import manage.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Manager.getDefault();

        Task task1 = inMemoryTaskManager.createTask(new Task("Записаться к врачу", "К дерматологу",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        Task task2 = inMemoryTaskManager.createTask(new Task("Написать проект", "К пятнице",
                Status.DONE, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1)));

        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд", "Во вторник"));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Собрать коробки",
                "Купить и упаковать", Status.NEW, Duration.ofMinutes(20),
                LocalDateTime.now().plusHours(2), epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Собрать вещи",
                "По категориям", Status.NEW, Duration.ofMinutes(25),
                LocalDateTime.now().plusHours(3), epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Поход в магазин",
                "В пятерочку"));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить молоко",
                "Простоквашино", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.now().plusHours(4), epic2.getId()));

        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());

        System.out.println("После создания задач:");
        printAllTasks(inMemoryTaskManager);
        System.out.println();

        subtask1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        subtask3.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask3);

        System.out.println("После изменения статуса:");
        printAllTasks(inMemoryTaskManager);
        System.out.println();

        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteEpicById(epic2.getId());

        System.out.println("После удаления:");
        printAllTasks(inMemoryTaskManager);
        System.out.println();

        System.out.println("Проверка manage.HistoryManager.manage.InMemoryHistoryManager:");
        HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание " + i, Status.NEW,
                    Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(i * 10));
            task.setId(i);
            inMemoryHistoryManager.add(task);
        }
        System.out.println("История последних 10 задач:");
        for (Task task : inMemoryHistoryManager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}