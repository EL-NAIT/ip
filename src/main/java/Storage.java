import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves HappyBot tasks to a text file and loads them back on startup.
 */
public class Storage {
    /** Markers written as the first field of a saved task line. */
    private static final String TASK_TYPE_TODO = "T";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";

    /** Number of fields a saved line holds for each task type. */
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    /** Text placed between the fields of a saved task line. */
    private static final String FIELD_SEPARATOR = " | ";

    /** Regular expression matching FIELD_SEPARATOR, because String.split() takes a regex. */
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    /** Completion field value written for a task that is done. */
    private static final String COMPLETION_STATUS_DONE = "1";

    /** Completion field value written for a task that is not done. */
    private static final String COMPLETION_STATUS_NOT_DONE = "0";

    /** Suffix of the temporary file used to make each save all-or-nothing. */
    private static final String TEMPORARY_FILE_SUFFIX = ".tmp";

    private final Path filePath;

    /** Number of unreadable lines skipped by the most recent call to loadTasks(). */
    private int skippedLineCount;

    /**
     * Creates storage that reads and writes the specified file.
     *
     * @param filePath The location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the number of unreadable lines skipped by the most recent call to loadTasks().
     *
     * @return The number of skipped lines.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Returns the location of the task data file.
     *
     * @return The data file path.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Saves all tasks to the data file, replacing any previous contents.
     *
     * <p>The lines are written to a temporary file that then replaces the data file, so an
     * interrupted save leaves the previous data file intact instead of a half-written one.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        // A data file named without a directory has no parent to create.
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(formatTask(task));
        }

        // resolveSibling() places the temporary file in the same directory as the data file.
        // ATOMIC_MOVE works only within one file system, so a system temporary directory,
        // which may sit on another file system, cannot be used here.
        Path temporaryFile = filePath.resolveSibling(filePath.getFileName() + TEMPORARY_FILE_SUFFIX);
        try {
            // Filling the temporary file first leaves the previous data file untouched until the
            // new contents are complete, so an interrupted save cannot truncate saved tasks.
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, filePath,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            // A successful move consumes the temporary file, so this only cleans up after a failure.
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads the previously saved tasks from the data file.
     *
     * <p>Lines that do not follow the storage format are skipped and counted rather than
     * abandoning the tasks that were read successfully.
     *
     * @return A modifiable list of the tasks that were read successfully.
     * @throws IOException If the data file exists but cannot be read.
     */
    public List<Task> loadTasks() throws IOException {
        skippedLineCount = 0;
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("The data file path is not a regular file: " + filePath);
        }

        List<String> taskLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String taskLine : taskLines) {
            if (taskLine.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(taskLine));
            } catch (IllegalArgumentException e) {
                skippedLineCount++;
            }
        }

        return tasks;
    }

    /**
     * Converts one task into a line of the storage format.
     *
     * @param task The task to format.
     * @return The formatted task line.
     */
    private String formatTask(Task task) {
        String completionStatus = task.isDone() ? COMPLETION_STATUS_DONE : COMPLETION_STATUS_NOT_DONE;

        if (task instanceof ToDo) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_TODO, completionStatus, task.getDescription());
        }

        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_DEADLINE, completionStatus,
                    deadline.getDescription(), deadline.getEndDate());
        }

        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_EVENT, completionStatus,
                    event.getDescription(), event.getStartTime(), event.getEndTime());
        }

        throw new IllegalArgumentException("Unsupported task type.");
    }

    /**
     * Converts one line of the storage format back into a task.
     *
     * @param taskLine The saved line to parse.
     * @return The task described by the line.
     * @throws IllegalArgumentException If the line does not follow the storage format.
     */
    private Task parseTask(String taskLine) {
        String[] fields = taskLine.split(FIELD_SEPARATOR_REGEX);
        checkFieldsAreFilled(fields, taskLine);

        String taskType = fields[0];
        Task task;

        switch (taskType) {
        case TASK_TYPE_TODO:
            checkFieldCount(fields, FIELD_COUNT_TODO, taskLine);
            task = new ToDo(fields[2]);
            break;
        case TASK_TYPE_DEADLINE:
            checkFieldCount(fields, FIELD_COUNT_DEADLINE, taskLine);
            task = new Deadline(fields[2], fields[3]);
            break;
        case TASK_TYPE_EVENT:
            checkFieldCount(fields, FIELD_COUNT_EVENT, taskLine);
            task = new Event(fields[3], fields[4], fields[2]);
            break;
        default:
            throw new IllegalArgumentException("Unsupported task type in data file: " + taskLine);
        }

        if (parseCompletionStatus(fields[1], taskLine)) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Verifies that a saved line was split into the number of fields its task type requires.
     *
     * @param fields The fields parsed from the line.
     * @param expectedCount The number of fields the task type requires.
     * @param taskLine The original line, included in the error message.
     * @throws IllegalArgumentException If the line holds the wrong number of fields.
     */
    private void checkFieldCount(String[] fields, int expectedCount, String taskLine) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Wrong number of fields in data file: " + taskLine);
        }
    }

    /**
     * Verifies that a saved line has at least a type and a completion status, and no blank field.
     *
     * @param fields The fields parsed from the line.
     * @param taskLine The original line, included in the error message.
     * @throws IllegalArgumentException If a field is missing or blank.
     */
    private void checkFieldsAreFilled(String[] fields, String taskLine) {
        if (fields.length < FIELD_COUNT_TODO) {
            throw new IllegalArgumentException("Incomplete task in data file: " + taskLine);
        }

        for (String field : fields) {
            if (field.isBlank()) {
                throw new IllegalArgumentException("Blank field in data file: " + taskLine);
            }
        }
    }

    /**
     * Converts the completion field of a saved line into a completion state.
     *
     * @param completionStatus The completion field read from the line.
     * @param taskLine The original line, included in the error message.
     * @return True if the field marks the task as done.
     * @throws IllegalArgumentException If the field is neither the done nor the not-done value.
     */
    private boolean parseCompletionStatus(String completionStatus, String taskLine) {
        if (completionStatus.equals(COMPLETION_STATUS_DONE)) {
            return true;
        }

        if (completionStatus.equals(COMPLETION_STATUS_NOT_DONE)) {
            return false;
        }

        throw new IllegalArgumentException("Unknown completion status in data file: " + taskLine);
    }
}
