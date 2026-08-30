package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_notDoneDeadline_typeStatusAndDueDateShown() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_statusBoxHoldsCross() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 02 2019 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_deadlineAtMidnight_timeShownAsTwelveAm() {
        // A command written without a time stores midnight, which has to read sensibly.
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 12:00AM)", deadline.toString());
    }

    @Test
    public void toString_deadlineAtNoon_timeShownAsTwelvePm() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 12, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 12:00PM)", deadline.toString());
    }

    @Test
    public void toString_deadlineBeforeTen_hourNotPadded() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 9, 5));

        assertEquals("[D][ ] return book (by: Dec 02 2019 9:05AM)", deadline.toString());
    }

    @Test
    public void getEndDate_deadline_dueDateReturned() {
        LocalDateTime dueDate = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(dueDate, new Deadline("return book", dueDate).getEndDate());
    }
}
