package http.endpoints;

import http.BaseHttpTest;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicsApiTest extends BaseHttpTest {

    @Test
    public void createEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        List<Epic> all = manager.getAllEpics();
        assertEquals(1, all.size());
        assertNull(all.getFirst().getStartTime());
        assertNull(all.getFirst().getEndTime());
        assertEquals(Duration.ZERO, all.getFirst().getDuration());
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "POST /tasks должен возвращать 201");

        List<Epic> all = manager.getAllEpics();
        assertEquals(2, all.size());

        HttpRequest getAll = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "GET /tasks должен возвращать 200");

        ArrayList<Epic> allEpicsFromManager = manager.getAllEpics();
        assertNotNull(getResponse.body());
        assertEquals(2, allEpicsFromManager.size(), "Некорректное количество задач");
        assertTrue(getResponse.body().contains("Эпик1"));
        assertTrue(getResponse.body().contains("Эпик2"));
    }


    @Test
    public void getEpicsById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");


        List<Epic> all = manager.getAllEpics();
        assertEquals(1, all.size());

        int idToKeep1 = all.getFirst().getId();


        HttpRequest getById = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics/" + idToKeep1))
                .GET()
                .build();

        HttpResponse<String> getByIdResponse = client.send(getById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getByIdResponse.statusCode(), "GET /tasks должен возвращать 200");

        ArrayList<Epic> allEpicsFromManager = manager.getAllEpics();
        assertNotNull(getByIdResponse.body());
        assertEquals(1, allEpicsFromManager.size(), "Некорректное количество задач");
        assertTrue(getByIdResponse.body().contains("Эпик1"));
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");


        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest deleteRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode(), "GET /tasks должен возвращать 200");
    }

    @Test
    public void getSubtaskByEpicsId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic2 = new Epic("Эпик2", "Описание2");

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "POST /tasks должен возвращать 201");

        List<Epic> all = manager.getAllEpics();
        assertEquals(2, all.size());

        int epicId1 = all.get(0).getId();
        int epicId2 = all.get(1).getId();

        Subtask subtask1 = new Subtask("S1", "sub1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(1), epicId1);
        Subtask subtask2 = new Subtask("S2", "sub2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(2), epicId1);
        Subtask subtask3 = new Subtask("Other", "other", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(3), epicId2);

        HttpRequest requestSub1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();

        HttpResponse<String> responseSub1 = client.send(requestSub1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSub1.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest requestSub2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();

        HttpResponse<String> responseSub2 = client.send(requestSub2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSub2.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest requestSub3 = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3)))
                .build();

        HttpResponse<String> responseSub3 = client.send(requestSub3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSub3.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest getSubtasksById = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics/" + epicId1 + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> getSubtasksByIdResponse = client.send(getSubtasksById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubtasksByIdResponse.statusCode(), "GET /tasks должен возвращать 200");

        String body = getSubtasksByIdResponse.body();
        assertTrue(body.contains("S1"));
        assertTrue(body.contains("S2"));
        assertFalse(body.contains("S3"));
    }
}