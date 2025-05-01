public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task("Записаться к врачу", "К дерматологу",
                Status.NEW));
        Task task2 = taskManager.createTask(new Task("Написать проект", "К пятнице",
                Status.DONE));

        Epic epic1 = taskManager.createEpic(new Epic("Переезд", "Во вторник"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Собрать коробки",
                "Купить и упаковать", Status.NEW, epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Собрать вещи",
                "По категориям", Status.NEW, epic1.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Поход в магазин",
                "В пятерочку"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Купить молоко",
                "Простоквашино", Status.NEW, epic2.getId()));

        System.out.println("Задачи:" + taskManager.getAllTasks());
        System.out.println("Эпики:" + taskManager.getAllEpics());
        System.out.println("Подзадачи:" + taskManager.getAllSubtasks());

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("Задачи после изменения статуса:" + taskManager.getAllTasks());
        System.out.println("Эпики после изменения статуса:" + taskManager.getAllEpics());
        System.out.println("Подзадачи после изменения статуса:" + taskManager.getAllSubtasks());

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println("Задачи после удаления:" + taskManager.getAllTasks());
        System.out.println("Эпики после удаления:" + taskManager.getAllEpics());
        System.out.println("Подзадачи после удаления:" + taskManager.getAllSubtasks());
    }
}
