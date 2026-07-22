package manage;

import comparator.TaskStartTimeComparator;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;
    private final HistoryManager history = Manager.getDefaultHistory();

    @Override
    public Task createTask(Task task) {
        List<Task> overlaps = findOverlaps(task);
        if (!overlaps.isEmpty()) {
            throw new OverlapException("Время задачи пересекается с задачами: " + overlaps);
        }
        int id = nextId++;
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = nextId++;
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Epic с id = " + subtask.getEpicId() + "не найден");
        }

        List<Task> overlaps = findOverlaps(subtask);
        if (!overlaps.isEmpty()) {
            throw new OverlapException("Время задачи пересекается с задачами: " + overlaps);
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с id = " + id + "не найдена");
        }
        addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Epic с id = " + id + "не найден");
        }
        addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id = " + id + "не найдена");
        }
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Epic с id = " + epicId + "не найдена");
        }

        return epic.getSubtaskId().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history.getHistory());
    }

    private void addToHistory(Task task) {
        history.add(task);
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            history.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());

            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            subtasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id = " + id + " не найдена");
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        history.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Epic с id = " + id + " не найден");
        }

        for (int subtaskId : epic.getSubtaskId()) {
            subtasks.remove(subtaskId);
            history.remove(subtaskId);
        }
        history.remove(id);

    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            history.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskId()) {
                history.remove(subtaskId);
            }
            history.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            history.remove(id);
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        subtasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Задача с id = " + task.getId() + "не найдена");
        }
        List<Task> overlaps = findOverlaps(task);
        if (!overlaps.isEmpty()) {
            throw new OverlapException("Время задачи пересекается с задачами: " + overlaps);
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Подзадача с id = " + subtask.getId() + " не найдена");
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Эпик с id = " + subtask.getEpicId() + " не найден");
        }

        List<Task> overlaps = findOverlaps(subtask);
        if (!overlaps.isEmpty()) {
            throw new OverlapException("Время задачи пересекается с задачами: " + overlaps);
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTime(epics.get(subtask.getEpicId()));
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            Duration subDuration = subtask.getDuration();
            totalDuration = totalDuration.plus(subDuration);

            LocalDateTime start = subtask.getStartTime();
            if (start != null) {
                if (earliestStart == null || start.isBefore(earliestStart)) {
                    earliestStart = start;
                }

                LocalDateTime end = subtask.getEndTime();
                if (latestEnd == null || end.isAfter(latestEnd)) {
                    latestEnd = end;
                }

            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskId();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (int id : subtaskIds) {
            Status status = subtasks.get(id).getStatus();
            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public List<Task> getPrioritizedTasks() {
        TaskStartTimeComparator comparator = new TaskStartTimeComparator();
        Set<Task> prioritizedTasks = new TreeSet<>(comparator);

        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
        return new ArrayList<>(prioritizedTasks);
    }

    public static boolean isOverlap(Task t1, Task t2) {
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime();
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime();

        if (start1 == null || start2 == null || end1 == null || end2 == null) {
            return false;
        }

        if (end1.isEqual(start2) || end1.isBefore(start2)) return false;
        return !end2.isEqual(start1) && !end2.isBefore(start1);
    }

    private List<Task> findOverlaps(Task task) {
        return getPrioritizedTasks().stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> isOverlap(task, t))
                .collect(Collectors.toList());
    }
}