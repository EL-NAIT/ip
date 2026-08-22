/**
 * Represents an Event (a type of task) managed by HappyBot
 * An Event has specific start and end date in addition to description
 */
public class Event extends Task {

    protected String start;
    protected String end;

    public Event(String start, String end, String description) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * @return the task description, start and end date of event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.start + " to: " + this.end + ")";
    }
}
