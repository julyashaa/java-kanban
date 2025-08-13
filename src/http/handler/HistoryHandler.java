package http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manage.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        String method = h.getRequestMethod();

        try {
            if ("/history".equals(path) && "GET".equals(method)) {
                List<Task> history = manager.getHistory();
                sendText(h, gson.toJson(history));
                return;
            }

            h.sendResponseHeaders(405, -1);
            h.close();
        } catch (OverlapException e) {
            sendHasOverlaps(h, e.getMessage());
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            sendBadRequest(h, "Bad Request: " + e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(h, e.getMessage());
        } catch (Exception e) {
            sendError(h, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}