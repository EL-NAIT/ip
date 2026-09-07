# HappyBot

HappyBot is a chatbot project built from this greenfield Java project template. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/happybot/Launcher.java` file, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE).
   IntelliJ then creates a `Launcher` run configuration, which you can start again using the green
   Run button in the toolbar. Alternatively, start the GUI from the project root with
   `./gradlew run`. If the setup is correct, the HappyBot chat window will open with a command field
   and a **Send** button.

HappyBot accepts a command when you press **Enter** or click **Send**. Its existing commands and
saved task data work in both the graphical and console interfaces. The console interface remains
available by running `HappyBot.main()` and starts with this message:

   ```text
   ____________________________________________________________
   H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
   H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
   HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
   H   H  A   A  P      P         Y    B   B  O   O    T
   H   H  A   A  P      P         Y    BBBB    OOO     T
   Hello! I'm HappyBot.
   How can I cheer you up today?
   ____________________________________________________________
   
   ```

## Saved data

HappyBot automatically loads tasks from `data/HappyBot.txt` when it starts and saves the task
list after a task is added, marked, unmarked, or deleted. The file is a UTF-8 plain-text file and
is created together with its parent folder when it is first needed.

Saving uses Java's `Files.write` method to write directly to the data file. This keeps the file
handling concise and avoids temporary-file naming conflicts and file-system-specific atomic move
behavior.

If the data file contains unreadable task lines, HappyBot skips those lines, loads the remaining
tasks, and displays a warning. If the file cannot be read or written, HappyBot reports the problem
instead of ending the session. Tasks remain available in memory for the rest of that session when
a save fails.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI Use

Codex was used for the following:

1. Updating this README.
2. Assisting with Level 0 increments.
3. Explaining Java and object-oriented design decisions, including where to validate command input.
4. Implementing user input parsing with string functions.
5. Helping format HappyBot’s console output.
6. Creating, updating, and running console UI test cases in `src/test/ui-test-plan.md`.
7. Implementing the JavaFX GUI for HappyBot and testing its basic chatbot functionality.
8. Reviewing and simplifying file I/O by replacing temporary-file and atomic-move logic with
   direct UTF-8 writing, and updating the related tests and documentation.

Claude Code was used for the following:

1. Implementing Level 7 tasks saving and loading
2. Handling storage edge cases and errors: corrupted or unreadable data files, a missing data file or folder, failed saves, task text containing the `|` character used as the data file separator, and input that ends without a `bye` command.
3. Implementing the original all-or-nothing save approach using a temporary file and an atomic
   move. This was later replaced with simpler direct writing after review.
4. Refactoring the duplicated task-adding code into an `addTask` method.
5. Explaining the Java library behavior used in that original implementation, including
   `Files.write`, `Path.of`, `Path.resolveSibling`, and `Scanner.hasNextLine`.
6. Updating and running the console UI test cases in `src/test/ui-test-plan.md`, including making each case start from a clean data file and adding cases TC-12 and TC-13.

Claude Code was used for the Level 8 date and time increment:

1. Replacing the `String` due date in `Deadline` and the start and end fields in `Event` with `java.time.LocalDateTime`.
2. Parsing command dates in either the `yyyy-MM-dd` or `d/M/yyyy` pattern, with an optional 24-hour time that defaults to `0000`.
3. Adding the `DatedTask` superclass so `Deadline` and `Event` share one display format.
4. Rejecting an event whose start is not before its end.
5. Adding the `due <date>` command
6. Storing dates in the data file in ISO form so that times survive saving and loading, and skipping saved lines whose dates cannot be read.
7. Updating and running the console UI test cases in `src/test/ui-test-plan.md`, adding cases TC-14 to TC-19.

Claude Code was used for the A-MoreOOP increment: turning `HappyBot` into an object and extracting the `Ui`, `Parser`, 
and `TaskList` classes out of it in five small steps, running the console UI test cases after each one.

Claude Code was used for the A-Packages increment: proposing the `happybot` and
`happybot.task` package structure with the alternatives weighed, moving the classes
into it, and updating the UI test plan runner commands and the README setup path.

Several designs suggested by Claude Code were rejected in favour of simpler code that relies on the Java standard library.

All AI-generated or AI-suggested code and test cases were personally reviewed before use.
