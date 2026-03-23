package controller;

import model.*;
import repository.TaskRepository;
import util.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PersonalTaskManager {
    private TaskRepository taskRepository;
    private ActivityLog activityLog;

    public PersonalTaskManager(TaskRepository taskRepository, ActivityLog activityLog) {
        this.taskRepository = taskRepository;
        this.activityLog = activityLog;
    }

    // ---- Interaction Diagram 1: Create Task ----
    public Task createTask(String title, String description, PriorityLevel priorityLevel,
                           LocalDate dueDate, RecurrencePattern recurrence) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required.");
        }

        if (taskRepository.findTaskByTitleAndDueDate(title, dueDate) != null) {
            throw new IllegalArgumentException("A task with this title and due date already exists.");
        }

        Task task = new Task(title, description, priorityLevel, dueDate, recurrence);

        if (recurrence != null) {
            task.generateOccurrencesIfRecurring();
        }

        taskRepository.saveTask(task);
        activityLog.addEntry("Task created: " + title);
        return task;
    }

    // ---- Interaction Diagram 2: Update Task ----
    public void updateTask(String taskId, String title, String description,
                           PriorityLevel priorityLevel, LocalDate dueDate,
                           CompletionStatus completionStatus, RecurrencePattern recurrence) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        task.updateDetails(title, description, priorityLevel, dueDate, completionStatus, recurrence);
        activityLog.addEntry("Task updated: " + task.getTitle());
    }

    // ---- Interaction Diagram 3: Complete Task ----
    public void completeTask(String taskId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        task.complete();
        activityLog.addEntry("Task completed: " + task.getTitle());
    }

    // ---- Interaction Diagram 4: Cancel Task ----
    public void cancelTask(String taskId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        task.cancel();
        activityLog.addEntry("Task cancelled: " + task.getTitle());
    }

    // ---- Interaction Diagram 5: Create Project ----
    public Project createProject(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required.");
        }

        if (taskRepository.findProjectByName(name) != null) {
            throw new IllegalArgumentException("Project name already exists: " + name);
        }

        Project project = new Project(name, description);
        taskRepository.saveProject(project);
        activityLog.addEntry("Project created: " + name);
        return project;
    }

    // ---- Interaction Diagram 6: Assign Task to Project ----
    public void assignTaskToProject(String taskId, String projectId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        Project project = taskRepository.findProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + projectId);
        }

        task.assignToProject(project);
        activityLog.addEntry("Task '" + task.getTitle() + "' assigned to project '" + project.getName() + "'");
    }

    // ---- Interaction Diagram 7: Remove Task from Project ----
    public void removeTaskFromProject(String taskId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        task.removeFromProject();
        activityLog.addEntry("Task '" + task.getTitle() + "' removed from project");
    }

    // ---- Interaction Diagram 8: Move Task to Project ----
    public void moveTaskToProject(String taskId, String newProjectId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        Project newProject = taskRepository.findProjectById(newProjectId);
        if (newProject == null) {
            throw new IllegalArgumentException("Project not found: " + newProjectId);
        }

        task.removeFromProject();
        task.assignToProject(newProject);
        activityLog.addEntry("Task '" + task.getTitle() + "' moved to project '" + newProject.getName() + "'");
    }

    // ---- Interaction Diagram 9: Create Subtask ----
    public Subtask createSubtask(String parentTaskId, String title) {
        Task task = taskRepository.findTaskById(parentTaskId);
        if (task == null) {
            throw new IllegalArgumentException("Parent task not found: " + parentTaskId);
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Subtask title is required.");
        }

        Subtask subtask = new Subtask(title);
        task.addSubtask(subtask);
        activityLog.addEntry("Subtask '" + title + "' created under task '" + task.getTitle() + "'");
        return subtask;
    }

    // ---- Interaction Diagram 10: Set Subtask Completion ----
    public void setSubtaskCompletion(String subtaskId, CompletionStatus completionStatus) {
        Task parentTask = taskRepository.findTaskBySubtaskId(subtaskId);
        if (parentTask == null) {
            throw new IllegalArgumentException("Subtask not found: " + subtaskId);
        }

        Subtask subtask = parentTask.findSubtaskById(subtaskId);
        subtask.setCompletion(completionStatus);
        activityLog.addEntry("Subtask '" + subtask.getTitle() + "' status set to " + completionStatus);
    }

    // ---- Interaction Diagram 11: Add Tag to Task ----
    public void addTagToTask(String taskId, String tagKeyword) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        Tag tag = taskRepository.findOrCreateTag(tagKeyword);
        task.addTag(tag);
        activityLog.addEntry("Tag '" + tagKeyword + "' added to task '" + task.getTitle() + "'");
    }

    // ---- Interaction Diagram 12: Remove Tag from Task ----
    public void removeTagFromTask(String taskId, String tagKeyword) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        task.removeTag(tagKeyword);
        activityLog.addEntry("Tag '" + tagKeyword + "' removed from task '" + task.getTitle() + "'");
    }

    // ---- Interaction Diagram 13: Assign Task to Collaborator ----
    public void assignTaskToCollaborator(String taskId, String collaboratorId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        Collaborator collaborator = taskRepository.findCollaboratorById(collaboratorId);
        if (collaborator == null) {
            throw new IllegalArgumentException("Collaborator not found: " + collaboratorId);
        }

        int openCount = taskRepository.countOpenTasksForCollaborator(collaborator);
        if (!collaborator.canAcceptTask(openCount)) {
            throw new IllegalStateException("Collaborator '" + collaborator.getName()
                    + "' has reached the open task limit (" + collaborator.getTaskLimit()
                    + "). Cannot assign more tasks.");
        }

        task.assignCollaborator(collaborator);
        activityLog.addEntry("Collaborator '" + collaborator.getName()
                + "' assigned to task '" + task.getTitle() + "'");
    }

    // ---- Interaction Diagram 14: Create Collaborator ----
    public Collaborator createCollaborator(String name, CollaboratorCategoryType categoryType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Collaborator name is required.");
        }

        Collaborator collaborator = new Collaborator(name, categoryType);
        taskRepository.saveCollaborator(collaborator);
        activityLog.addEntry("Collaborator created: " + name + " (" + categoryType + ")");
        return collaborator;
    }

    // ---- Interaction Diagram 15: Search Tasks ----
    public List<Task> searchTasks(SearchCriteria criteria) {
        return taskRepository.search(criteria);
    }

    // ---- Interaction Diagram 16: Import from CSV ----
    public void importFromCSV(String filePath) {
        List<String[]> rows;
        try {
            rows = CSVHelper.parseCSV(filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read CSV file: " + e.getMessage());
        }

        for (String[] fields : rows) {
            try {
                String taskName = fields[0];
                String description = fields[1];
                String subtaskTitle = fields[2];
                CompletionStatus status = CSVHelper.parseStatus(fields[3]);
                PriorityLevel priority = CSVHelper.parsePriority(fields[4]);
                LocalDate dueDate = CSVHelper.parseDate(fields[5]);
                String projectName = fields[6];
                String projectDesc = fields[7];
                String collabName = fields[8];
                CollaboratorCategoryType collabCat = CSVHelper.parseCategory(fields[9]);

                if (taskName == null || taskName.trim().isEmpty()) continue;

                // create task
                Task task = new Task(taskName, description, priority, dueDate, null);
                task.updateDetails(null, null, null, null, status, null);
                taskRepository.saveTask(task);

                // find or create project
                if (projectName != null && !projectName.trim().isEmpty()) {
                    Project project = taskRepository.findProjectByName(projectName);
                    if (project == null) {
                        project = new Project(projectName, projectDesc);
                        taskRepository.saveProject(project);
                        activityLog.addEntry("Project auto-created during import: " + projectName);
                    }
                    task.assignToProject(project);
                }

                // create subtask if specified
                if (subtaskTitle != null && !subtaskTitle.trim().isEmpty()) {
                    Subtask subtask = new Subtask(subtaskTitle);
                    task.addSubtask(subtask);
                }

                // find or create collaborator
                if (collabName != null && !collabName.trim().isEmpty() && collabCat != null) {
                    Collaborator collaborator = taskRepository.findCollaboratorByName(collabName);
                    if (collaborator == null) {
                        collaborator = new Collaborator(collabName, collabCat);
                        taskRepository.saveCollaborator(collaborator);
                        activityLog.addEntry("Collaborator auto-created during import: " + collabName);
                    }
                    int openCount = taskRepository.countOpenTasksForCollaborator(collaborator);
                    if (collaborator.canAcceptTask(openCount)) {
                        task.assignCollaborator(collaborator);
                    }
                }

                activityLog.addEntry("Task imported: " + taskName);

            } catch (Exception e) {
                System.out.println("  [WARN] Skipping malformed row: " + e.getMessage());
            }
        }
    }

    // ---- Interaction Diagram 17: Export Search Results to CSV ----
    public void exportSearchResultToCSV(SearchCriteria criteria, String filePath) {
        List<Task> results = taskRepository.search(criteria);
        try {
            CSVHelper.exportToCSV(results, filePath);
            activityLog.addEntry("Exported " + results.size() + " tasks to " + filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot write CSV file: " + e.getMessage());
        }
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }
}
