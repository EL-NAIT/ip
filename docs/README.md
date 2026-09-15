# HappyBot User Guide

HappyBot manages todos, deadlines, and events through a chat-style command interface.

## Command input and duplicates

HappyBot accepts accidental leading, trailing, repeated, and tab whitespace, and stores task
descriptions with one space between words. It still checks the command structure strictly:
`bye`, `list`, and `stats` take no arguments; `mark`, `unmark`, and `delete` take one positive
task number; a deadline has one `/by`; and an event has one `/from` followed by one `/to`.

The `due` command searches one entire calendar day, so it accepts a date only. For example,
`due 2019-12-02` is valid, while `due 2019-12-02 1800` is rejected rather than silently ignoring
the time.

HappyBot rejects a task with details already in the list and shows an error instead of ending the
session. Task details are the type, case-insensitive description, and any due/start/end date and
time; completion status does not make a duplicate distinct.

Only one HappyBot session can use a data file at a time. If a second session starts, it reports
that the task list is already open and asks the user to close the first session before trying
again.

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
