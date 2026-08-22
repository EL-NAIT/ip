# HappyBot UI Test Plan

## Runner configuration

**Working directory:** `.`

### Build command

```sh
source /Users/tianle/.sdkman/bin/sdkman-init.sh && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p /private/tmp/happybot-ui-test-classes && javac -d /private/tmp/happybot-ui-test-classes src/main/java/HappyBot.java src/main/java/HappyBotException.java src/main/java/Task.java src/main/java/ToDo.java src/main/java/Deadline.java src/main/java/Event.java
```

### Run command

```sh
source /Users/tianle/.sdkman/bin/sdkman-init.sh && sdk use java 25.0.3.fx-zulu >/dev/null && java -cp /private/tmp/happybot-ui-test-classes HappyBot
```

## Test cases

### TC-01: Exit the program

**Aim:** Confirm that HappyBot displays its welcome and farewell messages when the user exits.

#### Inputs

```text
bye
```

#### Expected output

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
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-06: Handle invalid commands without exiting

**Aim:** Confirm that HappyBot reports invalid command inputs and continues accepting commands.

#### Inputs

```text
todo
deadline return book
event project meeting /from Mon 2pm
mark abc
unmark 1
blah
bye
```

#### Expected output

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
____________________________________________________________
 Oops! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Oops! Use: deadline <description> /by <due date>.
____________________________________________________________
____________________________________________________________
 Oops! Use: event <description> /from <startDate> /to <endDate>.
____________________________________________________________
____________________________________________________________
 Oops! There are no tasks to mark.
____________________________________________________________
____________________________________________________________
 Oops! There are no tasks to unmark.
____________________________________________________________
____________________________________________________________
 Oops! I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-02: Mark and unmark a task

**Aim:** Confirm that marking and unmarking a task changes the status shown in the task list.

#### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

#### Expected output

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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-05: Add deadline and event tasks

**Aim:** Confirm that HappyBot stores the specified deadline and event details as strings.

#### Inputs

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

#### Expected output

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
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-03: Reject a todo without a description

**Aim:** Confirm that HappyBot rejects a todo command with no task description.

#### Inputs

```text
todo
bye
```

#### Expected output

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
____________________________________________________________
 Oops! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### TC-04: Add a todo with a description

**Aim:** Confirm that HappyBot adds and displays a todo with its task-type marker.

#### Inputs

```text
todo read book
list
bye
```

#### Expected output

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
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
