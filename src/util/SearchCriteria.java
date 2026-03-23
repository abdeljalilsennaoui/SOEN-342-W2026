package util;

import java.time.LocalDate;

public class SearchCriteria {
    private String keyword;
    private CompletionStatus status;
    private PriorityLevel priority;
    private String projectName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private java.time.DayOfWeek dayOfWeek;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public CompletionStatus getStatus() { return status; }
    public void setStatus(CompletionStatus status) { this.status = status; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public java.time.DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(java.time.DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}
