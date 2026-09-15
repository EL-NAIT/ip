package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HappyBotTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-16T09:00:00Z"), ZoneOffset.UTC);

    @Test
    public void getResponse_addAndList_taskShown(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String addResponse = happyBot.getResponse("todo read book");
        String listResponse = happyBot.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("dance");

        assertEquals(" Oops! I don't know what that means :-(", response);
    }

    @Test
    public void getResponse_whitespaceAroundCommand_taskAddedWithNormalizedDescription(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("  todo\tread   book  ");

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_blankCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        assertEquals(" Oops! Please enter a command.", happyBot.getResponse("   \t"));
    }

    @Test
    public void getResponse_emptyTaskListMarkCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("mark 1");

        assertEquals(" Oops! There are no tasks to mark.", response);
    }

    @Test
    public void getResponse_eventStartEqualsEnd_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("event meeting /from 2019-12-01 /to 2019-12-01");

        assertEquals(" Oops! An event must start before it ends.", response);
    }

    @Test
    public void getResponse_eventStartAfterEnd_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("event meeting /from 2019-12-02 /to 2019-12-01");

        assertEquals(" Oops! An event must start before it ends.", response);
    }

    @Test
    public void getResponse_byeCommand_goodbyeReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
    }

    @Test
    public void getResponse_noArgumentCommandWithArgument_usageErrorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        assertEquals(" Oops! Use: list.", happyBot.getResponse("list now"));
        assertEquals(" Oops! Use: bye.", happyBot.getResponse("bye now"));
        assertFalse(happyBot.isExitCommand("bye now"));
    }

    @Test
    public void getResponse_duplicateTask_errorReturnedAndListUnchanged(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));
        happyBot.getResponse("todo read book");

        String response = happyBot.getResponse("todo READ book");
        String listResponse = happyBot.getResponse("list");

        assertEquals(" Oops! That task is already in your list.", response);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(listResponse.contains("2."));
    }

    @Test
    public void constructor_savedTask_taskLoaded(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile);
        firstSession.getResponse("todo read book");
        firstSession.close();

        HappyBot secondSession = new HappyBot(dataFile);

        assertTrue(secondSession.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_stats_currentWeekStatisticsReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);
        happyBot.getResponse("todo read book");
        happyBot.getResponse("deadline submit report /by 2026-09-18");
        happyBot.getResponse("event team meeting /from 2026-09-16 /to 2026-09-17");
        happyBot.getResponse("deadline pay fees /by 2026-09-19");
        happyBot.getResponse("mark 1");
        happyBot.getResponse("mark 2");
        happyBot.getResponse("mark 3");

        String response = happyBot.getResponse("stats");

        assertEquals(" Here are your statistics for this week:\n"
                        + " Total tasks: 4\n"
                        + " Completed this week: 3\n"
                        + "   To-dos: 1\n"
                        + "   Deadlines: 1\n"
                        + "   Events: 1\n"
                        + " Uncompleted deadlines due this week: 1",
                response);
    }

    @Test
    public void getResponse_statsWithArgument_usageErrorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        assertEquals(" Oops! Use: stats.", happyBot.getResponse("stats week"));
    }

    @Test
    public void getResponse_statsAfterReload_completedTaskStillCounted(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile, FIXED_CLOCK);
        firstSession.getResponse("todo read book");
        firstSession.getResponse("mark 1");
        firstSession.close();
        HappyBot secondSession = new HappyBot(dataFile, FIXED_CLOCK);

        String response = secondSession.getResponse("stats");

        assertTrue(response.contains("Completed this week: 1"));
    }

    @Test
    public void constructor_dataFileInUse_noticeAndCommandErrorReturned(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile);
        HappyBot secondSession = new HappyBot(dataFile);

        try {
            assertTrue(secondSession.getWelcomeMessage().contains("already open in another HappyBot session"));
            assertEquals(" Oops! Your task list at " + dataFile
                            + " is already open in another HappyBot session. Close the other session and try again.",
                    secondSession.getResponse("todo read book"));
        } finally {
            secondSession.close();
            firstSession.close();
        }
    }
}
