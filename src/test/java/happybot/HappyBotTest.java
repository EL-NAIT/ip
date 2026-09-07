package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HappyBotTest {

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
}
