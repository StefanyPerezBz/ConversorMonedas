package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConversionRecord {
    private LocalDateTime timestamp;
    private double amount;
    private String fromCurrency;
    private double result;
    private String toCurrency;
    private double exchangeRate;

    public ConversionRecord(double amount, String fromCurrency, double result,
                            String toCurrency, double exchangeRate) {
        this.timestamp = LocalDateTime.now();
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.result = result;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %.2f %s → %.2f %s (Tasa: %.6f)",
                timestamp.format(formatter),
                amount,
                fromCurrency,
                result,
                toCurrency,
                exchangeRate);
    }

    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
    public String getFromCurrency() { return fromCurrency; }
    public double getResult() { return result; }
    public String getToCurrency() { return toCurrency; }
    public double getExchangeRate() { return exchangeRate; }
}