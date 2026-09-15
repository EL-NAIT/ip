# HappyBot User Guide

HappyBot manages todos, deadlines, and events through a chat-style command interface.

## Viewing statistics

Use `stats` to see a compact summary of the current Monday-to-Sunday week.

```text
stats
```

The reply shows the total number of tasks in the list, tasks completed during the current week
by type, and uncompleted deadlines due during the same week.

```text
 Here are your statistics for this week:
 Total tasks: 4
 Completed this week: 3
   To-dos: 1
   Deadlines: 1
   Events: 1
 Uncompleted deadlines due this week: 1
```

HappyBot records the calendar day when a task is marked as done. Marking an already completed
task does not change that date. Unmarking clears it, and marking the task done again records the
new day. Completion times are not recorded.

Tasks completed before completion-date tracking was introduced remain completed but are not
included in the current week's completed-task count.

`stats` accepts no arguments. For example, `stats week` produces:

```text
 Oops! Use: stats.
```
