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
                    .append(".[")
                    .append(tasks[i].getStatusIcon())
                    .append("] ")
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
     */
    private static void markTask(Task[] tasks, int numberOfTasks, String userInput) {
        String taskNumberText = userInput.substring("mark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            System.out.println(" Oops! Please provide a valid task number.\n" + DIVIDER);
            return;
        }

        if (taskNumber < 1 || taskNumber > numberOfTasks) {
            System.out.println(" Oops! Please choose a task number between 1 and "
                    + numberOfTasks + ".\n" + DIVIDER);
            return;
        }

        Task taskToMark = tasks[taskNumber - 1];
        taskToMark.markAsDone();
        System.out.println(" Nice! I've marked this task as done:\n"
                + "   [" + taskToMark.getStatusIcon() + "] " + taskToMark + "\n"
                + DIVIDER);
    }

    /**
     * Marks a selected task as not completed.
     *
     * @param tasks all tasks
     * @param numberOfTasks the number of tasks stored in the array
     * @param userInput the command entered by the user
     */
    private static void unmarkTask(Task[] tasks, int numberOfTasks, String userInput) {
        String taskNumberText = userInput.substring("unmark".length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            System.out.println(" Oops! Please provide a valid task number.\n" + DIVIDER);
            return;
        }

        if (taskNumber < 1 || taskNumber > numberOfTasks) {
            System.out.println(" Oops! Please choose a task number between 1 and "
                    + numberOfTasks + ".\n" + DIVIDER);
            return;
        }

        Task taskToUnmark = tasks[taskNumber - 1];
        taskToUnmark.unmarkAsDone();
        System.out.println(" OK, I've marked this task as not done yet:\n"
                + "   [" + taskToUnmark.getStatusIcon() + "] " + taskToUnmark + "\n"
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
            switch (userInput) {
            case "bye":
                isRunning = false;
                break;
            case "list":
                printTaskList(tasks, numberOfTasks);
                break;
            case String markCommand when markCommand.equals("mark") || markCommand.startsWith("mark "):
                markTask(tasks, numberOfTasks, userInput);
                break;
            case String unmarkCommand when unmarkCommand.equals("unmark")
                    || unmarkCommand.startsWith("unmark "):
                unmarkTask(tasks, numberOfTasks, userInput);
                break;
            default:
                tasks[numberOfTasks] = new Task(userInput);
                numberOfTasks++;
                System.out.printf(" added: %s\n%s\n", userInput, DIVIDER);
            }
        }

        printGoodbyeMessage();
    }
}
