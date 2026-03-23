package model;

import util.RecurrenceType;
import java.time.LocalDate;

public class RecurrencePattern {
    private RecurrenceType recurrenceType;
    private int interval;
    private LocalDate startDate;
    private LocalDate endDate;

    public RecurrencePattern(RecurrenceType recurrenceType, int interval,
                             LocalDate startDate, LocalDate endDate) {
        this.recurrenceType = recurrenceType;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public int getInterval() { return interval; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    @Override
    public String toString() {
        return recurrenceType + " every " + interval
                + " from " + startDate + " to " + endDate;
    }
}
