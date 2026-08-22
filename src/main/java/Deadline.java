/**
 * Represents a Deadline (a type of task) managed by HappyBot
 * A Deadline has an end date in addition to a description
 */
public class Deadline extends Task {
    protected String end;

    public Deadline(String description, String endDate) {
         super(description);
         this.end = endDate;
    }

    /**
     * @return the task description and end date of deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.end + ")";
    }
}
