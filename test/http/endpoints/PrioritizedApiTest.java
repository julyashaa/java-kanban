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

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedApiTest extends BaseHttpTest {
    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        Task early = new Task("Early", "t", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now().plusHours(1));

        HttpRequest requestEarly = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(early)))
                .build();
        HttpResponse<String> earlyTaskResp = client.send(requestEarly, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, earlyTaskResp.statusCode());

        Epic epic = new Epic("EP", "epic");
        HttpRequest requestEpic = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseEpic.statusCode(), "POST /tasks должен возвращать 201");

        int epicId = manager.getAllEpics().get(0).getId();

        Subtask mid = new Subtask("Middle Sub", "subtask desc", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(2), epicId);
        HttpRequest requestSubtask = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(mid)))
                .build();

        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode(), "POST /tasks должен возвращать 201");

        Task late = new Task("Late", "task late desc", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusHours(3));
        HttpRequest requestLate = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(late)))
                .build();
        HttpResponse<String> lateTaskResp = client.send(requestLate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, lateTaskResp.statusCode());

        Task noTime = new Task("NoTime", "nt", Status.NEW,
                Duration.ofMinutes(5), null);
        HttpRequest requestNoTime = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(noTime)))
                .build();
        HttpResponse<String> noTimeTaskResp = client.send(requestNoTime, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, noTimeTaskResp.statusCode());

        HttpRequest prioritizedReq = HttpRequest.newBuilder(URI.create("http://localhost:8080/prioritized"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> prioritizedResp = client.send(prioritizedReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, prioritizedResp.statusCode());

        String body = prioritizedResp.body();
        assertTrue(body.contains("Early"));
        assertTrue(body.contains("Middle Sub"));
        assertTrue(body.contains("Late"));
        assertFalse(body.contains("NoTime"), "Элементы без startTime не должны входить в приоритезированный список");

        int posEarly = body.indexOf("Early");
        int posMid = body.indexOf("Middle Sub");
        int posLate = body.indexOf("Late");
        assertTrue(posEarly < posMid, "Ожидалось: Early раньше MidSub");
        assertTrue(posMid < posLate, "Ожидалось: MidSub раньше Late");
    }
}