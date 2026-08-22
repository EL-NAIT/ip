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
4. After that, locate the `src/main/java/HappyBot.java` file, right-click it, and choose `Run HappyBot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   H   H   AAA   PPPP   PPPP   Y     Y BBBB    OOO   TTTTT
   H   H  A   A  P   P  P   P   Y   Y  B   B  O   O    T
   HHHHH  AAAAA  PPPP   PPPP     Y Y   BBBB   O   O    T
   H   H  A   A  P      P         Y    B   B  O   O    T
   H   H  A   A  P      P         Y    BBBB    OOO     T
   Hello! I'm HappyBot.
   What can I do for you?
   ____________________________________________________________
   
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI Use

Codex was used for the following:

1. Updating this README.
2. Assisting with Level 0 increments.
3. Explaining Java and object-oriented design decisions, including where to validate command input.
4. Implementing user input parsing with string functions.
5. Helping format HappyBot’s console output.
6. Creating, updating, and running console UI test cases in `test/ui-test-plan.md`.

All AI-generated or AI-suggested code and test cases were personally reviewed before use.
