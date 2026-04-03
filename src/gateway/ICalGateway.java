package gateway;

import model.Task;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ICalGateway {

    public void exportTaskToIcal(Task task, String filePath) throws IOException {
        writeToFile(buildCalendar(List.of(task)), filePath);
    }

    public void exportProjectTasksToIcal(List<Task> tasks, String filePath) throws IOException {
        writeToFile(buildCalendar(tasks), filePath);
    }

    public void exportFilteredTasksToIcal(List<Task> tasks, String filePath) throws IOException {
        writeToFile(buildCalendar(tasks), filePath);
    }

    private Calendar buildCalendar(List<Task> tasks) {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Personal Task Manager//iCal4j//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Task task : tasks) {
            if (task.getDueDate() == null) continue;

            try {
                String dateStr = task.getDueDate().format(DateTimeFormatter.BASIC_ISO_DATE);
                Date icalDate = new Date(dateStr);
                VEvent event = new VEvent(icalDate, task.getTitle());
                event.getProperties().add(new Uid(task.getTaskId() + "@ptm"));

                StringBuilder desc = new StringBuilder();
                if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                    desc.append(task.getDescription());
                }
                if (task.getProject() != null) {
                    desc.append("\\nProject: ").append(task.getProject().getName());
                }
                desc.append("\\nStatus: ").append(task.getCompletionStatus());
                desc.append("\\nPriority: ").append(task.getPriorityLevel());
                if (!task.getSubtasks().isEmpty()) {
                    String summary = task.getSubtasks().stream()
                            .map(s -> s.getTitle() + " [" + s.getCompletionStatus() + "]")
                            .collect(Collectors.joining(", "));
                    desc.append("\\nSubtasks: ").append(summary);
                }
                event.getProperties().add(new Description(desc.toString()));
                event.getProperties().add(new Priority(toPriorityValue(task)));

                calendar.getComponents().add(event);
            } catch (ParseException e) {
                System.err.println("[WARN] Skipping task for iCal export: " + task.getTitle());
            }
        }
        return calendar;
    }

    private int toPriorityValue(Task task) {
        switch (task.getPriorityLevel()) {
            case HIGH:   return 1;
            case MEDIUM: return 5;
            case LOW:    return 9;
            default:     return 5;
        }
    }

    private void writeToFile(Calendar calendar, String filePath) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(filePath)) {
            CalendarOutputter outputter = new CalendarOutputter();
            try {
                outputter.output(calendar, fout);
            } catch (Exception e) {
                throw new IOException("iCal export failed: " + e.getMessage(), e);
            }
        }
    }
}
