package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_notDoneEvent_typeStatusAndBothDatesShown() {
        Event event = new Event(LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0), "orientation");

        assertEquals("[E][ ] orientation (from: Dec 01 2019 9:00AM to: Dec 03 2019 5:00PM)",
                event.toString());
    }

    @Test
    public void toString_doneEvent_statusBoxHoldsCross() {
        Event event = new Event(LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0), "orientation");
        event.markAsDone();

        assertEquals("[E][X] orientation (from: Dec 01 2019 9:00AM to: Dec 03 2019 5:00PM)",
                event.toString());
    }

    @Test
    public void toString_eventWithinOneDay_bothDatesStillShown() {
        Event event = new Event(LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 1, 11, 0), "lecture");

        assertEquals("[E][ ] lecture (from: Dec 01 2019 9:00AM to: Dec 01 2019 11:00AM)",
                event.toString());
    }

    @Test
    public void getStartTimeAndGetEndTime_event_datesReturnedInOrderGiven() {
        // The constructor takes the start before the end, unlike Deadline which takes the
        // description first, so this guards against the two being swapped.
        LocalDateTime start = LocalDateTime.of(2019, 12, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2019, 12, 3, 17, 0);
        Event event = new Event(start, end, "orientation");

        assertEquals(start, event.getStartTime());
        assertEquals(end, event.getEndTime());
    }

    @Test
    public void constructor_endNotAfterStart_assertionErrorThrown() {
        LocalDateTime time = LocalDateTime.of(2019, 12, 1, 9, 0);

        assertThrows(AssertionError.class, () -> new Event(time, time, "orientation"));
    }
}
