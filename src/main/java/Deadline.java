/**
 * Represents a task with a due date managed by HappyBot.
 */
public class Deadline extends Task {
    protected String endDate;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description The deadline description.
     * @param endDate The deadline due date.
     */
    public Deadline(String description, String endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Returns the deadline due date.
     *
     * @return The deadline due date.
     */
    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.endDate + ")";
    }
}
