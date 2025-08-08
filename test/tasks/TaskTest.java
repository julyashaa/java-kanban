package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void tasksWithSameIdShouldBeEquals() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание 2", Status.DONE,
                Duration.ofMinutes(15), LocalDateTime.now());
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }
}