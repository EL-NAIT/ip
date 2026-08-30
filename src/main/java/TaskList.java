import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the tasks of one HappyBot session and the operations that change them.
 *
 * <p>A task is named from the outside by its number in the list, counted from one as it is
 * shown to the user. Turning that number into a position, and refusing a number that belongs
 * to no task, happens here, so no other class works out positions for itself.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the specified tasks.
     *
     * <p>The tasks are copied, so the list stays in charge of its own contents even when the
     * caller keeps a reference to the list it passed in.
     *
     * @param tasks The tasks to start with, usually the ones read from the data file.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list holds no tasks.
     *
     * @return True if there is nothing in the list.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in list order, for displaying or saving them.
     *
     * @return A read-only view of the tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param taskToAdd The task to add.
     */
    public void add(Task taskToAdd) {
        tasks.add(taskToAdd);
    }

    /**
     * Returns the task holding the specified number.
     *
     * @param taskNumber The task number, counted from one.
     * @return The task holding that number.
     * @throws HappyBotException If no task holds that number.
     */
    public Task get(int taskNumber) throws HappyBotException {
        checkTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes the task holding the specified number and returns it.
     *
     * <p>The tasks after it move up, so their numbers each fall by one.
     *
     * @param taskNumber The task number, counted from one.
     * @return The task that was removed.
     * @throws HappyBotException If no task holds that number.
     */
    public Task delete(int taskNumber) throws HappyBotException {
        checkTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the deadlines due on the specified date, each under its task number.
     *
     * <p>The numbers are the ones the full list gives, so a deadline found here can be marked
     * or deleted by the number shown beside it.
     *
     * @param date The date the deadlines are wanted for.
     * @return The matching deadlines in list order, stored under their task numbers.
     */
    public Map<Integer, Task> findDeadlinesDueOn(LocalDate date) {
        // A LinkedHashMap keeps the deadlines in list order while remembering each task number.
        Map<Integer, Task> matchingTasks = new LinkedHashMap<>();

        for (int i = 0; i < tasks.size(); i++) {
            // A todo and an event have no due date, so only a deadline can match.
            if (tasks.get(i) instanceof Deadline deadline
                    && deadline.getEndDate().toLocalDate().equals(date)) {
                matchingTasks.put(i + 1, deadline);
            }
        }

        return matchingTasks;
    }

    /**
     * Rejects a task number that belongs to no task.
     *
     * @param taskNumber The task number, counted from one.
     * @throws HappyBotException If no task holds that number.
     */
    private void checkTaskNumber(int taskNumber) throws HappyBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HappyBotException("Please choose a valid task number.");
        }
    }
}
