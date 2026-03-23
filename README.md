# SOEN 342 — Personal Task Manager (Iteration 2)

**Course:** SOEN 342 — Software Requirements and Deployment (Winter 2026, Concordia University)

**Team:**
- Abdeljalil Sennaoui (40117162)
- Mohammad Almasri (40248819)
- Subaanky Krishnapillai (40128716)

## Overview

A Personal Task Manager proof-of-concept built in plain Java. It demonstrates the design from our UML class diagram and interaction diagrams using GRASP patterns (Controller, Creator, Information Expert, Low Coupling, High Cohesion).

Features include:
- Create, update, complete, and cancel tasks
- Organize tasks into projects
- Create subtasks under tasks
- Tag tasks with keywords
- Assign collaborators with category-based task limits (Junior: 10, Intermediate: 5, Senior: 2)
- Recurring tasks with daily, weekly, or monthly occurrences
- Search tasks by keyword, status, priority, project, or date range
- Import and export tasks via CSV
- Full activity log of all operations

## Prerequisites

- **Java 8** or higher (JDK)

Verify your installation:
```bash
java -version
javac -version
```

## Project Structure

```
src/
├── app/            Main.java (entry point — runs the demo)
├── controller/     PersonalTaskManager.java (handles all system operations)
├── model/          Domain classes: Task, Subtask, Project, Collaborator,
│                   Tag, RecurrencePattern, TaskOccurrence, ActivityEntry, ActivityLog
├── repository/     TaskRepository.java (in-memory storage and lookup)
└── util/           Enums (CompletionStatus, PriorityLevel, RecurrenceType,
                    CollaboratorCategoryType), SearchCriteria, CSVHelper
```

## How to Compile and Run

From the project root directory:

### 1. Compile

```bash
javac -d out src/util/*.java src/model/*.java src/repository/*.java src/controller/*.java src/app/*.java
```

This compiles all source files and places the `.class` files in the `out/` directory.

### 2. Run

```bash
java -cp out app.Main
```

This runs the full demo script which exercises all 18 system operations in order:

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
13. Search tasks (by default, keyword, status, project)
14. Export search results to CSV
15. Import tasks from CSV
16. Print the full activity log

### Output Files

The demo generates two files in the working directory:
- `export_output.csv` — exported task data
- `sample_import.csv` — sample CSV used for the import step

These are generated at runtime and are not checked into the repository.
