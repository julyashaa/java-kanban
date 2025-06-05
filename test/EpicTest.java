import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void tasksWithSameIdShouldBeEquals(){
        Epic epic1 = new Epic("Эпик 1", "Описание 2");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        epic1.setId(2);
        epic2.setId(2);
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    public void epicShouldNotAddSelfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(1);
        epic.addSubtask(epic.getId());
        assertFalse(epic.getSubtaskId().contains(epic.getId()),
                "Эпик не должен содержать самого себя в списке подзадач");
    }

    @Test
    public void clearAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.clearSubtasks();
        assertTrue(epic.getSubtaskId().isEmpty(), "Подзадачи должны быть удалены");
    }

    @Test
    public void clearSubtasksById() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.removeSubtask(1);
        assertFalse(epic.getSubtaskId().contains(1), "Подзадача должна быть удалена");
        assertEquals(1, epic.getSubtaskId().size());
    }
}