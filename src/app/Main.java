package app;

import controller.PersonalTaskManager;
import gateway.ICalGateway;
import model.*;
import repository.TaskRepository;
import util.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static void pause(String label) {
        System.out.print("\n[press ENTER for " + label + "] ");
        SCANNER.nextLine();
        System.out.println();
    }

    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        ActivityLog activityLog = new ActivityLog();
        ICalGateway icalGateway = new ICalGateway();
        PersonalTaskManager manager = new PersonalTaskManager(repository, activityLog, icalGateway);

        // Load persisted data if it exists
        JsonPersistence.load(repository, activityLog);

        System.out.println("==============================================");
        System.out.println("  Personal Task Manager - PoC Demo");
        System.out.println("==============================================\n");

        pause("Step 1: create collaborators");

        // ---- Step 1: Create Collaborators ----
        System.out.println("--- Step 1: Create Collaborators ---");
        Collaborator seniorCollab = manager.createCollaborator("Alice", CollaboratorCategoryType.SENIOR);
        Collaborator intermediateCollab = manager.createCollaborator("Bob", CollaboratorCategoryType.INTERMEDIATE);
        Collaborator juniorCollab = manager.createCollaborator("Charlie", CollaboratorCategoryType.JUNIOR);
        System.out.println("  Created: " + seniorCollab);
        System.out.println("  Created: " + intermediateCollab);
        System.out.println("  Created: " + juniorCollab);
        System.out.println();

        // ---- Step 2: Create Projects ----
        System.out.println("--- Step 2: Create Projects ---");
        Project project1 = manager.createProject("Website Redesign", "Redesign the company website");
        Project project2 = manager.createProject("Mobile App", "Develop the mobile application");
        System.out.println("  Created: " + project1);
        System.out.println("  Created: " + project2);
        System.out.println();

        // ---- Step 3: Create Tasks ----
        System.out.println("--- Step 3: Create Tasks ---");
        Task task1 = manager.createTask("Design homepage", "Create wireframes for the homepage",
                PriorityLevel.HIGH, LocalDate.of(2026, 4, 10), null);
        Task task2 = manager.createTask("Write API docs", "Document all REST endpoints",
                PriorityLevel.MEDIUM, LocalDate.of(2026, 4, 15), null);
        Task task3 = manager.createTask("Fix login bug", "Login fails on mobile browsers",
                PriorityLevel.HIGH, LocalDate.of(2026, 4, 5), null);
        System.out.println("  Created: " + task1);
        System.out.println("  Created: " + task2);
        System.out.println("  Created: " + task3);
        System.out.println();

        pause("Steps 4-8: update, assign to projects, subtasks, tags");

        // ---- Step 4: Update Task ----
        System.out.println("--- Step 4: Update Task ---");
        manager.updateTask(task2.getTaskId(), null, "Document all REST and GraphQL endpoints",
                PriorityLevel.HIGH, null, null);
        System.out.println("  Updated task2 description and priority.");
        System.out.println("  " + task2);
        System.out.println();

        // ---- Step 5: Assign Task to Project ----
        System.out.println("--- Step 5: Assign Task to Project ---");
        manager.assignTaskToProject(task1.getTaskId(), project1.getProjectId());
        manager.assignTaskToProject(task2.getTaskId(), project1.getProjectId());
        manager.assignTaskToProject(task3.getTaskId(), project2.getProjectId());
        System.out.println("  task1 -> " + project1.getName());
        System.out.println("  task2 -> " + project1.getName());
        System.out.println("  task3 -> " + project2.getName());
        System.out.println();

        // ---- Step 6: Create Subtask ----
        System.out.println("--- Step 6: Create Subtask ---");
        Subtask sub1 = manager.createSubtask(task1.getTaskId(), "Draft wireframe v1");
        Subtask sub2 = manager.createSubtask(task1.getTaskId(), "Review wireframe with team");
        System.out.println("  Created: " + sub1);
        System.out.println("  Created: " + sub2);
        System.out.println();

        // ---- Step 7: Complete Subtask ----
        System.out.println("--- Step 7: Complete Subtask ---");
        manager.setSubtaskCompletion(sub1.getSubtaskId(), CompletionStatus.COMPLETED);
        System.out.println("  Completed: " + sub1);
        System.out.println("  Parent task status still: " + task1.getCompletionStatus());
        System.out.println();

        // ---- Step 8: Add and Remove Tag ----
        System.out.println("--- Step 8: Add and Remove Tag ---");
        manager.addTagToTask(task1.getTaskId(), "design");
        manager.addTagToTask(task1.getTaskId(), "urgent");
        System.out.println("  Tags on task1: " + task1.getTags());
        manager.removeTagFromTask(task1.getTaskId(), "urgent");
        System.out.println("  After removing 'urgent': " + task1.getTags());
        System.out.println();

        pause("Steps 9-10: collaborator assignment + overload rejection");

        // ---- Step 9: Assign Collaborator (success) ----
        System.out.println("--- Step 9: Assign Collaborator (success) ---");
        manager.assignTaskToCollaborator(task1.getTaskId(), seniorCollab.getCollaboratorId());
        System.out.println("  Assigned " + seniorCollab.getName() + " to task1.");
        manager.assignTaskToCollaborator(task2.getTaskId(), seniorCollab.getCollaboratorId());
        System.out.println("  Assigned " + seniorCollab.getName() + " to task2.");
        System.out.println();

        // ---- Step 10: Assign Beyond Limit (rejection) ----
        System.out.println("--- Step 10: Assign Beyond Collaborator Limit ---");
        try {
            manager.assignTaskToCollaborator(task3.getTaskId(), seniorCollab.getCollaboratorId());
            System.out.println("  ERROR: Should have been rejected!");
        } catch (IllegalStateException e) {
            System.out.println("  Rejected as expected: " + e.getMessage());
        }
        System.out.println();

        pause("Step 11: recurring task");

        // ---- Step 11: Create Recurring Task ----
        System.out.println("--- Step 11: Create Recurring Task ---");
        RecurrencePattern weeklyPattern = new RecurrencePattern(
                RecurrenceType.WEEKLY, 1,
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 29));
        Task recurringTask = manager.createTask("Weekly standup notes", "Write standup summary",
                PriorityLevel.LOW, LocalDate.of(2026, 4, 1), weeklyPattern);
        System.out.println("  Created recurring task: " + recurringTask);
        System.out.println("  Occurrences generated:");
        for (TaskOccurrence occ : recurringTask.getOccurrences()) {
            System.out.println("    " + occ);
        }
        System.out.println();

        pause("Step 12: complete and cancel");

        // ---- Step 12: Complete and Cancel Tasks ----
        System.out.println("--- Step 12: Complete and Cancel Tasks ---");
        manager.completeTask(task3.getTaskId());
        System.out.println("  Completed: " + task3);
        manager.cancelTask(task2.getTaskId());
        System.out.println("  Cancelled: " + task2);
        System.out.println();

        pause("Step 12b: STATE MACHINE — guards and reopen (iteration 4 highlight)");

        // ---- Step 12b: State Machine Guards and Reopen ----
        System.out.println("--- Step 12b: State Machine Guards and Reopen ---");

        System.out.println("  Attempting to complete already-completed task...");
        try {
            manager.completeTask(task3.getTaskId());
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }

        System.out.println("  Attempting to cancel already-cancelled task...");
        try {
            manager.cancelTask(task2.getTaskId());
        } catch (IllegalStateException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }

        System.out.println("  Reopening completed task...");
        manager.reopenTask(task3.getTaskId());
        System.out.println("  Reopened: " + task3);

        System.out.println("  Reopening cancelled task...");
        manager.reopenTask(task2.getTaskId());
        System.out.println("  Reopened: " + task2);
        System.out.println();

        pause("Steps 13-15: search, CSV export, CSV import");

        // ---- Step 13: Search Tasks ----
        System.out.println("--- Step 13: Search Tasks ---");

        // default search: all open tasks
        System.out.println("  [All open tasks, sorted by due date]");
        List<Task> openTasks = manager.searchTasks(null);
        for (Task t : openTasks) {
            System.out.println("    " + t);
        }

        // search by keyword
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword("homepage");
        System.out.println("\n  [Search by keyword 'homepage']");
        List<Task> keywordResults = manager.searchTasks(criteria);
        for (Task t : keywordResults) {
            System.out.println("    " + t);
        }

        // search by status
        SearchCriteria statusCriteria = new SearchCriteria();
        statusCriteria.setStatus(CompletionStatus.COMPLETED);
        System.out.println("\n  [Search by status COMPLETED]");
        List<Task> completedResults = manager.searchTasks(statusCriteria);
        for (Task t : completedResults) {
            System.out.println("    " + t);
        }

        // search by project
        SearchCriteria projectCriteria = new SearchCriteria();
        projectCriteria.setProjectName("Website Redesign");
        System.out.println("\n  [Search by project 'Website Redesign']");
        List<Task> projectResults = manager.searchTasks(projectCriteria);
        for (Task t : projectResults) {
            System.out.println("    " + t);
        }
        System.out.println();

        // ---- Step 14: Export to CSV ----
        System.out.println("--- Step 14: Export to CSV ---");
        String exportPath = "export_output.csv";
        manager.exportSearchResultToCSV(null, exportPath);
        System.out.println("  Exported open tasks to: " + exportPath);
        System.out.println();

        // ---- Step 15: Import from CSV ----
        System.out.println("--- Step 15: Import from CSV ---");
        String sampleCsvPath = "sample_import.csv";
        createSampleCSV(sampleCsvPath);
        System.out.println("  Created sample CSV: " + sampleCsvPath);
        manager.importFromCSV(sampleCsvPath);
        System.out.println("  Import complete. Current task count: " + repository.getAllTasks().size());

        // show imported tasks
        System.out.println("  All tasks after import:");
        for (Task t : repository.getAllTasks()) {
            System.out.println("    " + t);
        }
        System.out.println();

        pause("Step 16: activity log (now persisted)");

        // ---- Step 16: Print Activity Log ----
        System.out.println("--- Step 16: Activity Log ---");
        for (ActivityEntry entry : activityLog.getEntries()) {
            System.out.println("  " + entry);
        }
        System.out.println();

        pause("Steps 17-18: iCal export, overloaded collaborators");

        // ---- Step 17: Export to iCal ----
        System.out.println("--- Step 17: Export to iCal ---");
        // a) Single task
        manager.exportTaskToIcal(task1.getTaskId(), "task_single.ics");
        System.out.println("  Single task exported: task_single.ics");
        // b) All tasks in a project
        manager.exportProjectTasksToIcal(project1.getProjectId(), "project_website.ics");
        System.out.println("  Project tasks exported: project_website.ics");
        // c) Filtered list (open tasks only)
        SearchCriteria icalCriteria = new SearchCriteria();
        icalCriteria.setStatus(CompletionStatus.OPEN);
        manager.exportFilteredTasksToIcal(icalCriteria, "filtered_open.ics");
        System.out.println("  Filtered open tasks exported: filtered_open.ics");
        System.out.println();

        // ---- Step 18: List Overloaded Collaborators ----
        System.out.println("--- Step 18: Overloaded Collaborators ---");
        List<Collaborator> overloaded = manager.getOverloadedCollaborators();
        if (overloaded.isEmpty()) {
            System.out.println("  No collaborators are currently overloaded.");
        } else {
            for (Collaborator c : overloaded) {
                int count = repository.countOpenTasksForCollaborator(c);
                System.out.println("  OVERLOADED: " + c.getName()
                        + " (" + c.getCategoryType() + ") — "
                        + count + "/" + c.getTaskLimit() + " open tasks");
            }
        }
        System.out.println();

        // ---- Save state to JSON ----
        JsonPersistence.save(repository, activityLog);
        System.out.println("  Data saved to data/ directory.");

        System.out.println("\n==============================================");
        System.out.println("  Demo Complete");
        System.out.println("==============================================");
    }

    private static void createSampleCSV(String filePath) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filePath))) {
            writer.println("TaskName,Description,Subtask,Status,Priority,DueDate,"
                    + "ProjectName,ProjectDescription,Collaborator,CollaboratorCategory");
            writer.println("Setup CI pipeline,Configure GitHub Actions,,OPEN,HIGH,2026-05-01,"
                    + "DevOps Project,Infrastructure automation,,");
            writer.println("Write unit tests,Cover auth module,Test login flow,OPEN,MEDIUM,2026-05-10,"
                    + "Website Redesign,Redesign the company website,Diana,JUNIOR");
            writer.println("Deploy staging,Push to staging env,,OPEN,LOW,2026-05-15,"
                    + "DevOps Project,Infrastructure automation,Eve,INTERMEDIATE");
        } catch (java.io.IOException e) {
            System.out.println("  [ERROR] Could not create sample CSV: " + e.getMessage());
        }
    }
}
