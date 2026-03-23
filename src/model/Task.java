package model;

import util.CompletionStatus;
import util.PriorityLevel;
import util.RecurrenceType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private static int counter = 1;

    private String taskId;
    private String title;
    private String description;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private CompletionStatus completionStatus;
    private PriorityLevel priorityLevel;
    private Project project;
    private List<Collaborator> collaborators;
    private List<Tag> tags;
    private List<Subtask> subtasks;
    private RecurrencePattern recurrencePattern;
    private List<TaskOccurrence> occurrences;

    public Task(String title, String description, PriorityLevel priorityLevel,
                LocalDate dueDate, RecurrencePattern recurrencePattern) {
        this.taskId = "TSK-" + counter++;
        this.title = title;
        this.description = description;
        this.priorityLevel = priorityLevel;
        this.dueDate = dueDate;
        this.creationDate = LocalDate.now();
        this.completionStatus = CompletionStatus.OPEN;
        this.project = null;
        this.collaborators = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.subtasks = new ArrayList<>();
        this.recurrencePattern = recurrencePattern;
        this.occurrences = new ArrayList<>();
    }

    public void complete() {
        this.completionStatus = CompletionStatus.COMPLETED;
    }

    public void cancel() {
        this.completionStatus = CompletionStatus.CANCELLED;
    }

    public void updateDetails(String title, String description, PriorityLevel priorityLevel,
                              LocalDate dueDate, CompletionStatus completionStatus,
                              RecurrencePattern recurrence) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (priorityLevel != null) this.priorityLevel = priorityLevel;
        if (dueDate != null) this.dueDate = dueDate;
        if (completionStatus != null) this.completionStatus = completionStatus;
        if (recurrence != null) {
            this.recurrencePattern = recurrence;
            this.occurrences.clear();
            generateOccurrencesIfRecurring();
        }
    }

    public void assignToProject(Project project) {
        this.project = project;
    }

    public void removeFromProject() {
        this.project = null;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public void addTag(Tag tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public void removeTag(String tagKeyword) {
        this.tags.removeIf(t -> t.getTagKeyword().equalsIgnoreCase(tagKeyword));
    }

    public void assignCollaborator(Collaborator collaborator) {
        if (!this.collaborators.contains(collaborator)) {
            this.collaborators.add(collaborator);
        }
    }

    public void generateOccurrencesIfRecurring() {
        if (recurrencePattern == null) return;

        LocalDate current = recurrencePattern.getStartDate();
        LocalDate end = recurrencePattern.getEndDate();
        if (current == null || end == null) return;

        int interval = recurrencePattern.getInterval();
        RecurrenceType type = recurrencePattern.getRecurrenceType();

        while (!current.isAfter(end)) {
            occurrences.add(new TaskOccurrence(current));
            switch (type) {
                case DAILY:
                    current = current.plusDays(interval);
                    break;
                case WEEKLY:
                    current = current.plusWeeks(interval);
                    break;
                case MONTHLY:
                    current = current.plusMonths(interval);
                    break;
            }
        }
    }

    // find a subtask by id within this task
    public Subtask findSubtaskById(String subtaskId) {
        for (Subtask s : subtasks) {
            if (s.getSubtaskId().equals(subtaskId)) return s;
        }
        return null;
    }

    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getCreationDate() { return creationDate; }
    public LocalDate getDueDate() { return dueDate; }
    public CompletionStatus getCompletionStatus() { return completionStatus; }
    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public Project getProject() { return project; }
    public List<Collaborator> getCollaborators() { return collaborators; }
    public List<Tag> getTags() { return tags; }
    public List<Subtask> getSubtasks() { return subtasks; }
    public RecurrencePattern getRecurrencePattern() { return recurrencePattern; }
    public List<TaskOccurrence> getOccurrences() { return occurrences; }

    @Override
    public String toString() {
        return "Task{id='" + taskId + "', title='" + title + "', status=" + completionStatus
                + ", priority=" + priorityLevel + ", due=" + dueDate
                + (project != null ? ", project=" + project.getName() : "") + "}";
    }
}
