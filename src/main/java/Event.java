/**
 * Represents a task with a start and end time managed by HappyBot.
 */
public class Event extends Task {

    protected String startTime;
    protected String endTime;

    /**
     * Creates an event task with the specified description and time range.
     *
     * @param start The event start time.
     * @param end The event end time.
     * @param description The event description.
     */
    public Event(String start, String end, String description) {
        super(description);
        this.startTime = start;
        this.endTime = end;
    }

    /**
     * Returns the event start time.
     *
     * @return The event start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Returns the event end time.
     *
     * @return The event end time.
     */
    public String getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.startTime + " to: " + this.endTime + ")";
    }
}
