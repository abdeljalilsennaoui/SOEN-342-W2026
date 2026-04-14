package app;

import controller.PersonalTaskManager;
import gateway.ICalGateway;
import model.*;
import repository.TaskRepository;
import util.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class InteractiveMain {

    private static final Scanner IN = new Scanner(System.in);
    private static PersonalTaskManager manager;
    private static TaskRepository repo;
    private static ActivityLog log;

    public static void run() {
        repo = new TaskRepository();
        log = new ActivityLog();
        ICalGateway gateway = new ICalGateway();
        manager = new PersonalTaskManager(repo, log, gateway);

        JsonPersistence.load(repo, log);

        System.out.println("==============================================");
        System.out.println("  Personal Task Manager - Interactive Mode");
        System.out.println("==============================================");
        System.out.println("  Data (if any) loaded from data/ directory.");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Select option").trim();
            try {
                switch (choice) {
                    case "1":  createTask(); break;
                    case "2":  updateTask(); break;
                    case "3":  completeTask(); break;
                    case "4":  cancelTask(); break;
                    case "5":  reopenTask(); break;
                    case "6":  createProject(); break;
                    case "7":  assignTaskToProject(); break;
                    case "8":  removeTaskFromProject(); break;
                    case "9":  moveTaskToProject(); break;
                    case "10": createSubtask(); break;
                    case "11": setSubtaskStatus(); break;
                    case "12": addTag(); break;
                    case "13": removeTag(); break;
                    case "14": createCollaborator(); break;
                    case "15": assignCollaborator(); break;
                    case "16": searchTasks(); break;
                    case "17": importCsv(); break;
                    case "18": exportSearchCsv(); break;
                    case "19": exportTaskIcal(); break;
                    case "20": exportProjectIcal(); break;
                    case "21": exportFilteredIcal(); break;
                    case "22": listTasks(); break;
                    case "23": listProjects(); break;
                    case "24": listCollaborators(); break;
                    case "25": showOverloaded(); break;
                    case "26": showActivityLog(); break;
                    case "27": saveData(); break;
                    case "q": case "Q": case "0":
                        running = false; break;
                    default:
                        System.out.println("  Unknown option.");
                }
            } catch (Exception e) {
                System.out.println("  [ERROR] " + e.getMessage());
            }
        }

        JsonPersistence.save(repo, log);
        System.out.println("\n  Data saved to data/ directory. Goodbye.");
    }

    private static void printMenu() {
        System.out.println("\n---------------- Menu ----------------");
        System.out.println("  Tasks:        1 create  2 update  3 complete  4 cancel  5 reopen");
        System.out.println("  Projects:     6 create  7 assign task  8 remove task  9 move task");
        System.out.println("  Subtasks:     10 create  11 set status");
        System.out.println("  Tags:         12 add  13 remove");
        System.out.println("  Collabs:      14 create  15 assign to task");
        System.out.println("  Search/CSV:   16 search  17 import CSV  18 export search to CSV");
        System.out.println("  iCal:         19 task  20 project  21 filtered");
        System.out.println("  Listings:     22 tasks  23 projects  24 collaborators");
        System.out.println("  Other:        25 overloaded  26 activity log  27 save");
        System.out.println("  0 / q         quit (auto-saves)");
        System.out.println("--------------------------------------");
    }

    // ---- helpers ----
    private static String prompt(String label) {
        System.out.print("  " + label + ": ");
        return IN.nextLine();
    }

    private static String promptOptional(String label) {
        System.out.print("  " + label + " (blank = none): ");
        String s = IN.nextLine();
        return s.isEmpty() ? null : s;
    }

    private static LocalDate promptDate(String label, boolean optional) {
        while (true) {
            String s = IN.nextLine();
            String line = s;
            if (line == null) line = "";
            if (optional && line.trim().isEmpty()) return null;
            try {
                return LocalDate.parse(line.trim());
            } catch (DateTimeParseException e) {
                System.out.print("  Invalid date. " + label + " (YYYY-MM-DD): ");
            }
        }
    }

    private static LocalDate askDate(String label, boolean optional) {
        System.out.print("  " + label + (optional ? " (YYYY-MM-DD, blank = none): " : " (YYYY-MM-DD): "));
        return promptDate(label, optional);
    }

    private static PriorityLevel askPriority() {
        while (true) {
            String s = prompt("Priority [LOW/MEDIUM/HIGH]").trim().toUpperCase();
            try { return PriorityLevel.valueOf(s); }
            catch (Exception e) { System.out.println("  Invalid priority."); }
        }
    }

    private static CompletionStatus askStatus() {
        while (true) {
            String s = prompt("Status [OPEN/COMPLETED/CANCELLED]").trim().toUpperCase();
            try { return CompletionStatus.valueOf(s); }
            catch (Exception e) { System.out.println("  Invalid status."); }
        }
    }

    private static CollaboratorCategoryType askCategory() {
        while (true) {
            String s = prompt("Category [JUNIOR/INTERMEDIATE/SENIOR]").trim().toUpperCase();
            try { return CollaboratorCategoryType.valueOf(s); }
            catch (Exception e) { System.out.println("  Invalid category."); }
        }
    }

    // ---- actions ----
    private static void createTask() {
        String title = prompt("Title");
        String description = promptOptional("Description");
        PriorityLevel p = askPriority();
        LocalDate due = askDate("Due date", true);
        RecurrencePattern rec = null;
        String recAns = prompt("Recurring? [y/N]").trim().toLowerCase();
        if (recAns.equals("y") || recAns.equals("yes")) {
            String type = prompt("Type [DAILY/WEEKLY/MONTHLY]").trim().toUpperCase();
            int interval = Integer.parseInt(prompt("Interval (e.g. 1)").trim());
            LocalDate start = askDate("Start date", false);
            LocalDate end = askDate("End date", false);
            rec = new RecurrencePattern(RecurrenceType.valueOf(type), interval, start, end);
        }
        Task t = manager.createTask(title, description, p, due, rec);
        System.out.println("  Created: " + t);
    }

    private static void updateTask() {
        String id = prompt("Task id");
        String title = prompt("New title");
        String desc = promptOptional("New description");
        PriorityLevel p = askPriority();
        LocalDate due = askDate("New due date", true);
        manager.updateTask(id, title, desc, p, due, null);
        System.out.println("  Updated.");
    }

    private static void completeTask() { manager.completeTask(prompt("Task id")); System.out.println("  Completed."); }
    private static void cancelTask()   { manager.cancelTask(prompt("Task id"));   System.out.println("  Cancelled."); }
    private static void reopenTask()   { manager.reopenTask(prompt("Task id"));   System.out.println("  Reopened."); }

    private static void createProject() {
        Project p = manager.createProject(prompt("Project name"), promptOptional("Description"));
        System.out.println("  Created: " + p.getProjectId() + " - " + p.getName());
    }

    private static void assignTaskToProject() {
        manager.assignTaskToProject(prompt("Task id"), prompt("Project id"));
        System.out.println("  Assigned.");
    }

    private static void removeTaskFromProject() {
        manager.removeTaskFromProject(prompt("Task id"));
        System.out.println("  Removed from project.");
    }

    private static void moveTaskToProject() {
        manager.moveTaskToProject(prompt("Task id"), prompt("New project id"));
        System.out.println("  Moved.");
    }

    private static void createSubtask() {
        Subtask s = manager.createSubtask(prompt("Parent task id"), prompt("Subtask title"));
        System.out.println("  Created subtask: " + s.getSubtaskId() + " - " + s.getTitle());
    }

    private static void setSubtaskStatus() {
        manager.setSubtaskCompletion(prompt("Subtask id"), askStatus());
        System.out.println("  Updated.");
    }

    private static void addTag() {
        manager.addTagToTask(prompt("Task id"), prompt("Tag keyword"));
        System.out.println("  Tag added.");
    }

    private static void removeTag() {
        manager.removeTagFromTask(prompt("Task id"), prompt("Tag keyword"));
        System.out.println("  Tag removed.");
    }

    private static void createCollaborator() {
        Collaborator c = manager.createCollaborator(prompt("Name"), askCategory());
        System.out.println("  Created: " + c.getCollaboratorId() + " - " + c.getName());
    }

    private static void assignCollaborator() {
        manager.assignTaskToCollaborator(prompt("Task id"), prompt("Collaborator id"));
        System.out.println("  Assigned.");
    }

    private static SearchCriteria buildCriteria() {
        SearchCriteria c = new SearchCriteria();
        String kw = promptOptional("Keyword");
        if (kw != null) c.setKeyword(kw);
        String st = promptOptional("Status [OPEN/COMPLETED/CANCELLED]");
        if (st != null) c.setStatus(CompletionStatus.valueOf(st.toUpperCase()));
        String pr = promptOptional("Priority [LOW/MEDIUM/HIGH]");
        if (pr != null) c.setPriority(PriorityLevel.valueOf(pr.toUpperCase()));
        String pn = promptOptional("Project name");
        if (pn != null) c.setProjectName(pn);
        System.out.print("  From date (YYYY-MM-DD, blank = none): ");
        LocalDate from = promptDate("From", true);
        if (from != null) c.setFromDate(from);
        System.out.print("  To date (YYYY-MM-DD, blank = none): ");
        LocalDate to = promptDate("To", true);
        if (to != null) c.setToDate(to);
        return c;
    }

    private static void searchTasks() {
        List<Task> results = manager.searchTasks(buildCriteria());
        System.out.println("  " + results.size() + " result(s):");
        for (Task t : results) System.out.println("    " + t);
    }

    private static void importCsv() {
        manager.importFromCSV(prompt("CSV file path"));
        System.out.println("  Imported.");
    }

    private static void exportSearchCsv() {
        System.out.println("  Enter search criteria for export:");
        SearchCriteria c = buildCriteria();
        manager.exportSearchResultToCSV(c, prompt("Output CSV path"));
        System.out.println("  Exported.");
    }

    private static void exportTaskIcal() {
        manager.exportTaskToIcal(prompt("Task id"), prompt("Output .ics path"));
        System.out.println("  Exported.");
    }

    private static void exportProjectIcal() {
        manager.exportProjectTasksToIcal(prompt("Project id"), prompt("Output .ics path"));
        System.out.println("  Exported.");
    }

    private static void exportFilteredIcal() {
        System.out.println("  Enter filter criteria:");
        SearchCriteria c = buildCriteria();
        manager.exportFilteredTasksToIcal(c, prompt("Output .ics path"));
        System.out.println("  Exported.");
    }

    private static void listTasks() {
        List<Task> all = repo.getAllTasks();
        if (all.isEmpty()) { System.out.println("  (no tasks)"); return; }
        for (Task t : all) System.out.println("    " + t);
    }

    private static void listProjects() {
        List<Project> all = repo.getAllProjects();
        if (all.isEmpty()) { System.out.println("  (no projects)"); return; }
        for (Project p : all) System.out.println("    " + p.getProjectId() + " - " + p.getName());
    }

    private static void listCollaborators() {
        List<Collaborator> all = repo.getAllCollaborators();
        if (all.isEmpty()) { System.out.println("  (no collaborators)"); return; }
        for (Collaborator c : all) {
            int openCount = repo.countOpenTasksForCollaborator(c);
            System.out.println("    " + c.getCollaboratorId() + " - " + c.getName()
                    + " (" + c.getCategoryType() + ", open=" + openCount + "/" + c.getTaskLimit() + ")");
        }
    }

    private static void showOverloaded() {
        List<Collaborator> over = manager.getOverloadedCollaborators();
        if (over.isEmpty()) { System.out.println("  No overloaded collaborators."); return; }
        for (Collaborator c : over) System.out.println("    " + c.getName());
    }

    private static void showActivityLog() {
        List<ActivityEntry> entries = log.getEntries();
        if (entries.isEmpty()) { System.out.println("  (empty)"); return; }
        for (ActivityEntry e : entries) System.out.println("    " + e);
    }

    private static void saveData() {
        JsonPersistence.save(repo, log);
        System.out.println("  Saved.");
    }
}
