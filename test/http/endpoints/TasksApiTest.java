package http.endpoints;

import http.BaseHttpTest;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TasksApiTest extends BaseHttpTest {
    @Test
    public void createTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "POST /tasks должен возвращать 201");

        List<Task> allTasksFromManager = manager.getAllTasks();
        assertNotNull(allTasksFromManager, "Задачи не возвращаются");
        assertEquals(1, allTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", allTasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void getAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Задача 2", "описание2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));

        HttpRequest request1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest getRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "GET /tasks должен возвращать 200");

        List<Task> allTasksFromManager = manager.getAllTasks();
        assertNotNull(getResponse.body());
        assertEquals(2, allTasksFromManager.size(), "Некорректное количество задач");
        assertTrue(getResponse.body().contains("Задача 1"));
        assertTrue(getResponse.body().contains("Задача 2"));
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        HttpRequest request1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "POST /tasks должен возвращать 201");

        int id = manager.getAllTasks().get(0).getId();
        HttpRequest getRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "GET /tasks должен возвращать 200");

        Task[] taskFromApi = gson.fromJson(getResponse.body(), Task[].class);
        assertNotNull(taskFromApi);

        Task fromApi = taskFromApi[0];
        assertEquals(id, fromApi.getId());
        assertEquals("Задача 1", fromApi.getTitle());
        assertEquals(Status.NEW, fromApi.getStatus());
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Задача 2", "описание2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));

        HttpRequest request1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "POST /tasks должен возвращать 201");

        HttpRequest request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "POST /tasks должен возвращать 201");

        List<Task> allBefore = manager.getAllTasks();
        assertEquals(2, allBefore.size());

        int idToDelete = allBefore.get(1).getId();

        HttpRequest deleteRequest = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks/" + idToDelete))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode());

        List<Task> allAfter = manager.getAllTasks();
        assertEquals(1, allAfter.size(), "После удаления должна остаться 1 задача");
        assertEquals("Задача 1", allAfter.get(0).getTitle());
    }
}