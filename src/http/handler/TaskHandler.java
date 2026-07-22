package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manage.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        String method = h.getRequestMethod();

        try {
            if ("/tasks".equals(path)) {
                switch (method) {
                    case "GET": {
                        List<Task> allTasks = manager.getAllTasks();
                        sendText(h, gson.toJson(allTasks));
                        return;
                    }
                    case "POST": {
                        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(body, Task.class);
                        if (task == null) {
                            sendError(h, "Неверный формат JSON");
                            return;
                        }
                        if (task.getId() == 0) {
                            manager.createTask(task);
                        } else {
                            manager.updateTask(task);
                        }
                        sendCreated(h);
                        return;
                    }
                    case "DELETE": {
                        manager.deleteAllTasks();
                        sendCreated(h);
                        return;
                    }
                }
            }
            if (path.startsWith("/tasks/")) {
                int id;
                try {
                    id = Integer.parseInt(path.substring("/tasks/".length()));
                } catch (NumberFormatException e) {
                    sendNotFound(h, "Некорректный id");
                    return;
                }

                switch (method) {
                    case "GET": {
                        Task task = manager.getTaskById(id);
                        sendText(h, gson.toJson(task));
                        return;
                    }
                    case "DELETE": {
                        manager.deleteTaskById(id);
                        sendCreated(h);
                        return;
                    }
                }
            }
            h.sendResponseHeaders(405, -1);
            h.close();

        } catch (NotFoundException e) {
            sendNotFound(h, e.getMessage());
        } catch (OverlapException e) {
            sendHasOverlaps(h, e.getMessage());
        } catch (ManagerSaveException e) {
            sendError(h, "Ошибка сохранения: " + e.getMessage());
        } catch (Exception e) {
            sendError(h, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}