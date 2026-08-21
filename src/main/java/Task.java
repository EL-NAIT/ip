/**
 * Represents a task managed by HappyBot
 */
public class Task {
    protected String description;

    /**
     * Creates a task with the specified description.
     *
     * @param description the text entered by the user for this task
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Returns the task description for display.
     *
     * @return the task description
     */
    @Override
    public String toString() {
        return description;
    }
}
