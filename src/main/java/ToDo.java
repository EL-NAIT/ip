/**
 * Represents a ToDo (a type of task) managed by HappyBot
 */
public class ToDo extends Task {

    /**
     * Creates a todo with the specified description.
     *
     * @param description the todo description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * @return the task description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
