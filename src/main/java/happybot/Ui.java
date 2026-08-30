package happybot;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import happybot.task.Task;

/**
 * Deals with interactions with the user.
 *
 * <p>Every line HappyBot reads from the console and every line it prints passes through this
 * class, so no other class builds console text. The divider lines, the banner and the wording
 * of each reply can then be changed in one place.
 */
public class Ui {
    /** Line printed above and below a reply to separate it from the rest of the session. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Text art shown once, as part of the welcome message. */
    private static final String BANNER = "H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT\n"
            + "H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T\n"
            + "HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T\n"
            + "H   H  A   A  P      P         Y    B   B  O   O    T\n"
            + "H   H  A   A  P      P         Y    BBBB    OOO     T\n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the user has another command waiting to be read.
     *
     * @return True if a further command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command line typed by the user.
     *
     * @return The command line, exactly as it was typed.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the divider that separates a typed command from the reply to it.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints HappyBot's welcome message.
     */
    public void showWelcome() {
        System.out.println(DIVIDER + "\n"
                + BANNER
                + "Hello! I'm HappyBot.\n"
                + "How can I cheer you up today?\n"
                + DIVIDER);
    }

    /**
     * Prints HappyBot's farewell message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!\n" + DIVIDER);
    }

    /**
     * Prints every task together with its number and completion status.
     *
     * @param tasks The tasks to display, numbered from one in the order given.
     */
    public void showTaskList(List<Task> tasks) {
        StringBuilder taskList = new StringBuilder(" Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            taskList.append("\n").append(formatEntry(i + 1, tasks.get(i)));
        }

        showReply(taskList.toString());
    }

    /**
     * Prints the deadlines due on one date, or a notice when there are none.
     *
     * <p>The numbers come from the caller rather than from the position in this listing,
     * because a deadline shown here is marked or deleted by its number in the full task list.
     *
     * @param displayedDate The date the deadlines fall on, already written for display.
     * @param numberedDeadlines The deadlines to show, each stored under its task number.
     */
    public void showDeadlinesDueOn(String displayedDate, Map<Integer, Task> numberedDeadlines) {
        if (numberedDeadlines.isEmpty()) {
            showReply(" There are no deadlines due on " + displayedDate + ".");
            return;
        }

        StringBuilder matchingTasks =
                new StringBuilder(" Here are the deadlines due on " + displayedDate + ":");

        for (Map.Entry<Integer, Task> numberedDeadline : numberedDeadlines.entrySet()) {
            matchingTasks.append("\n")
                    .append(formatEntry(numberedDeadline.getKey(), numberedDeadline.getValue()));
        }

        showReply(matchingTasks.toString());
    }

    /**
     * Prints the tasks matching a keyword, or a notice when there are none.
     *
     * <p>The numbers come from the caller rather than from the position in this listing,
     * because a task shown here is marked or deleted by its number in the full task list.
     *
     * @param keyword The keyword that was searched for.
     * @param numberedTasks The matching tasks, each stored under its task number.
     */
    public void showMatchingTasks(String keyword, Map<Integer, Task> numberedTasks) {
        if (numberedTasks.isEmpty()) {
            showReply(" There are no tasks matching \"" + keyword + "\".");
            return;
        }

        StringBuilder matchingTasks = new StringBuilder(" Here are the matching tasks in your list:");

        for (Map.Entry<Integer, Task> numberedTask : numberedTasks.entrySet()) {
            matchingTasks.append("\n")
                    .append(formatEntry(numberedTask.getKey(), numberedTask.getValue()));
        }

        showReply(matchingTasks.toString());
    }

    /**
     * Confirms that a task was added.
     *
     * @param addedTask The task that was added.
     * @param taskCount The number of tasks now in the list.
     */
    public void showAddedTask(Task addedTask, int taskCount) {
        showReply(" Got it. I've added this task:\n"
                + "   " + addedTask + "\n"
                + " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was removed.
     *
     * @param deletedTask The task that was removed.
     * @param taskCount The number of tasks left in the list.
     */
    public void showDeletedTask(Task deletedTask, int taskCount) {
        showReply(" Alrighties I've removed this task:\n"
                + "   " + deletedTask + "\n"
                + " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param markedTask The task that was marked.
     */
    public void showMarkedTask(Task markedTask) {
        showReply(" Nice! I've marked this task as done:\n"
                + "   " + markedTask);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param unmarkedTask The task that was unmarked.
     */
    public void showUnmarkedTask(Task unmarkedTask) {
        showReply(" OK, I've marked this task as not done yet:\n"
                + "   " + unmarkedTask);
    }

    /**
     * Reports that the saved tasks could not be read.
     *
     * @param filePath The location of the data file.
     */
    public void showLoadingError(Path filePath) {
        // A notice answers no command, so it opens a block of its own instead of closing one.
        System.out.println(DIVIDER + "\n Heads up! I could not read " + filePath
                + ", so I am starting with an empty task list.\n" + DIVIDER);
    }

    /**
     * Reports how many saved lines were unreadable and therefore left out.
     *
     * @param skippedLineCount The number of lines that were skipped.
     */
    public void showSkippedLinesNotice(int skippedLineCount) {
        // A notice answers no command, so it opens a block of its own instead of closing one.
        System.out.println(DIVIDER + "\n Heads up! I skipped " + skippedLineCount
                + " unreadable line(s) in your saved data.\n" + DIVIDER);
    }

    /**
     * Reports that the tasks could not be written to the data file.
     *
     * @param filePath The location of the data file.
     */
    public void showSavingError(Path filePath) {
        showReply(" Heads up! I could not save your tasks to " + filePath + ".");
    }

    /**
     * Reports a command that HappyBot could not carry out.
     *
     * @param message The explanation of what was wrong with the command.
     */
    public void showError(String message) {
        showReply(" Oops! " + message);
    }

    /**
     * Prints a reply to a command, followed by the closing divider.
     *
     * <p>Every reply to a command ends here, so the closing divider is written once rather than
     * in each of the nine methods above. The opening divider is printed by showDivider() as soon
     * as the command is read, so a reply only has to close the block.
     *
     * @param reply The reply text, with each line already indented.
     */
    private void showReply(String reply) {
        System.out.println(reply + "\n" + DIVIDER);
    }

    /**
     * Returns the display text of one numbered task, without a line break of its own.
     *
     * @param taskNumber The number shown beside the task.
     * @param task The task to display.
     * @return The numbered task line.
     */
    private static String formatEntry(int taskNumber, Task task) {
        return " " + taskNumber + "." + task;
    }
}
