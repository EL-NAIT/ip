import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the HappyBot chatbot application.
 *
 * <p>A HappyBot object owns the task list, the storage that saves it, and the user interface
 * that reads commands and prints replies. Ui builds the console text and Parser reads the
 * typed command, which leaves this class with the task list and what each command does to it.
 */
public class HappyBot {
    private static final Path DATA_FILE_PATH = Path.of("data", "HappyBot.txt");

    /** User interface that reads the commands and prints every reply. */
    private final Ui ui;

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
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new ArrayList<>();
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
                ui.showSkippedLinesNotice(storage.getSkippedLineCount());
            }
        } catch (IOException e) {
            ui.showLoadingError(storage.getFilePath());
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
            ui.showSavingError(storage.getFilePath());
        }
    }

    /**
     * Returns the task that a command's task number names.
     *
     * @param taskNumber The number typed by the user, counted from one.
     * @return The task holding that number.
     * @throws HappyBotException If no task holds that number.
     */
    private Task getTask(int taskNumber) throws HappyBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        return tasks.get(taskNumber - 1);
    }

    /**
     * Adds a task, saves the updated list, and confirms the addition to the user.
     *
     * @param taskToAdd The task to add.
     */
    private void addTask(Task taskToAdd) {
        tasks.add(taskToAdd);
        saveTasks();
        ui.showAddedTask(taskToAdd, tasks.size());
    }

    /**
     * Marks a selected task as completed.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private void markTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to mark.");
        }

        Task taskToMark = getTask(Parser.parseTaskNumber(body));
        taskToMark.markAsDone();
        saveTasks();
        ui.showMarkedTask(taskToMark);
    }

    /**
     * Marks a selected task as not completed.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private void unmarkTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to unmark.");
        }

        Task taskToUnmark = getTask(Parser.parseTaskNumber(body));
        taskToUnmark.unmarkAsDone();
        saveTasks();
        ui.showUnmarkedTask(taskToUnmark);
    }

    /**
     * Deletes a selected task from the list.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private void deleteTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to delete.");
        }

        int taskNumber = Parser.parseTaskNumber(body);
        Task taskToDelete = getTask(taskNumber);
        // Removing by position avoids List.remove(Object), which would search for an equal task.
        tasks.remove(taskNumber - 1);
        saveTasks();
        ui.showDeletedTask(taskToDelete, tasks.size());
    }

    /**
     * Shows the deadlines due on the specified date.
     *
     * <p>Each deadline is collected under the number it has in the full list, so one shown here
     * can be marked or deleted with the number displayed beside it.
     *
     * @param date The date the user asked about.
     */
    private void showDeadlinesDueOn(LocalDate date) {
        // Reusing the task display keeps one source of truth for how a date is written.
        String displayedDate = DatedTask.formatDate(date);
        // A LinkedHashMap keeps the deadlines in list order while remembering each task number.
        Map<Integer, Task> matchingTasks = new LinkedHashMap<>();

        for (int i = 0; i < tasks.size(); i++) {
            // A todo and an event have no due date, so only a deadline can match.
            if (tasks.get(i) instanceof Deadline deadline
                    && deadline.getEndDate().toLocalDate().equals(date)) {
                matchingTasks.put(i + 1, deadline);
            }
        }

        ui.showDeadlinesDueOn(displayedDate, matchingTasks);
    }

    /**
     * Greets the user, then reads and carries out commands until the session ends.
     */
    public void run() {
        boolean isRunning = true;

        ui.showWelcome();
        loadSavedTasks();

        // Stopping at the end of the input also ends the session cleanly when no bye is typed.
        while (isRunning && ui.hasNextCommand()) {
            String userInput = ui.readCommand();
            ui.showDivider();
            String command = Parser.parseCommandWord(userInput);
            String body = Parser.parseCommandBody(userInput);

            try {
                switch (command) {
                case "bye":
                    isRunning = false;
                    break;
                case "list":
                    ui.showTaskList(tasks);
                    break;
                case "mark":
                    markTask(body);
                    break;
                case "unmark":
                    unmarkTask(body);
                    break;
                case "delete":
                    deleteTask(body);
                    break;
                case "due":
                    showDeadlinesDueOn(Parser.parseDueDate(body));
                    break;
                case "todo":
                    addTask(Parser.parseToDo(body));
                    break;
                case "deadline":
                    addTask(Parser.parseDeadline(body));
                    break;
                case "event":
                    addTask(Parser.parseEvent(body));
                    break;
                default:
                    throw new HappyBotException("I don't know what that means :-(");
                }
            } catch (HappyBotException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showGoodbye();
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
