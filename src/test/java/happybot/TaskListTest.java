package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.Task;
import happybot.task.ToDo;

public class TaskListTest {
    private static final String TASK_NUMBER_MESSAGE = "Please choose a valid task number.";

    // Builds a list holding three tasks, numbered 1 to 3 in the order given here.
    private TaskList buildListOfThree() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        taskList.add(new Event(LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0), "orientation"));
        return taskList;
    }

    // ==================== construction ====================

    @Test
    public void constructor_noArguments_listEmpty() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertTrue(taskList.isEmpty());
    }

    @Test
    public void constructor_existingTasks_tasksCopied() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new ToDo("read book"));
        TaskList taskList = new TaskList(sourceTasks);

        // The list must not follow changes made to the list it was built from.
        sourceTasks.add(new ToDo("return book"));

        assertEquals(1, taskList.size());
    }

    // ==================== add, size and isEmpty ====================

    @Test
    public void add_tasks_sizeGrowsAndOrderKept() throws HappyBotException {
        TaskList taskList = buildListOfThree();

        assertEquals(3, taskList.size());
        assertFalse(taskList.isEmpty());
        assertEquals("read book", taskList.get(1).getDescription());
        assertEquals("orientation", taskList.get(3).getDescription());
    }

    // ==================== getTasks ====================

    @Test
    public void getTasks_returnedList_cannotBeModified() {
        TaskList taskList = buildListOfThree();
        List<Task> tasks = taskList.getTasks();

        // The view is read-only so that no caller can add or remove tasks behind the list's back.
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new ToDo("sneak in")));
    }

    @Test
    public void getTasks_afterAdd_viewShowsNewTask() {
        TaskList taskList = new TaskList();
        List<Task> tasks = taskList.getTasks();
        taskList.add(new ToDo("read book"));

        // The view is a window onto the list rather than a snapshot taken when it was asked for.
        assertEquals(1, tasks.size());
    }

    // ==================== get ====================

    @Test
    public void get_firstAndLastNumbers_matchingTasksReturned() throws HappyBotException {
        TaskList taskList = buildListOfThree();

        assertEquals("read book", taskList.get(1).getDescription());
        assertEquals("orientation", taskList.get(3).getDescription());
    }

    @Test
    public void get_numberBelowRange_exceptionThrown() {
        TaskList taskList = buildListOfThree();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.get(0));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void get_negativeNumber_exceptionThrown() {
        TaskList taskList = buildListOfThree();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.get(-1));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void get_numberPastEnd_exceptionThrown() {
        TaskList taskList = buildListOfThree();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.get(4));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void get_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.get(1));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void get_task_sameObjectReturned() throws HappyBotException {
        // Marking a task through the object returned here has to change the stored task, so the
        // list must hand back the task itself rather than a copy.
        TaskList taskList = new TaskList();
        Task task = new ToDo("read book");
        taskList.add(task);

        assertSame(task, taskList.get(1));
    }

    // ==================== delete ====================

    @Test
    public void delete_middleNumber_taskRemovedAndLaterNumbersShift() throws HappyBotException {
        TaskList taskList = buildListOfThree();

        Task deletedTask = taskList.delete(2);

        assertEquals("return book", deletedTask.getDescription());
        assertEquals(2, taskList.size());
        // The event moves up from number 3 to number 2.
        assertEquals("orientation", taskList.get(2).getDescription());
    }

    @Test
    public void delete_onlyTask_listBecomesEmpty() throws HappyBotException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        taskList.delete(1);

        assertTrue(taskList.isEmpty());
    }

    @Test
    public void delete_numberPastEnd_exceptionThrownAndListUnchanged() {
        TaskList taskList = buildListOfThree();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.delete(4));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
        assertEquals(3, taskList.size());
    }

    @Test
    public void delete_numberBelowRange_exceptionThrown() {
        TaskList taskList = buildListOfThree();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.delete(0));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    @Test
    public void delete_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        HappyBotException e = assertThrows(HappyBotException.class, () -> taskList.delete(1));

        assertEquals(TASK_NUMBER_MESSAGE, e.getMessage());
    }

    // ==================== findDeadlinesDueOn ====================

    // ==================== findTasksContaining ====================

    @Test
    public void findTasksContaining_keywordInDescription_foundUnderTaskNumbers() {
        TaskList taskList = buildListOfThree();

        Map<Integer, Task> found = taskList.findTasksContaining("book");

        // The numbers are the ones the full list shows, so a match can be marked or deleted by them.
        assertEquals(List.of(1, 2), new ArrayList<>(found.keySet()));
        assertEquals("read book", found.get(1).getDescription());
        assertEquals("return book", found.get(2).getDescription());
    }

    @Test
    public void findTasksContaining_differentCapitalization_taskStillFound() {
        TaskList taskList = buildListOfThree();

        assertEquals(2, taskList.findTasksContaining("BOOK").size());
        assertEquals(2, taskList.findTasksContaining("BoOk").size());
    }

    @Test
    public void findTasksContaining_partOfWord_taskFound() {
        // The keyword is looked for anywhere in the description, not only as a whole word.
        TaskList taskList = buildListOfThree();

        assertEquals(1, taskList.findTasksContaining("rient").size());
    }

    @Test
    public void findTasksContaining_matchInLaterTaskOnly_numberKept() {
        TaskList taskList = buildListOfThree();

        Map<Integer, Task> found = taskList.findTasksContaining("orientation");

        // The event is the third task, so it stays number 3 rather than being renumbered to 1.
        assertEquals(List.of(3), new ArrayList<>(found.keySet()));
    }

    @Test
    public void findTasksContaining_keywordInDateNotDescription_taskNotFound() {
        // Only the description is searched, so the date of a deadline cannot match.
        TaskList taskList = buildListOfThree();

        assertTrue(taskList.findTasksContaining("Dec").isEmpty());
    }

    @Test
    public void findTasksContaining_noMatch_noneFound() {
        TaskList taskList = buildListOfThree();

        assertTrue(taskList.findTasksContaining("pizza").isEmpty());
    }

    @Test
    public void findTasksContaining_emptyList_noneFound() {
        assertTrue(new TaskList().findTasksContaining("book").isEmpty());
    }

    // ==================== findDeadlinesDueOn ====================

    @Test
    public void findDeadlinesDueOn_matchingDeadlines_foundUnderTaskNumbers() {
        TaskList taskList = buildListOfThree();
        taskList.add(new Deadline("pay fees", LocalDateTime.of(2019, 12, 2, 9, 0)));

        Map<Integer, Task> found = taskList.findDeadlinesDueOn(LocalDate.of(2019, 12, 2));

        // The numbers are the ones the full list shows, so a match can be marked or deleted by them.
        assertEquals(List.of(2, 4), new ArrayList<>(found.keySet()));
        assertEquals("return book", found.get(2).getDescription());
        assertEquals("pay fees", found.get(4).getDescription());
    }

    @Test
    public void findDeadlinesDueOn_deadlineAtDifferentTimeSameDay_deadlineFound() {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 23, 59)));

        // A due command asks about a whole day, so the time of day must not matter.
        assertEquals(1, taskList.findDeadlinesDueOn(LocalDate.of(2019, 12, 2)).size());
    }

    @Test
    public void findDeadlinesDueOn_noDeadlineOnDate_noneFound() {
        TaskList taskList = buildListOfThree();

        assertTrue(taskList.findDeadlinesDueOn(LocalDate.of(2019, 12, 5)).isEmpty());
    }

    @Test
    public void findDeadlinesDueOn_eventCoveringDate_eventNotFound() {
        // The event runs from 1 December to 3 December, but only deadlines are due on a date.
        TaskList taskList = buildListOfThree();

        Map<Integer, Task> found = taskList.findDeadlinesDueOn(LocalDate.of(2019, 12, 1));

        assertTrue(found.isEmpty());
    }

    @Test
    public void findDeadlinesDueOn_emptyList_noneFound() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.findDeadlinesDueOn(LocalDate.of(2019, 12, 2)).isEmpty());
    }
}
