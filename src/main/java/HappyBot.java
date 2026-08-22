import java.util.Scanner;

/**
 * Starts the HappyBot chatbot application.
 */
public class HappyBot {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT\n"
            + "H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T\n"
            + "HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T\n"
            + "H   H  A   A  P      P         Y    B   B  O   O    T\n"
            + "H   H  A   A  P      P         Y    BBBB    OOO     T\n";

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
     * @param tasks the tasks to display
     * @param numberOfTasks the number of tasks stored in the array
     */
    private static void printTaskList(Task[] tasks, int numberOfTasks) {
        StringBuilder taskList = new StringBuilder(" Here are the tasks in your list:\n");

        for (int i = 0; i < numberOfTasks; i++) {
            taskList.append(" ")
                    .append(i + 1)
                    .append(".")
                    .append(tasks[i])
                    .append("\n");
        }

        taskList.append(DIVIDER);
        System.out.println(taskList);
    }

    /**
     * Marks a selected task as completed.
     *
     * @param tasks all tasks
     * @param numberOfTasks the number of tasks stored in the array
     * @param userInput the command entered by the user
     * @throws HappyBotException if the task number is invalid
     */
    private static void markTask(Task[] tasks, int numberOfTasks, String userInput)
            throws HappyBotException {
        if (numberOfTasks == 0) {
            throw new HappyBotException("There are no tasks to mark.");
        }

        String taskNumberText = userInput.substring("mark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > numberOfTasks) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        Task taskToMark = tasks[taskNumber - 1];
        taskToMark.markAsDone();
        System.out.println(" Nice! I've marked this task as done:\n"
                + "   " + taskToMark + "\n"
                + DIVIDER);
    }

    /**
     * Marks a selected task as not completed.
     *
     * @param tasks all tasks
     * @param numberOfTasks the number of tasks stored in the array
     * @param userInput the command entered by the user
     * @throws HappyBotException if the task number is invalid
     */
    private static void unmarkTask(Task[] tasks, int numberOfTasks, String userInput)
            throws HappyBotException {
        if (numberOfTasks == 0) {
            throw new HappyBotException("There are no tasks to unmark.");
        }

        String taskNumberText = userInput.substring("unmark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new HappyBotException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > numberOfTasks) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        Task taskToUnmark = tasks[taskNumber - 1];
        taskToUnmark.unmarkAsDone();
        System.out.println(" OK, I've marked this task as not done yet:\n"
                + "   " + taskToUnmark + "\n"
                + DIVIDER);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Assumption here is that the number of tasks will not exceed 100
        Task[] tasks = new Task[100];
        int numberOfTasks = 0;
        boolean isRunning = true;
        printWelcomeMessage();

        while (isRunning) {
            String userInput = scanner.nextLine();
            System.out.println(DIVIDER);
            String[] instructionArray = userInput.split(" ", 2);
            String command = instructionArray[0];
            String body;
            if (instructionArray.length == 2) {
                body = instructionArray[1];
            } else {
                body = "";
            }
            try {
                switch (command) {
                case "bye":
                    isRunning = false;
                    break;
                case "list":
                    printTaskList(tasks, numberOfTasks);
                    break;
                case "mark":
                    markTask(tasks, numberOfTasks, userInput);
                    break;
                case "unmark":
                    unmarkTask(tasks, numberOfTasks, userInput);
                    break;
                case "todo":
                    if (body.isBlank()) {
                        throw new HappyBotException("The description of a todo cannot be empty.");
                    }
                    tasks[numberOfTasks] = new ToDo(body);
                    numberOfTasks++;
                    System.out.println(" Got it. I've added this task:\n"
                            + "   " + tasks[numberOfTasks - 1] + "\n"
                            + " Now you have " + numberOfTasks + " tasks in the list.\n"
                            + DIVIDER);
                    break;
                case "deadline":
                    String deadlineMarker = " /by ";
                    int deadlineMarkerIndex = body.indexOf(deadlineMarker);
                    if (deadlineMarkerIndex < 0) {
                        throw new HappyBotException("Use: deadline <description> /by <due date>.");
                    }
                    String deadlineDescription = body.substring(0, deadlineMarkerIndex);
                    String dueDate = body.substring(deadlineMarkerIndex + deadlineMarker.length());
                    if (deadlineDescription.isBlank() || dueDate.isBlank()) {
                        throw new HappyBotException("Use: deadline <description> /by <due date>");
                    }
                    tasks[numberOfTasks] = new Deadline(deadlineDescription, dueDate);
                    numberOfTasks++;
                    System.out.println(" Got it. I've added this task:\n"
                            + "   " + tasks[numberOfTasks - 1] + "\n"
                            + " Now you have " + numberOfTasks + " tasks in the list.\n"
                            + DIVIDER);
                    break;
                case "event":
                    String fromMarker = " /from ";
                    String toMarker = " /to ";
                    int fromMarkerIndex = body.indexOf(fromMarker);
                    int toMarkerIndex = body.indexOf(toMarker, fromMarkerIndex + fromMarker.length());
                    if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
                        throw new HappyBotException("Use: event <description> /from <startDate> /to <endDate>.");
                    }
                    String eventDescription = body.substring(0, fromMarkerIndex);
                    String startTime = body.substring(fromMarkerIndex + fromMarker.length(), toMarkerIndex);
                    String endTime = body.substring(toMarkerIndex + toMarker.length());
                    if (eventDescription.isBlank() || startTime.isBlank() || endTime.isBlank()) {
                        throw new HappyBotException("Use: event <description> /from <startDate> /to <endDate>.");
                    }
                    tasks[numberOfTasks] = new Event(startTime, endTime, eventDescription);
                    numberOfTasks++;
                    System.out.println(" Got it. I've added this task:\n"
                            + "   " + tasks[numberOfTasks - 1] + "\n"
                            + " Now you have " + numberOfTasks + " tasks in the list.\n"
                            + DIVIDER);
                    break;
                default:
                    throw new HappyBotException("I don't know what that means :-(");
                }
            } catch (HappyBotException e) {
                System.out.println(" Oops! " + e.getMessage() + "\n" + DIVIDER);
            }
        }

        printGoodbyeMessage();
    }
}
