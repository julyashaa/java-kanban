package comparator;

import tasks.Task;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        int cmp = o1.getStartTime().compareTo(o2.getStartTime());
        if (cmp == 0) {
            return Integer.compare(o1.getId(), o2.getId());
        }
        return cmp;
    }
}