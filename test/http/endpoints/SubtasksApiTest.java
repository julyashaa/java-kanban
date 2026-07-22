package http.endpoints;

import http.BaseHttpTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksApiTest extends BaseHttpTest {

    int epicId1;
    int subtaskId;

    @BeforeEach
    void subtasksBeforeEach() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        List<Epic> all = manager.getAllEpics();
        assertEquals(1, all.size());

        epicId1 = all.get(0).getId();

        Subtask subtask1 = new Subtask("S1", "sub1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(1), epicId1);

        HttpRequest requestSubtask = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();

        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode(), "POST /tasks должен возвращать 201");

        subtaskId = manager.getAllSubtasks().get(0).getId();

    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        HttpRequest getById = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();
        HttpResponse<String> respById = client.send(getById, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, respById.statusCode());
        assertTrue(respById.body().contains("S1"));
    }

    @Test
    public void createAnotherSubtask() throws IOException, InterruptedException {
        Subtask subtask2 = new Subtask("S2", "sub2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(3), epicId1);

        HttpRequest requestSubtask2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();

        HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask2.statusCode(), "POST /tasks должен возвращать 201");
        assertEquals(2, manager.getAllSubtasks().size());
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        HttpRequest requestSubtask = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }
}