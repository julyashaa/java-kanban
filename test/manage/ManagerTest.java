package manage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagerTest {
    @Test
    public void returnInitializedTaskManager() {
        TaskManager taskManager = Manager.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    public void returnInitializedHistoryManager() {
        HistoryManager historyManager = Manager.getDefaultHistory();
        assertNotNull(historyManager);
    }
}