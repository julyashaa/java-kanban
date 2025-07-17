package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

    @Test
    public void subtasksWithSameIdShouldBeEquals() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 2", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.DONE, 1);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    public void subtaskDoNotBeItsSameEpic() {
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 2);
        subtask.setId(1);
        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Подзадача не может быть своим же эпиком");
    }
}