package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.*;
import repository.TaskRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class JsonPersistence {

    private static final String DATA_DIR        = "data";
    private static final String PROJECTS_FILE   = "data/projects.json";
    private static final String COLLABS_FILE    = "data/collaborators.json";
    private static final String TASKS_FILE      = "data/tasks.json";

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (d, t, ctx) -> new JsonPrimitive(d.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (j, t, ctx) -> LocalDate.parse(j.getAsString()))
                .setPrettyPrinting()
                .create();
    }

    public static void save(TaskRepository repo) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot create data directory: " + e.getMessage());
            return;
        }
        Gson gson = createGson();

        List<ProjectDto> projectDtos = new ArrayList<>();
        for (Project p : repo.getAllProjects()) projectDtos.add(new ProjectDto(p));
        writeJson(gson, PROJECTS_FILE, projectDtos);

        List<CollaboratorDto> collabDtos = new ArrayList<>();
        for (Collaborator c : repo.getAllCollaborators()) collabDtos.add(new CollaboratorDto(c));
        writeJson(gson, COLLABS_FILE, collabDtos);

        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task t : repo.getAllTasks()) taskDtos.add(new TaskDto(t));
        writeJson(gson, TASKS_FILE, taskDtos);
    }

    public static void load(TaskRepository repo) {
        if (!Files.exists(Paths.get(TASKS_FILE))) return;
        Gson gson = createGson();

        // Load projects
        Map<String, Project> projectMap = new HashMap<>();
        List<ProjectDto> projectDtos = readJson(gson, PROJECTS_FILE,
                new TypeToken<List<ProjectDto>>(){}.getType());
        if (projectDtos != null) {
            int maxN = 0;
            for (ProjectDto dto : projectDtos) {
                Project p = dto.toProject();
                projectMap.put(p.getProjectId(), p);
                repo.saveProject(p);
                maxN = Math.max(maxN, parseN(p.getProjectId()));
            }
            Project.resetCounter(maxN + 1);
        }

        // Load collaborators
        Map<String, Collaborator> collabMap = new HashMap<>();
        List<CollaboratorDto> collabDtos = readJson(gson, COLLABS_FILE,
                new TypeToken<List<CollaboratorDto>>(){}.getType());
        if (collabDtos != null) {
            int maxN = 0;
            for (CollaboratorDto dto : collabDtos) {
                Collaborator c = dto.toCollaborator();
                collabMap.put(c.getCollaboratorId(), c);
                repo.saveCollaborator(c);
                maxN = Math.max(maxN, parseN(c.getCollaboratorId()));
            }
            Collaborator.resetCounter(maxN + 1);
        }

        // Load tasks
        List<TaskDto> taskDtos = readJson(gson, TASKS_FILE,
                new TypeToken<List<TaskDto>>(){}.getType());
        if (taskDtos != null) {
            int maxTask = 0, maxSub = 0, maxOcc = 0;
            for (TaskDto dto : taskDtos) {
                Project project = dto.projectId != null ? projectMap.get(dto.projectId) : null;
                List<Collaborator> collabs = new ArrayList<>();
                if (dto.collaboratorIds != null) {
                    for (String cid : dto.collaboratorIds) {
                        Collaborator c = collabMap.get(cid);
                        if (c != null) collabs.add(c);
                    }
                }
                Task task = dto.toTask(project, collabs);
                repo.saveTask(task);
                maxTask = Math.max(maxTask, parseN(task.getTaskId()));
                for (Subtask s : task.getSubtasks())    maxSub = Math.max(maxSub, parseN(s.getSubtaskId()));
                for (TaskOccurrence o : task.getOccurrences()) maxOcc = Math.max(maxOcc, parseN(o.getOccurrenceId()));
            }
            Task.resetCounter(maxTask + 1);
            Subtask.resetCounter(maxSub + 1);
            TaskOccurrence.resetCounter(maxOcc + 1);
        }
    }

    private static int parseN(String id) {
        try { return Integer.parseInt(id.substring(id.lastIndexOf('-') + 1)); }
        catch (Exception e) { return 0; }
    }

    private static void writeJson(Gson gson, String path, Object data) {
        try (Writer w = new FileWriter(path)) {
            gson.toJson(data, w);
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot save " + path + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readJson(Gson gson, String path, Type type) {
        if (!Files.exists(Paths.get(path))) return null;
        try (Reader r = new FileReader(path)) {
            return gson.fromJson(r, type);
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot load " + path + ": " + e.getMessage());
            return null;
        }
    }

    // ---- DTOs ----

    static class ProjectDto {
        String projectId, name, description;
        ProjectDto(Project p) { projectId = p.getProjectId(); name = p.getName(); description = p.getDescription(); }
        Project toProject() { Project p = new Project(name, description); p.setProjectId(projectId); return p; }
    }

    static class CollaboratorDto {
        String collaboratorId, name, categoryType;
        int taskLimit;
        CollaboratorDto(Collaborator c) {
            collaboratorId = c.getCollaboratorId(); name = c.getName();
            categoryType = c.getCategoryType().name(); taskLimit = c.getTaskLimit();
        }
        Collaborator toCollaborator() {
            Collaborator c = new Collaborator(name, CollaboratorCategoryType.valueOf(categoryType));
            c.setCollaboratorId(collaboratorId);
            c.setTaskLimit(taskLimit);
            return c;
        }
    }

    static class SubtaskDto {
        String subtaskId, title, completionStatus;
        SubtaskDto(Subtask s) { subtaskId = s.getSubtaskId(); title = s.getTitle(); completionStatus = s.getCompletionStatus().name(); }
        Subtask toSubtask() {
            Subtask s = new Subtask(title);
            s.setSubtaskId(subtaskId);
            s.setCompletion(CompletionStatus.valueOf(completionStatus));
            return s;
        }
    }

    static class RecurrencePatternDto {
        String recurrenceType, startDate, endDate;
        int interval;
        RecurrencePatternDto(RecurrencePattern r) {
            recurrenceType = r.getRecurrenceType().name(); interval = r.getInterval();
            startDate = r.getStartDate() != null ? r.getStartDate().toString() : null;
            endDate   = r.getEndDate()   != null ? r.getEndDate().toString()   : null;
        }
        RecurrencePattern toRecurrencePattern() {
            return new RecurrencePattern(RecurrenceType.valueOf(recurrenceType), interval,
                    startDate != null ? LocalDate.parse(startDate) : null,
                    endDate   != null ? LocalDate.parse(endDate)   : null);
        }
    }

    static class TaskOccurrenceDto {
        String occurrenceId, dueDate, completionStatus;
        TaskOccurrenceDto(TaskOccurrence o) {
            occurrenceId = o.getOccurrenceId();
            dueDate = o.getDueDate() != null ? o.getDueDate().toString() : null;
            completionStatus = o.getCompletionStatus().name();
        }
        TaskOccurrence toTaskOccurrence() {
            TaskOccurrence o = new TaskOccurrence(dueDate != null ? LocalDate.parse(dueDate) : null);
            o.setOccurrenceId(occurrenceId);
            if ("COMPLETED".equals(completionStatus)) o.complete();
            return o;
        }
    }

    static class TaskDto {
        String taskId, title, description, creationDate, dueDate, completionStatus, priorityLevel, projectId;
        List<String> collaboratorIds = new ArrayList<>();
        List<SubtaskDto> subtasks = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        RecurrencePatternDto recurrencePattern;
        List<TaskOccurrenceDto> occurrences = new ArrayList<>();

        TaskDto(Task t) {
            taskId = t.getTaskId(); title = t.getTitle(); description = t.getDescription();
            creationDate = t.getCreationDate() != null ? t.getCreationDate().toString() : null;
            dueDate = t.getDueDate() != null ? t.getDueDate().toString() : null;
            completionStatus = t.getCompletionStatus().name();
            priorityLevel = t.getPriorityLevel().name();
            projectId = t.getProject() != null ? t.getProject().getProjectId() : null;
            for (Collaborator c : t.getCollaborators()) collaboratorIds.add(c.getCollaboratorId());
            for (Subtask s : t.getSubtasks()) subtasks.add(new SubtaskDto(s));
            for (Tag tag : t.getTags()) tags.add(tag.getTagKeyword());
            if (t.getRecurrencePattern() != null) recurrencePattern = new RecurrencePatternDto(t.getRecurrencePattern());
            for (TaskOccurrence o : t.getOccurrences()) occurrences.add(new TaskOccurrenceDto(o));
        }

        Task toTask(Project project, List<Collaborator> collabs) {
            RecurrencePattern rp = recurrencePattern != null ? recurrencePattern.toRecurrencePattern() : null;
            Task t = new Task(title, description,
                    PriorityLevel.valueOf(priorityLevel),
                    dueDate != null ? LocalDate.parse(dueDate) : null,
                    rp);
            t.setTaskId(taskId);
            if (creationDate != null) t.setCreationDate(LocalDate.parse(creationDate));
            t.setCompletionStatus(CompletionStatus.valueOf(completionStatus));
            if (project != null) t.assignToProject(project);
            for (Collaborator c : collabs) t.assignCollaborator(c);
            for (SubtaskDto sd : subtasks) t.getSubtasks().add(sd.toSubtask());
            for (String kw : tags) t.addTag(new Tag(kw));
            for (TaskOccurrenceDto od : occurrences) t.getOccurrences().add(od.toTaskOccurrence());
            return t;
        }
    }
}
