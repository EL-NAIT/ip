/**
 * Represents a task managed by HappyBot.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the specified description.
     *
     * @param description The text entered by the user for this task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a marker for display depending on task completion
     *
     * @return "X" for completed task, " " for uncompleted task
     */
    private String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
