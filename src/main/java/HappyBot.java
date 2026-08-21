import java.util.Scanner;

/**
 * Starts the HappyBot chatbot application.
 */
public class HappyBot {
    // Codex was used to generate ASCII art for HAPPYBOT.
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String divider = "____________________________________________________________";
        Task[] tasks = new Task[100];
        int numberOfTasks = 0;
        String banner = "H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT\n"
                + "H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T\n"
                + "HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T\n"
                + "H   H  A   A  P      P         Y    B   B  O   O    T\n"
                + "H   H  A   A  P      P         Y    BBBB    OOO     T\n";
        System.out.println(divider);
        System.out.print(banner);
        System.out.println("Hello! I'm HappyBot.");
        System.out.println("How can I cheer you up today?");
        System.out.println(divider);
        while (true) {
            String userInput = scanner.nextLine();
            System.out.println(divider);
            if (userInput.equals("bye")) {
                break;
            } else if (userInput.equals("list")) {
                for (int i = 0; i < numberOfTasks; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(divider);
            } else {
                tasks[numberOfTasks] = new Task(userInput);
                numberOfTasks++;
                System.out.printf(" added: %s\n%s\n", userInput, divider);
            }
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }
}
