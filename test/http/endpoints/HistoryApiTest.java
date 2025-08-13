package http.endpoints;

import http.BaseHttpTest;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryApiTest extends BaseHttpTest {

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");
        int taskId = manager.getAllTasks().get(0).getId();

        Epic epic1 = new Epic("Эпик1", "Описание1");

        HttpRequest requestEpic = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseEpic.statusCode(), "POST /tasks должен возвращать 201");
        int epicId = manager.getAllEpics().get(0).getId();

        Subtask subtask1 = new Subtask("S1", "sub1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(3), epicId);

        HttpRequest requestSubtask = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();

        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode(), "POST /tasks должен возвращать 201");

        int subId = manager.getAllSubtasks().get(0).getId();


        assertEquals(200, client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks/" + taskId)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());

        assertEquals(200, client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:8080/epics/" + epicId)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());

        assertEquals(200, client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks/" + subId)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());

        assertEquals(200, client.send( // повторный просмотр задачи — должен уйти в конец истории
                HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks/" + taskId)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());

        HttpRequest historyReq = HttpRequest.newBuilder(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> httpResponse = client.send(historyReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        String body = httpResponse.body();

        assertTrue(body.contains("Задача 1"));
        assertTrue(body.contains("Эпик1"));
        assertTrue(body.contains("S1"));

        assertEquals(body.indexOf("Задача 1"), body.lastIndexOf("Задача 1"));
        assertEquals(body.indexOf("Эпик1"), body.lastIndexOf("Эпик1"));
        assertEquals(body.indexOf("S1"), body.lastIndexOf("S1"));

        int positionEpic = body.indexOf("Эпик1");
        int positionSubtask = body.indexOf("S1");
        int positionTask = body.indexOf("Задача 1");
        assertTrue(positionEpic < positionSubtask, "Ожидалось: epic раньше subtask");
        assertTrue(positionSubtask < positionTask, "Ожидалось: subtask раньше task");

        List<Task> hist = manager.getHistory();
        assertEquals(3, hist.size(), "В истории должно быть 3 уникальных элемента");
        assertEquals(taskId, hist.get(2).getId(), "Повторно просмотренная задача должна быть в конце");
    }
}