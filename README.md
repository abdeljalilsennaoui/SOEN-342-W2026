# SOEN 342 — Personal Task Manager

**Course:** SOEN 342 — Software Requirements and Deployment (Winter 2026, Concordia University)

**Team:**

- Abdeljalil Sennaoui (40117162)
- Mohammad Almasri (40248819)
- Subaanky Krishnapillai (40128716)

## Demo Videos

- **Scripted demo** — full automated walkthrough of every feature: https://drive.google.com/file/d/1utn3kxB8oSvWEvhkQ2D9QAXPjoax62ne/view?usp=sharing
- **Interactive demo** — short manual session driving the menu-driven CLI by hand: https://drive.google.com/file/d/1J6wQZmi4-2wPcgYiH7Zt-2CgqXAHS3JP/view?usp=sharing

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
└── iteration4/     All final artifacts
```

## How to Build and Run

From the project root directory:

### Build

```bash
mvn package
```

### Run

The app can be launched in either of two modes.

#### Scripted demo (default)

```bash
java -jar target/personal-task-manager-1.0.jar
```

Runs the automated walkthrough shown in the first demo video. It exercises every feature in order, pausing between checkpoints for an ENTER key press. The full step list:

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

#### Interactive mode

```bash
java -jar target/personal-task-manager-1.0.jar --interactive   # or -i
```

Launches a menu-driven CLI (shown in the second demo video) where you drive operations manually. The menu covers every feature — tasks, projects, subtasks, tags, collaborators, search, CSV import/export, iCal export, listings, activity log, and overloaded collaborators. Data loads from `data/` on start and auto-saves on quit (`0` or `q`).

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

## Scripted Demo — Recording Script

Step-by-step commands used to record the first (scripted) demo video. Copy-paste in order. These apply to the default scripted mode only — interactive mode has no fixed script.

### Step 0 — Clean old data

```bash
rm -rf data/
```

### Step 1 — Build

```bash
mvn clean package
```

### Step 2 — Start the demo

```bash
java -jar target/personal-task-manager-1.0.jar
```

### Step 3 — Checkpoint 1 (Steps 1–3: collaborators, projects, tasks)

Press **ENTER**

### Step 4 — Checkpoint 2 (Steps 4–8: update, project assignment, subtasks, tags)

Press **ENTER**

### Step 5 — Checkpoint 3 (Steps 9–10: collaborator overload rejection)

Press **ENTER**

### Step 6 — Checkpoint 4 (Step 11: recurring task)

Press **ENTER**

### Step 7 — Checkpoint 5 (Step 12: complete and cancel)

Press **ENTER**

### Step 8 — Checkpoint 6 (Step 12b: state machine guards + reopen) ⭐

Press **ENTER**

### Step 9 — Checkpoint 7 (Steps 13–15: search, CSV export, CSV import)

Press **ENTER**

Then **open a second terminal tab** (Cmd+T) to show the generated CSV files:

```bash
cat export_output.csv
cat sample_import.csv
```

### Step 10 — Checkpoint 8 (Step 16: activity log)

Back to demo terminal. Press **ENTER**

### Step 11 — Checkpoint 9 (Steps 17–18: iCal export, overloaded collaborators)

Press **ENTER**
_(Demo exits and writes to `data/`.)_

Show the generated iCalendar files:

```bash
cat task_single.ics
```

### Step 12 — List persisted files

```bash
ls data/
```

### Step 13 — Show persisted files

```bash
cat data/tasks.json
cat data/projects.json
cat data/collaborators.json
cat data/activitylog.json
```

### Step 14 — Re-run to prove reload

```bash
java -jar target/personal-task-manager-1.0.jar
```

Wait for Step 1 output. Confirm IDs continue (`COL-4`, `PRJ-3`, `TSK-8` — not 1).

### Step 15 — Exit

Press **Ctrl+C**
