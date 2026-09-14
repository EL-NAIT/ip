package happybot.task;

import java.time.LocalDate;

/**
 * Represents a task managed by HappyBot.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private LocalDate completionDate;

    /**
     * Creates a task with the specified description.
     *
     * @param description The text entered by the user for this task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.completionDate = null;
    }

    /**
     * Marks this task as completed without recording a completion date.
     *
     * <p>This method is used when loading legacy saved tasks. HappyBot records a date for every
     * task marked through the user command.
     */
    public void markAsDone() {
        markAsDone(null);
    }

    /**
     * Marks this task as completed on the specified date.
     *
     * <p>Marking an already completed task preserves its existing completion date so that one
     * task never contributes more than once to a week's statistics.
     *
     * @param completionDate The completion date, or null when loading a legacy task whose date
     *         was not saved.
     */
    public void markAsDone(LocalDate completionDate) {
        if (!isDone) {
            this.isDone = true;
            this.completionDate = completionDate;
        }
    }

    /**
     * Marks this task as not completed and clears its completion date.
     */
    public void unmarkAsDone() {
        this.isDone = false;
        this.completionDate = null;
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
     * Returns the date this task was most recently marked as completed.
     *
     * @return The completion date, or null when the task is not done or was loaded from legacy
     *         data with no recorded completion date.
     */
    public LocalDate getCompletionDate() {
        return completionDate;
    }

    /**
     * Returns a marker for display depending on task completion.
     *
     * @return "X" for a completed task, " " for an uncompleted task.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
