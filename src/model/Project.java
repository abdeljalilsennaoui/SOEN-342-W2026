package model;

public class Project {
    private static int counter = 1;

    private String projectId;
    private String name;
    private String description;

    public Project(String name, String description) {
        this.projectId = "PRJ-" + counter++;
        this.name = name;
        this.description = description;
    }

    public String getProjectId() { return projectId; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "Project{id='" + projectId + "', name='" + name + "'}";
    }
}
