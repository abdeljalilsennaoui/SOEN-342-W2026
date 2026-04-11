# SOEN 342 — Personal Task Manager

**Course:** SOEN 342 — Software Requirements and Deployment (Winter 2026, Concordia University)

**Team:**
- Abdeljalil Sennaoui (40117162)
- Mohammad Almasri (40248819)
- Subaanky Krishnapillai (40128716)

## Overview

A Personal Task Manager proof-of-concept built in Java with Maven. It demonstrates GRASP patterns (Controller, Creator, Information Expert, Low Coupling, High Cohesion) and includes a protocol state machine governing Task lifecycle transitions.

Features include:
- Create, update, complete, cancel, and reopen tasks
- Protocol state machine enforcement (only OPEN tasks can be completed/cancelled; completed/cancelled tasks can be reopened)
- Organize tasks into projects
- Create subtasks under tasks
- Tag tasks with keywords
- Assign collaborators with category-based task limits (Junior: 10, Intermediate: 5, Senior: 2)
- Recurring tasks with daily, weekly, or monthly occurrences
- Search tasks by keyword, status, priority, project, or date range
- Import and export tasks via CSV
- Export tasks to iCalendar (.ics) format
- JSON persistence (data saved/loaded from `data/` directory)
- OCL constraints enforced in code
- Full activity log of all operations

## Prerequisites

- **Java 11** or higher (JDK)
- **Maven 3.6+**

Verify your installation:
```bash
java -version
mvn -version
```

## Project Structure

```
src/
├── app/            Main.java (entry point — runs the demo)
├── controller/     PersonalTaskManager.java (handles all system operations)
├── gateway/        ICalGateway.java (iCalendar export via ical4j)
├── model/          Domain classes: Task, Subtask, Project, Collaborator,
│                   Tag, RecurrencePattern, TaskOccurrence, ActivityEntry, ActivityLog
├── repository/     TaskRepository.java (in-memory storage and lookup)
└── util/           Enums, SearchCriteria, CSVHelper, JsonPersistence
data/               JSON persistence files (runtime, gitignored)
docs/
├── iteration1/     Domain model, use cases
├── iteration2/     UML class diagram, interaction diagrams
├── iteration3/     OCL constraints, sequence diagrams
└── iteration4/     Protocol state machine, data model, updated class diagram
```

## How to Build and Run

From the project root directory:

### Build
```bash
mvn package
```

### Run
```bash
java -jar target/personal-task-manager-1.0.jar
```

This runs the full demo script which exercises all system operations:

1. Create collaborators (Senior, Intermediate, Junior)
2. Create projects
3. Create tasks
4. Update a task
5. Assign tasks to projects
6. Create subtasks
7. Complete a subtask
8. Add and remove tags
9. Assign a collaborator to a task (success)
10. Assign beyond collaborator limit (rejected)
11. Create a recurring task with weekly occurrences
12. Complete and cancel tasks
13. **State machine guards**: attempt invalid transitions (rejected), then reopen tasks
14. Search tasks (by default, keyword, status, project)
15. Export search results to CSV
16. Import tasks from CSV
17. Print the full activity log
18. Export to iCalendar (single task, project tasks, filtered list)
19. List overloaded collaborators
20. Save all data to JSON

### Output Files

The demo generates files in the working directory:
- `export_output.csv` — exported task data
- `sample_import.csv` — sample CSV used for the import step
- `task_single.ics`, `project_website.ics`, `filtered_open.ics` — iCalendar exports
- `data/*.json` — persisted tasks, projects, and collaborators

## Task State Machine

Tasks follow a protocol state machine with three states:

```
            complete()              reopen()
  [new] --> OPEN ---------> COMPLETED
              |                  |
              | cancel()         | reopen()
              v                  v
           CANCELLED <----------(back to OPEN)
```

- Only **OPEN** tasks can be completed or cancelled
- **COMPLETED** and **CANCELLED** tasks can be reopened (returns to OPEN)
- Invalid transitions throw `IllegalStateException`

## Demo Video

TODO: add link
