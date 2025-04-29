package pog.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Session {
    private final int id;
    private final LocalDate date;
    private final LocalTime time;
    private final String clientName;
    private final int duration;

    public Session(int id, LocalDate date, LocalTime time, String clientName, int duration) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.clientName = clientName;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getClientName() {
        return clientName;
    }

    public int getDuration() {
        return duration;
    }
}
