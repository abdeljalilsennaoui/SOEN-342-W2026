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

    public void setProjectId(String projectId) { this.projectId = projectId; }
    public static void resetCounter(int n) { counter = n; }

    @Override
    public String toString() {
        return "Project{id='" + projectId + "', name='" + name + "'}";
    }
}
