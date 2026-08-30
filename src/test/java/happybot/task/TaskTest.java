package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void constructor_description_taskNotDone() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void markAsDone_notDoneTask_taskBecomesDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    public void markAsDone_alreadyDoneTask_taskStaysDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    public void unmarkAsDone_doneTask_taskBecomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.unmarkAsDone();

        assertFalse(task.isDone());
    }

    @Test
    public void unmarkAsDone_notDoneTask_taskStaysNotDone() {
        Task task = new Task("read book");

        task.unmarkAsDone();

        assertFalse(task.isDone());
    }

    @Test
    public void toString_notDoneTask_statusBoxEmpty() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_doneTask_statusBoxHoldsCross() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }
}
