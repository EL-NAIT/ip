package happybot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.ToDo;

/**
 * Deals with making sense of the user command.
 *
 * <p>Each method turns command text into something the rest of HappyBot can act on: a command
 * word, a task number, a date, or a task. Text that cannot be read is rejected here with a
 * HappyBotException carrying the message shown to the user, so no other class has to look at
 * what was typed.
 *
 * <p>A parser keeps nothing between calls, so every method is static.
 */
public class Parser {
    /** Usage messages that show where a date belongs in each command. */
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd>.";
    private static final String EVENT_USAGE =
            "Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";

    /** Usage message for the command that lists the deadlines falling on one date. */
    private static final String DUE_USAGE = "Use: due <date>, such as due 2019-12-02.";

    /** Text that separates a deadline description from its due date. */
    private static final String DEADLINE_MARKER = " /by ";

    /** Text that separates an event description from its start date. */
    private static final String EVENT_START_MARKER = " /from ";

    /** Text that separates an event start date from its end date. */
    private static final String EVENT_END_MARKER = " /to ";

    /** Date patterns accepted from the user. The brackets mark each one as optional. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("[yyyy-MM-dd][d/M/yyyy]");

    /** Pattern accepted for the optional 24-hour time that may follow a date. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    /** Message shown when a date and time cannot be read. */
    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    /**
     * Returns the first word of a command, which says what the user wants done.
     *
     * @param userInput The command line, exactly as it was typed.
     * @return The command word, or the whole line when it holds no space.
     */
    public static String parseCommandWord(String userInput) {
        return userInput.split(" ", 2)[0];
    }

    /**
     * Returns everything a command holds after its command word.
     *
     * @param userInput The command line, exactly as it was typed.
     * @return The text after the first space, or an empty string when there is none.
     */
    public static String parseCommandBody(String userInput) {
        String[] commandParts = userInput.split(" ", 2);
        return commandParts.length == 2 ? commandParts[1] : "";
    }

    /**
     * Returns the task number named by a mark, unmark or delete command.
     *
     * <p>Whether the number belongs to a task is not decided here, because that depends on the
     * task list rather than on the text.
     *
     * @param body The command text after the command word.
     * @return The number the user typed.
     * @throws HappyBotException If the text is not a whole number.
     */
    public static int parseTaskNumber(String body) throws HappyBotException {
        try {
            return Integer.parseInt(body.trim());
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }
    }

    /**
     * Returns the todo described by a todo command.
     *
     * @param body The command text after the command word.
     * @return The todo the command describes.
     * @throws HappyBotException If the description is unusable or missing.
     */
    public static ToDo parseToDo(String body) throws HappyBotException {
        checkTaskText(body);

        if (body.isBlank()) {
            throw new HappyBotException("The description of a todo cannot be empty.");
        }

        return new ToDo(body);
    }

    /**
     * Returns the deadline described by a deadline command.
     *
     * @param body The command text after the command word.
     * @return The deadline the command describes.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable.
     */
    public static Deadline parseDeadline(String body) throws HappyBotException {
        checkTaskText(body);

        int markerIndex = body.indexOf(DEADLINE_MARKER);
        if (markerIndex < 0) {
            throw new HappyBotException(DEADLINE_USAGE);
        }

        String description = body.substring(0, markerIndex);
        String dueDateText = body.substring(markerIndex + DEADLINE_MARKER.length());
        if (description.isBlank() || dueDateText.isBlank()) {
            throw new HappyBotException(DEADLINE_USAGE);
        }

        return new Deadline(description, parseDateTime(dueDateText));
    }

    /**
     * Returns the event described by an event command.
     *
     * @param body The command text after the command word.
     * @return The event the command describes.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable, or
     *         if the event does not start before it ends.
     */
    public static Event parseEvent(String body) throws HappyBotException {
        checkTaskText(body);

        int startMarkerIndex = body.indexOf(EVENT_START_MARKER);
        // Searching for the end marker past the start marker keeps a /to that was typed first
        // from being read as the one belonging to this event.
        int endMarkerIndex =
                body.indexOf(EVENT_END_MARKER, startMarkerIndex + EVENT_START_MARKER.length());
        if (startMarkerIndex < 0 || endMarkerIndex < 0) {
            throw new HappyBotException(EVENT_USAGE);
        }

        String description = body.substring(0, startMarkerIndex);
        String startText =
                body.substring(startMarkerIndex + EVENT_START_MARKER.length(), endMarkerIndex);
        String endText = body.substring(endMarkerIndex + EVENT_END_MARKER.length());
        if (description.isBlank() || startText.isBlank() || endText.isBlank()) {
            throw new HappyBotException(EVENT_USAGE);
        }

        LocalDateTime startTime = parseDateTime(startText);
        LocalDateTime endTime = parseDateTime(endText);
        if (!startTime.isBefore(endTime)) {
            throw new HappyBotException("An event must start before it ends.");
        }

        return new Event(startTime, endTime, description);
    }

    /**
     * Returns the date named by a due command.
     *
     * <p>Any time written after the date is read and then dropped, because a due command asks
     * about a whole day.
     *
     * @param body The command text after the command word.
     * @return The date the deadlines are wanted for.
     * @throws HappyBotException If no date was given or the date cannot be read.
     */
    public static LocalDate parseDueDate(String body) throws HappyBotException {
        if (body.isBlank()) {
            throw new HappyBotException(DUE_USAGE);
        }

        return parseDateTime(body).toLocalDate();
    }

    /**
     * Converts the date text of a command into a date and time.
     *
     * <p>The time is optional: text without a space holds a date alone and starts at midnight,
     * while text with one must have a 24-hour time after the space.
     *
     * @param dateTimeText The date, and optional time, typed by the user.
     * @return The date and time the text describes.
     * @throws HappyBotException If the date, or the time given after it, cannot be read.
     */
    private static LocalDateTime parseDateTime(String dateTimeText) throws HappyBotException {
        // Collapsing runs of spaces lets the date and any time split cleanly in two.
        String[] dateTimeParts = dateTimeText.trim().replaceAll("\\s+", " ").split(" ", 2);

        try {
            LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FORMAT);
            LocalTime time = dateTimeParts.length == 2
                    ? LocalTime.parse(dateTimeParts[1], TIME_FORMAT)
                    : LocalTime.MIDNIGHT;

            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            // The hint replaces the exception's own message, which names an index in the text.
            throw new HappyBotException(DATE_FORMAT_HINT);
        }
    }

    /**
     * Rejects task text that could not be stored and read back correctly.
     *
     * <p>The data file separates fields with a vertical bar, so a task containing one would be
     * split into the wrong fields the next time HappyBot starts.
     *
     * @param taskText The command text describing the task.
     * @throws HappyBotException If the text contains a vertical bar.
     */
    private static void checkTaskText(String taskText) throws HappyBotException {
        if (taskText.contains("|")) {
            throw new HappyBotException("A task cannot contain the '|' character.");
        }
    }
}
