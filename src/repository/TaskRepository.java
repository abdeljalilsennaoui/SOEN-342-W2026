package repository;

import model.*;
import util.CompletionStatus;
import util.SearchCriteria;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskRepository {
    private List<Task> tasks;
    private List<Project> projects;
    private List<Collaborator> collaborators;
    private List<Tag> tags;

    public TaskRepository() {
        this.tasks = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.collaborators = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    // Task operations
    public void saveTask(Task task) {
        tasks.add(task);
    }

    public Task findTaskById(String taskId) {
        for (Task t : tasks) {
            if (t.getTaskId().equals(taskId)) return t;
        }
        return null;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByProject(String projectId) {
        return tasks.stream()
                .filter(t -> t.getProject() != null
                        && t.getProject().getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    // Project operations
    public void saveProject(Project project) {
        projects.add(project);
    }

    public Project findProjectById(String projectId) {
        for (Project p : projects) {
            if (p.getProjectId().equals(projectId)) return p;
        }
        return null;
    }

    public Project findProjectByName(String name) {
        for (Project p : projects) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projects);
    }

    // Collaborator operations
    public void saveCollaborator(Collaborator collaborator) {
        collaborators.add(collaborator);
    }

    public Collaborator findCollaboratorById(String collaboratorId) {
        for (Collaborator c : collaborators) {
            if (c.getCollaboratorId().equals(collaboratorId)) return c;
        }
        return null;
    }

    public Collaborator findCollaboratorByName(String name) {
        for (Collaborator c : collaborators) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    public List<Collaborator> getAllCollaborators() {
        return new ArrayList<>(collaborators);
    }

    // Tag operations
    public Tag findOrCreateTag(String keyword) {
        for (Tag t : tags) {
            if (t.getTagKeyword().equalsIgnoreCase(keyword)) return t;
        }
        Tag newTag = new Tag(keyword);
        tags.add(newTag);
        return newTag;
    }

    public Task findTaskByTitleAndDueDate(String title, LocalDate dueDate) {
        for (Task t : tasks) {
            if (t.getTitle().equalsIgnoreCase(title)
                    && ((t.getDueDate() == null && dueDate == null)
                        || (t.getDueDate() != null && t.getDueDate().equals(dueDate)))) {
                return t;
            }
        }
        return null;
    }

    // Count open tasks assigned to a collaborator across all tasks
    public int countOpenTasksForCollaborator(Collaborator collaborator) {
        int count = 0;
        for (Task t : tasks) {
            if (t.getCompletionStatus() == CompletionStatus.OPEN
                    && t.getCollaborators().contains(collaborator)) {
                count++;
            }
        }
        return count;
    }

    // Find the task that contains a given subtask
    public Task findTaskBySubtaskId(String subtaskId) {
        for (Task t : tasks) {
            if (t.findSubtaskById(subtaskId) != null) return t;
        }
        return null;
    }

    // Search with criteria
    public List<Task> search(SearchCriteria criteria) {
        List<Task> results = new ArrayList<>(tasks);

        if (criteria == null) {
            // default: all open tasks sorted by due date ascending
            return results.stream()
                    .filter(t -> t.getCompletionStatus() == CompletionStatus.OPEN)
                    .sorted(Comparator.comparing(Task::getDueDate,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }

        if (criteria.getKeyword() != null) {
            String kw = criteria.getKeyword().toLowerCase();
            results = results.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(kw))
                            || (t.getDescription() != null && t.getDescription().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        if (criteria.getStatus() != null) {
            results = results.stream()
                    .filter(t -> t.getCompletionStatus() == criteria.getStatus())
                    .collect(Collectors.toList());
        }

        if (criteria.getPriority() != null) {
            results = results.stream()
                    .filter(t -> t.getPriorityLevel() == criteria.getPriority())
                    .collect(Collectors.toList());
        }

        if (criteria.getProjectName() != null) {
            String pn = criteria.getProjectName().toLowerCase();
            results = results.stream()
                    .filter(t -> t.getProject() != null
                            && t.getProject().getName().toLowerCase().contains(pn))
                    .collect(Collectors.toList());
        }

        if (criteria.getFromDate() != null) {
            results = results.stream()
                    .filter(t -> t.getDueDate() != null && !t.getDueDate().isBefore(criteria.getFromDate()))
                    .collect(Collectors.toList());
        }

        if (criteria.getToDate() != null) {
            results = results.stream()
                    .filter(t -> t.getDueDate() != null && !t.getDueDate().isAfter(criteria.getToDate()))
                    .collect(Collectors.toList());
        }

        if (criteria.getDayOfWeek() != null) {
            results = results.stream()
                    .filter(t -> t.getDueDate() != null
                            && t.getDueDate().getDayOfWeek() == criteria.getDayOfWeek())
                    .collect(Collectors.toList());
        }

        results.sort(Comparator.comparing(Task::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return results;
    }
}
