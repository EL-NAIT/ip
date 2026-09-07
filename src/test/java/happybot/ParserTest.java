package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.ToDo;

public class ParserTest {
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd>.";

    private static final String EVENT_USAGE =
            "Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";

    private static final String DUE_USAGE = "Use: due <date>, such as due 2019-12-02.";

    private static final String FIND_USAGE = "Use: find <keyword>, such as find book.";

    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    private static final String NO_SUCH_DATE_HINT =
            "There is no such date on the calendar. Please enter a valid date, checking the "
                    + "number of days the month has.";

    private static final String PIPE_MESSAGE = "A task cannot contain the '|' character.";

    private static final String TASK_NUMBER_MESSAGE = "Please provide a valid task number.";

    // ==================== parseCommandWord ====================

    @Test
    public void parseCommandWord_commandWithBody_firstWordReturned() {
        assertEquals("deadline", Parser.parseCommandWord("deadline return book /by 2019-12-02"));
    }

    @Test
    public void parseCommandWord_commandWithoutBody_wholeLineReturned() {
        assertEquals("list", Parser.parseCommandWord("list"));
    }

    @Test
    public void parseCommandWord_emptyInput_emptyStringReturned() {
        assertEquals("", Parser.parseCommandWord(""));
    }

    // ==================== parseCommandBody ====================

    @Test
    public void parseCommandBody_commandWithBody_textAfterFirstSpaceReturned() {
        assertEquals("read book", Parser.parseCommandBody("todo read book"));
    }

    @Test
    public void parseCommandBody_commandWithoutBody_emptyStringReturned() {
        assertEquals("", Parser.parseCommandBody("list"));
    }

    @Test
    public void parseCommandBody_bodyHoldsSpaces_spacesKept() {
        // Only the first space is consumed, so the rest of the line reaches the command untouched.
        assertEquals("return book /by 2019-12-02",
                Parser.parseCommandBody("deadline return book /by 2019-12-02"));
    }

    // ==================== parseTaskNumber ====================

    @Test
    public void parseTaskNumber_numberWithSurroundingSpaces_numberReturned() throws HappyBotException {
        assertEquals(2, Parser.parseTaskNumber("  2  "));
    }

    @Test
    public void parseTaskNumber_numberOutOfRange_numberStillReturned() throws HappyBotException {
        // Whether a number belongs to a task depends on the task list, so TaskList rejects it
        // instead of Parser. This records that split of responsibility.
        assertEquals(0, Parser.parseTaskNumber("0"));
        assertEquals(-1, Parser.parseTaskNumber("-1"));
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber("two"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_decimalNumber_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber("2.5"));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void parseTaskNumber_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseTaskNumber(""));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    // ==================== parseToDo ====================

    @Test
    public void parseToDo_description_todoCreated() throws HappyBotException {
        ToDo todo = Parser.parseToDo("read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseToDo_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        assertEquals("read book", Parser.parseToDo("   read book   ").getDescription());
    }

    @Test
    public void parseToDo_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo(""));

        assertEquals("The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseToDo_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo("    "));

        assertEquals("The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseToDo_pipeInDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseToDo("read book | CD"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    // ==================== parseDeadline ====================

    @Test
    public void parseDeadline_isoDateWithoutTime_deadlineStartsAtMidnight() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_slashDateWithoutTime_deadlineStartsAtMidnight() throws HappyBotException {
        // The d/M/yyyy pattern accepts a one-digit day and month, so 2/12/2019 is the same date.
        Deadline deadline = Parser.parseDeadline("return book /by 2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_dateWithTime_timeKept() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_extraSpacesAroundDate_deadlineParsed() throws HappyBotException {
        // Parser collapses runs of spaces before splitting the date from the time.
        Deadline deadline = Parser.parseDeadline("return book /by    2019-12-02    1800   ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_descriptionContainsBy_onlyMarkerSplitsCommand() throws HappyBotException {
        // "by" only separates the two parts when it is written as the " /by " marker.
        Deadline deadline = Parser.parseDeadline("stand by the door /by 2019-12-02");

        assertEquals("stand by the door", deadline.getDescription());
    }

    @Test
    public void parseDeadline_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        // Stray spaces must not reach the stored description, where they would be saved to the
        // data file and shown in every listing.
        Deadline deadline = Parser.parseDeadline("   return book  /by 2019-12-02");

        assertEquals("return book", deadline.getDescription());
    }

    @Test
    public void parseDeadline_dayPastEndOfMonth_exceptionThrown() {
        // November has 30 days, so this date names no real day and must be refused rather than
        // quietly moved to 30 November.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-11-31"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_dayPastEndOfFebruary_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-02-30"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_leapDayInNonLeapYear_exceptionThrown() {
        // 2019 is not a leap year, so it has no 29 February.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-02-29"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_leapDayInLeapYear_deadlineCreated() throws HappyBotException {
        // 2020 is a leap year, so 29 February exists and must still be accepted.
        Deadline deadline = Parser.parseDeadline("return book /by 2020-02-29");

        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), deadline.getDueDateTime());
    }

    @Test
    public void parseDeadline_dayPastEndOfMonthInSlashDate_exceptionThrown() {
        // The same rule applies to the other date pattern.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 31/11/2019"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_dayOutsideAnyMonth_formatHintGiven() {
        // No month has 32 days, so this is a badly written date rather than a missing one.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-11-32"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_monthOutsideYear_formatHintGiven() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-13-05"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_newDeadline_notDone() throws HappyBotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");

        assertFalse(deadline.isDone());
    }

    @Test
    public void parseDeadline_noMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book 2019-12-02"));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_noDueDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("do homework /by       "));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_noDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline(" /by 2019-12-02"));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDeadline(""));

        assertEquals(DEADLINE_USAGE, e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by tomorrow"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_unreadableTime_exceptionThrown() {
        // 6pm has to be written as the 24-hour time 1800.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-12-02 6pm"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_readableDateWithUnreadableTime_formatHintGiven() {
        // The date is fine here, so the message has to be about the time rather than the calendar.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book /by 2019-12-02 2500"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseDeadline_pipeInDescription_exceptionThrown() {
        // The data file separates fields with a vertical bar, so a task may not hold one.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDeadline("return book | CD /by 2019-12-02"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    // ==================== parseEvent ====================

    @Test
    public void parseEvent_datesWithoutTimes_eventCreated() throws HappyBotException {
        Event event = Parser.parseEvent("orientation /from 2019-12-01 /to 2019-12-03");

        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 0, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 0, 0), event.getEndTime());
    }

    @Test
    public void parseEvent_datesWithTimes_timesKept() throws HappyBotException {
        Event event = Parser.parseEvent("orientation /from 2019-12-01 0900 /to 2019-12-03 1700");

        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 17, 0), event.getEndTime());
    }

    @Test
    public void parseEvent_sameDayDifferentTimes_eventCreated() throws HappyBotException {
        Event event = Parser.parseEvent("lecture /from 2019-12-01 0900 /to 2019-12-01 1100");

        assertEquals(LocalDateTime.of(2019, 12, 1, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 1, 11, 0), event.getEndTime());
    }

    @Test
    public void parseEvent_spacesAroundDescription_descriptionTrimmed() throws HappyBotException {
        Event event = Parser.parseEvent("   orientation  /from 2019-12-01 /to 2019-12-03");

        assertEquals("orientation", event.getDescription());
    }

    @Test
    public void parseEvent_noStartMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_noEndMarker_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_endMarkerBeforeStartMarker_exceptionThrown() {
        // The end marker is searched for only after the start marker, so a /to typed first is
        // not mistaken for this event's end.
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /to 2019-12-03 /from 2019-12-01"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_noDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent(" /from 2019-12-01 /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_blankStartDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from    /to 2019-12-03"));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_blankEndDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /to     "));

        assertEquals(EVENT_USAGE, e.getMessage());
    }

    @Test
    public void parseEvent_startEqualsEnd_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-01 /to 2019-12-01"));

        assertEquals("An event must start before it ends.", e.getMessage());
    }

    @Test
    public void parseEvent_startAfterEnd_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from 2019-12-03 /to 2019-12-01"));

        assertEquals("An event must start before it ends.", e.getMessage());
    }

    @Test
    public void parseEvent_unreadableStartDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("orientation /from someday /to 2019-12-03"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }

    @Test
    public void parseEvent_pipeInDescription_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseEvent("talk | panel /from 2019-12-01 /to 2019-12-03"));

        assertEquals(PIPE_MESSAGE, e.getMessage());
    }

    // ==================== parseKeyword ====================

    @Test
    public void parseKeyword_word_keywordReturned() throws HappyBotException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_spacesAroundKeyword_keywordTrimmed() throws HappyBotException {
        assertEquals("book", Parser.parseKeyword("   book   "));
    }

    @Test
    public void parseKeyword_severalWords_wholeTextReturned() throws HappyBotException {
        // The whole text is searched for, so a keyword may hold a space.
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseKeyword(""));

        assertEquals(FIND_USAGE, e.getMessage());
    }

    @Test
    public void parseKeyword_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseKeyword("    "));

        assertEquals(FIND_USAGE, e.getMessage());
    }

    // ==================== parseDueDate ====================

    @Test
    public void parseDueDate_isoDate_dateReturned() throws HappyBotException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDueDate("2019-12-02"));
    }

    @Test
    public void parseDueDate_slashDate_dateReturned() throws HappyBotException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDueDate("2/12/2019"));
    }

    @Test
    public void parseDueDate_dateWithTime_timeDropped() throws HappyBotException {
        // A due command asks about a whole day, so any time written after the date is ignored.
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDueDate("2019-12-02 1800"));
    }

    @Test
    public void parseDueDate_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate(""));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("   "));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_dayPastEndOfMonth_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("2019-11-31"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDueDate_unreadableDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("tomorrow"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }
}
