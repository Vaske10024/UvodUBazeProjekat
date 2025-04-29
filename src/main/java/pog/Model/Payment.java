package pog.Model;

import java.time.LocalDate;

public class Payment {
    private final int id;
    private final String clientName;
    private final double amount;
    private final LocalDate date;
    private final String status;

    public Payment(int id, String clientName, double amount, LocalDate date, String status) {
        this.id = id;
        this.clientName = clientName;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
