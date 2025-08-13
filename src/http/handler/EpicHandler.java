package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manage.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        String method = h.getRequestMethod();

        try {
            String[] segments = path.split("/");
            if (segments.length == 2) {
                switch (method) {
                    case "GET": {
                        List<Epic> allEpics = manager.getAllEpics();
                        sendText(h, gson.toJson(allEpics));
                        return;
                    }
                    case "POST": {
                        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (epic == null) {
                            sendError(h, "Неверный формат JSON");
                            return;
                        }
                        manager.createEpic(epic);
                        sendCreated(h);
                        return;
                    }
                    case "DELETE": {
                        manager.deleteAllEpics();
                        sendCreated(h);
                        return;
                    }
                }
            }
            if (segments.length == 3) {
                int id;
                try {
                    id = Integer.parseInt(segments[2]);
                } catch (NumberFormatException e) {
                    sendNotFound(h, "Некорректный id");
                    return;
                }
                switch (method) {
                    case "GET":
                        sendText(h, gson.toJson(manager.getEpicById(id)));
                        return;
                    case "DELETE":
                        manager.deleteEpicById(id);
                        sendCreated(h);
                        return;
                }
            }
            if (segments.length == 4 && "subtasks".equals(segments[3])) {
                int epicId;
                try {
                    epicId = Integer.parseInt(segments[2]);
                } catch (NotFoundException e) {
                    sendNotFound(h, "Некорректный id");
                    return;
                }
                if ("GET".equals(method)) {
                    sendText(h, gson.toJson(manager.getSubtasksByEpicId(epicId)));
                    return;
                }
            }
            h.sendResponseHeaders(405, -1);
            h.close();

        } catch (
                NotFoundException e) {
            sendNotFound(h, e.getMessage());
        } catch (
                OverlapException e) {
            sendHasOverlaps(h, e.getMessage());
        } catch (
                ManagerSaveException e) {
            sendError(h, "Ошибка сохранения: " + e.getMessage());
        } catch (Exception e) {
            sendError(h, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}