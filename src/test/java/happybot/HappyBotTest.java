package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void getResponse_emptyTaskListMarkCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("mark 1");

        assertEquals(" Oops! There are no tasks to mark.", response);
    }

    @Test
    public void getResponse_byeCommand_goodbyeReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
    }

    @Test
    public void constructor_savedTask_taskLoaded(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile);
        firstSession.getResponse("todo read book");

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
        HappyBot secondSession = new HappyBot(dataFile, FIXED_CLOCK);

        String response = secondSession.getResponse("stats");

        assertTrue(response.contains("Completed this week: 1"));
    }
}
