import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the HappyBot chatbot application.
 *
 * <p>A HappyBot object owns the task list and the storage that saves it, so the methods that
 * work on them read those from the object instead of receiving them as parameters.
 */
public class HappyBot {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path DATA_FILE_PATH = Path.of("data", "HappyBot.txt");

    /** Usage messages that show where a date belongs in each command. */
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd>.";
    private static final String EVENT_USAGE =
            "Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";

    /** Usage message for the command that lists the deadlines falling on one date. */
    private static final String DUE_USAGE = "Use: due <date>, such as due 2019-12-02.";

    /** Date patterns accepted from the user. The brackets mark each one as optional. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("[yyyy-MM-dd][d/M/yyyy]");

    /** Pattern accepted for the optional 24-hour time that may follow a date. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    /** Message shown when a date and time cannot be read. */
    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";
    private static final String BANNER = "H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT\n"
            + "H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T\n"
            + "HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T\n"
            + "H   H  A   A  P      P         Y    B   B  O   O    T\n"
            + "H   H  A   A  P      P         Y    BBBB    OOO     T\n";

    /** Storage that loads the saved tasks at startup and saves them after every change. */
    private final Storage storage;

    /** Tasks held by this HappyBot for the length of the session. */
    private List<Task> tasks;

    /**
     * Creates a HappyBot that keeps its tasks in the specified data file.
     *
     * <p>The saved tasks are read in run() rather than here, so that the welcome message is
     * printed before any notice about unreadable saved data.
     *
     * @param filePath The location of the task data file.
     */
    public HappyBot(Path filePath) {
        this.storage = new Storage(filePath);
        this.tasks = new ArrayList<>();
    }

    /**
     * Prints HappyBot's welcome message.
     */
    private static void printWelcomeMessage() {
        System.out.println(DIVIDER + "\n"
                + BANNER
                + "Hello! I'm HappyBot.\n"
                + "How can I cheer you up today?\n"
                + DIVIDER);
    }

    /**
     * Prints HappyBot's farewell message.
     */
    private static void printGoodbyeMessage() {
        System.out.println("Bye. Hope to see you again soon!\n" + DIVIDER);
    }

    /**
     * Prints every task together with its completion status.
     *
     * @param tasks The tasks to display.
     */
    private static void printTaskList(List<Task> tasks) {
        StringBuilder taskList = new StringBuilder(" Here are the tasks in your list:\n");

        for (int i = 0; i < tasks.size(); i++) {
            taskList.append(" ")
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i))
                    .append("\n");
        }

        taskList.append(DIVIDER);
        System.out.println(taskList);
    }

    /**
     * Prints a standalone notice inside the usual divider lines.
     *
     * @param notice The message to display.
     */
    private static void printNotice(String notice) {
        System.out.println(DIVIDER + "\n " + notice + "\n" + DIVIDER);
    }

    /**
     * Loads the saved tasks and reports saved data that could not be read.
     *
     * <p>A data file that cannot be read must not end the session, so the problem is reported
     * and HappyBot starts with the empty task list made by the constructor.
     */
    private void loadSavedTasks() {
        try {
            tasks = storage.loadTasks();
            if (storage.getSkippedLineCount() > 0) {
                printNotice("Heads up! I skipped " + storage.getSkippedLineCount()
                        + " unreadable line(s) in your saved data.");
            }
        } catch (IOException e) {
            printNotice("Heads up! I could not read " + storage.getFilePath()
                    + ", so I am starting with an empty task list.");
        }
    }

    /**
     * Saves the tasks and warns the user when the data file cannot be written.
     *
     * <p>A failed save must not end the session, so the problem is reported and the in-memory
     * task list is kept for the rest of the run.
     */
    private void saveTasks() {
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            System.out.println(" Heads up! I could not save your tasks to "
                    + storage.getFilePath() + ".\n" + DIVIDER);
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

    /**
     * Adds a task, saves the updated list, and confirms the addition to the user.
     *
     * @param taskToAdd The task to add.
     */
    private void addTask(Task taskToAdd) {
        tasks.add(taskToAdd);
        saveTasks();
        System.out.println(" Got it. I've added this task:\n"
                + "   " + taskToAdd + "\n"
                + " Now you have " + tasks.size() + " tasks in the list.\n"
                + DIVIDER);
    }

    /**
     * Marks a selected task as completed.
     *
     * @param userInput The command entered by the user.
     * @throws HappyBotException If the task number is invalid.
     */
    private void markTask(String userInput) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to mark.");
        }

        String taskNumberText = userInput.substring("mark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        Task taskToMark = tasks.get(taskNumber - 1);
        taskToMark.markAsDone();
        saveTasks();
        System.out.println(" Nice! I've marked this task as done:\n"
                + "   " + taskToMark + "\n"
                + DIVIDER);
    }

    /**
     * Marks a selected task as not completed.
     *
     * @param userInput The command entered by the user.
     * @throws HappyBotException If the task number is invalid.
     */
    private void unmarkTask(String userInput) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to unmark.");
        }

        String taskNumberText = userInput.substring("unmark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        Task taskToUnmark = tasks.get(taskNumber - 1);
        taskToUnmark.unmarkAsDone();
        saveTasks();
        System.out.println(" OK, I've marked this task as not done yet:\n"
                + "   " + taskToUnmark + "\n"
                + DIVIDER);
    }

    /**
     * Deletes a selected task from the list.
     *
     * @param userInput The command entered by the user.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private void deleteTask(String userInput) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to delete.");
        }

        String taskNumberText = userInput.substring("delete".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        Task taskToDelete = tasks.get(taskNumber - 1);
        tasks.remove(taskNumber - 1);
        saveTasks();
        System.out.println(" Alrighties I've removed this task:\n"
                + "   " + taskToDelete + "\n"
                + " Now you have " + tasks.size() + " tasks in the list.\n"
                + DIVIDER);
    }

    /**
     * Converts the date text of a command into a date and time.
     *
     * <p>The time is optional: text without a space holds a date alone and starts at midnight,
     * while text with one must have a 24-hour time after the space.
     *
     * @param dateTimeText The date, and optional time, typed by the user.
     * @return The date and time the text describes.
     * @throws DateTimeParseException If the date, or the time given after it, cannot be read.
     */
    private static LocalDateTime parseDateTime(String dateTimeText) {
        // Collapsing runs of spaces lets the date and any time split cleanly in two.
        String[] dateTimeParts = dateTimeText.trim().replaceAll("\\s+", " ").split(" ", 2);
        LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FORMAT);
        LocalTime time = dateTimeParts.length == 2
                ? LocalTime.parse(dateTimeParts[1], TIME_FORMAT)
                : LocalTime.MIDNIGHT;

        return LocalDateTime.of(date, time);
    }

    /**
     * Prints the deadlines due on the specified date.
     *
     * <p>Each deadline keeps the number it has in the full list, so one shown here can be marked
     * or deleted with the number displayed beside it.
     *
     * @param dateText The date typed by the user.
     * @throws HappyBotException If no date was given or the date cannot be read.
     */
    private void printDeadlinesDueOn(String dateText) throws HappyBotException {
        if (dateText.isBlank()) {
            throw new HappyBotException(DUE_USAGE);
        }

        LocalDate date = parseDateTime(dateText).toLocalDate();
        // Reusing the task display keeps one source of truth for how a date is written.
        String displayedDate = DatedTask.formatDate(date);
        StringBuilder matchingTasks = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            // A todo and an event have no due date, so only a deadline can match.
            if (tasks.get(i) instanceof Deadline deadline
                    && deadline.getEndDate().toLocalDate().equals(date)) {
                matchingTasks.append(" ")
                        .append(i + 1)
                        .append(".")
                        .append(tasks.get(i))
                        .append("\n");
            }
        }

        if (matchingTasks.isEmpty()) {
            System.out.println(" There are no deadlines due on " + displayedDate + ".\n" + DIVIDER);
            return;
        }

        System.out.println(" Here are the deadlines due on " + displayedDate + ":\n"
                + matchingTasks + DIVIDER);
    }

    /**
     * Greets the user, then reads and carries out commands until the session ends.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        printWelcomeMessage();
        loadSavedTasks();

        // Stopping at the end of the input also ends the session cleanly when no bye is typed.
        while (isRunning && scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            System.out.println(DIVIDER);
            String[] instructionArray = userInput.split(" ", 2);
            String command = instructionArray[0];
            String body = instructionArray.length == 2 ? instructionArray[1] : "";

            try {
                switch (command) {
                case "bye":
                    isRunning = false;
                    break;
                case "list":
                    printTaskList(tasks);
                    break;
                case "mark":
                    markTask(userInput);
                    break;
                case "unmark":
                    unmarkTask(userInput);
                    break;
                case "delete":
                    deleteTask(userInput);
                    break;
                case "due":
                    printDeadlinesDueOn(body);
                    break;
                case "todo":
                    checkTaskText(body);
                    if (body.isBlank()) {
                        throw new HappyBotException("The description of a todo cannot be empty.");
                    }
                    addTask(new ToDo(body));
                    break;
                case "deadline":
                    checkTaskText(body);
                    String deadlineMarker = " /by ";
                    int deadlineMarkerIndex = body.indexOf(deadlineMarker);
                    if (deadlineMarkerIndex < 0) {
                        throw new HappyBotException(DEADLINE_USAGE);
                    }
                    String deadlineDescription = body.substring(0, deadlineMarkerIndex);
                    String dueDateText = body.substring(deadlineMarkerIndex + deadlineMarker.length());
                    if (deadlineDescription.isBlank() || dueDateText.isBlank()) {
                        throw new HappyBotException(DEADLINE_USAGE);
                    }
                    LocalDateTime dueDate = parseDateTime(dueDateText);
                    addTask(new Deadline(deadlineDescription, dueDate));
                    break;
                case "event":
                    checkTaskText(body);
                    String fromMarker = " /from ";
                    String toMarker = " /to ";
                    int fromMarkerIndex = body.indexOf(fromMarker);
                    int toMarkerIndex = body.indexOf(toMarker, fromMarkerIndex + fromMarker.length());
                    if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
                        throw new HappyBotException(EVENT_USAGE);
                    }
                    String eventDescription = body.substring(0, fromMarkerIndex);
                    String startText = body.substring(fromMarkerIndex + fromMarker.length(), toMarkerIndex);
                    String endText = body.substring(toMarkerIndex + toMarker.length());
                    if (eventDescription.isBlank() || startText.isBlank() || endText.isBlank()) {
                        throw new HappyBotException(EVENT_USAGE);
                    }
                    LocalDateTime startTime = parseDateTime(startText);
                    LocalDateTime endTime = parseDateTime(endText);
                    if (!startTime.isBefore(endTime)) {
                        throw new HappyBotException("An event must start before it ends.");
                    }
                    addTask(new Event(startTime, endTime, eventDescription));
                    break;
                default:
                    throw new HappyBotException("I don't know what that means :-(");
                }
            } catch (HappyBotException e) {
                System.out.println(" Oops! " + e.getMessage() + "\n" + DIVIDER);
            } catch (DateTimeParseException e) {
                System.out.println(" Oops! " + DATE_FORMAT_HINT + "\n" + DIVIDER);
            }
        }

        printGoodbyeMessage();
    }

    /**
     * Starts HappyBot.
     *
     * @param args Command-line arguments, which HappyBot does not use.
     */
    public static void main(String[] args) {
        new HappyBot(DATA_FILE_PATH).run();
    }
}
