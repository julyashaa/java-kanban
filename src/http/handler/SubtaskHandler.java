package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manage.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        String method = h.getRequestMethod();

        try {
            if ("/subtasks".equals(path)) {
                switch (method) {
                    case "GET": {
                        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
                        sendText(h, gson.toJson(allSubtasks));
                        return;
                    }
                    case "POST": {
                        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        if (subtask == null) {
                            sendError(h, "Неверный формат JSON");
                            return;
                        }
                        if (subtask.getId() == 0) {
                            manager.createSubtask(subtask);
                        } else {
                            manager.updateSubtask(subtask);
                        }
                        sendCreated(h);
                        return;
                    }
                    case "DELETE": {
                        manager.deleteAllSubtasks();
                        sendCreated(h);
                        return;
                    }
                }
            }

            if (path.startsWith("/subtasks/")) {
                int id;
                try {
                    id = Integer.parseInt(path.substring("/subtasks/".length()));
                } catch (NumberFormatException e) {
                    sendNotFound(h, "Некорректный id");
                    return;
                }

                switch (method) {
                    case "GET": {
                        Subtask subtask = manager.getSubtaskById(id);
                        sendText(h, gson.toJson(subtask));
                        return;
                    }
                    case "DELETE": {
                        manager.deleteSubtaskById(id);
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