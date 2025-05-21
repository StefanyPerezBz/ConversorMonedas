package api;

import model.ConversionRecord;
import model.ExchangeRateResponse;
import service.ConversionHistory;
import java.util.Map;

public class CurrencyConverter {
    private final ConversionHistory history;

    public CurrencyConverter(ConversionHistory history) {
        this.history = history;
    }

    public double convertAndRecord(ExchangeRateResponse rates, String from,
                                   String to, double amount) {
        Map<String, Double> conversionRates = rates.getConversion_rates();

        if (!conversionRates.containsKey(to)) {
            throw new IllegalArgumentException("Moneda no soportada: " + to);
        }

        double rate = conversionRates.get(to);
        double result = amount * rate;

        // Registrar la conversión en el historial
        history.addRecord(new ConversionRecord(amount, from, result, to, rate));

        return result;
    }

    public void printAvailableCurrencies(ExchangeRateResponse rates) {
        System.out.println("╔════════════════ MONEDAS DISPONIBLES ("
                + rates.getConversion_rates().size() + ") ═══════════════╗");
        System.out.println("║                                                    ║");

        int count = 0;
        StringBuilder line = new StringBuilder("║ ");
        for (String currency : rates.getConversion_rates().keySet()) {
            line.append(String.format("%-6s", currency));
            count++;

            if (count % 6 == 0) {
                line.append(" ║");
                System.out.println(line.toString());
                line = new StringBuilder("║ ");
            }
        }

        if (count % 6 != 0) {
            while (count % 6 != 0) {
                line.append("      ");
                count++;
            }
            line.append(" ║");
            System.out.println(line.toString());
        }

        System.out.println("║                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}