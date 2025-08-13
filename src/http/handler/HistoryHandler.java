package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        } catch (Exception e) {
            sendError(h, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}