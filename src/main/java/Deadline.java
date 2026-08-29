import java.time.LocalDateTime;

/**
 * Represents a task with a due date managed by HappyBot.
 */
public class Deadline extends DatedTask {
    protected LocalDateTime endDate;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description The deadline description.
     * @param endDate The deadline due date.
     */
    public Deadline(String description, LocalDateTime endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Returns the deadline due date.
     *
     * @return The deadline due date.
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDate(this.endDate) + ")";
    }
}
