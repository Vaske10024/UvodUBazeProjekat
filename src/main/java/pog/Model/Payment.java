package pog.Model;

import java.time.LocalDate;

public class Payment {
    private final int id;
    private final String clientName;
    private final double amount;
    private final String currency;
    private final LocalDate date;
    private final String method;
    private final Integer installment;

    public Payment(int id, String clientName, double amount, String currency, LocalDate date, String method, Integer installment) {
        this.id = id;
        this.clientName = clientName;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.method = method;
        this.installment = installment;
    }

    public int getId() { return id; }
    public String getClientName() { return clientName; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDate getDate() { return date; }
    public String getMethod() { return method; }
    public Integer getInstallment() { return installment; }
}