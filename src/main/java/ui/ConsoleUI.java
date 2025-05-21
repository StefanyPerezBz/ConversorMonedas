package ui;

import api.CurrencyApiClient;
import api.CurrencyConverter;
import model.ConversionRecord;
import model.ExchangeRateResponse;
import service.ConversionHistory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final CurrencyApiClient apiClient;
    private final CurrencyConverter converter;
    private final DecimalFormat decimalFormat;
    private final NumberFormat numberFormat;
    private final ConversionHistory history;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.apiClient = new CurrencyApiClient();
        this.history = new ConversionHistory();
        this.converter = new CurrencyConverter(history);
        this.decimalFormat = new DecimalFormat("#,##0.00");
        this.numberFormat = NumberFormat.getNumberInstance(Locale.US);
    }

    public void start() {
        printWelcomeMessage();

        while (true) {
            printMenu();
            try {
                int option = getIntInput("Elija una opción: ");

                if (option == 0) {
                    printExitMessage();
                    break;
                }

                processOption(option);
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
            }
        }
        scanner.close();
    }

    private void printWelcomeMessage() {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║         CONVERSOR DE MONEDAS 1.0          ║");
        System.out.println("║                                            ║");
        System.out.println("║  Conversiones en tiempo real usando datos  ║");
        System.out.println("║          de ExchangeRate-API.com          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
    }


    private void printMenu() {
        System.out.println("\n╔════════════════ MENÚ PRINCIPAL ═══════════════╗");
        System.out.println("╟──────────────────────────────────────────────╢");
        System.out.println("║ 1. Convertir de ARS (Peso Argentino) a USD   ║");
        System.out.println("║ 2. Convertir de USD a ARS                    ║");
        System.out.println("║ 3. Convertir de BRL (Real Brasileño) a USD   ║");
        System.out.println("║ 4. Convertir de USD a BRL                    ║");
        System.out.println("║ 5. Convertir de COP (Peso Colombiano) a USD   ║");
        System.out.println("║ 6. Convertir de USD a COP                    ║");
        System.out.println("║ 7. Convertir entre cualquier moneda          ║");
        System.out.println("║ 8. Mostrar todas las monedas disponibles     ║");
        System.out.println("║ 9. Ver historial de conversiones             ║");
        System.out.println("║ 0. Salir                                     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private void processOption(int option) throws Exception {
        switch (option) {
            case 1: convertCurrency("ARS", "USD"); break;
            case 2: convertCurrency("USD", "ARS"); break;
            case 3: convertCurrency("BRL", "USD"); break;
            case 4: convertCurrency("USD", "BRL"); break;
            case 5: convertCurrency("COP", "USD"); break;
            case 6: convertCurrency("USD", "COP"); break;
            case 7: convertAnyCurrency(); break;
            case 8: showAvailableCurrencies(); break;
            case 9: showConversionHistory(); break;
            case 0: return;
            default: throw new IllegalArgumentException("Opción no válida");
        }
    }

    private void convertCurrency(String from, String to) throws Exception {
        double amount = getDoubleInput("Ingrese la cantidad a convertir (puede usar decimales): ");
        ExchangeRateResponse rates = apiClient.getExchangeRates(from);
        double result = converter.convertAndRecord(rates, from, to, amount);
        printConversionResult(amount, from, result, to, rates);
    }

    private void convertAnyCurrency() throws Exception {
        showAvailableCurrencies();
        String from = getCurrencyInput("Ingrese código de moneda origen (ej: USD): ");
        String to = getCurrencyInput("Ingrese código de moneda destino (ej: EUR): ");
        double amount = getDoubleInput("Ingrese la cantidad a convertir: ");

        ExchangeRateResponse rates = apiClient.getExchangeRates(from);
        double result = converter.convertAndRecord(rates, from, to, amount);
        printConversionResult(amount, from, result, to, rates);
    }

    private String getCurrencyInput(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.next().trim().toUpperCase();
            if (input.length() == 3) {
                return input;
            }
            System.out.println("Error: Debe ingresar un código de moneda válido de 3 letras (ej: USD)");
        }
    }

    private void showAvailableCurrencies() throws Exception {
        System.out.println("\nObteniendo monedas disponibles...");
        ExchangeRateResponse rates = apiClient.getExchangeRates("USD");
        converter.printAvailableCurrencies(rates);
    }

    private void showConversionHistory() {
        List<ConversionRecord> records = history.getHistory();

        System.out.println("\n╔════════════════ HISTORIAL DE CONVERSIONES ═══════════════╗");
        System.out.println("║                                                          ║");

        if (records.isEmpty()) {
            System.out.println("║              No hay conversiones registradas             ║");
        } else {
            for (ConversionRecord record : records) {
                System.out.printf("║ %-56s ║\n", record.toString());
            }
        }

        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    private void printConversionResult(double amount, String from, double result, String to, ExchangeRateResponse rates) {
        System.out.println("\n╔════════════════ RESULTADO ═══════════════╗");
        System.out.printf("║ %15s: %20s ║\n", "De", formatCurrency(amount, from));
        System.out.printf("║ %15s: %20s ║\n", "A", formatCurrency(result, to));
        System.out.println("╟──────────────────────────────────────────────╢");
        System.out.printf("║ %15s: 1 %-3s = %-10s %-3s ║\n",
                "Tasa de cambio",
                from,
                decimalFormat.format(rates.getConversion_rates().get(to)),
                to);
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private String formatCurrency(double amount, String currency) {
        return String.format("%s %s", decimalFormat.format(amount), currency);
    }

    private int getIntInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número entero. Intente nuevamente.");
                scanner.nextLine();
            }
        }
    }

    private double getDoubleInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.next();
                input = input.replace(',', '.');
                return numberFormat.parse(input).doubleValue();
            } catch (Exception e) {
                System.out.println("Error: Debe ingresar un número válido (puede usar decimales). Intente nuevamente.");
                scanner.nextLine();
            }
        }
    }

    private void printExitMessage() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║    Gracias por usar el Conversor 1.0      ║");
        System.out.println("║                                            ║");
        System.out.println("║         ¡Vuelva pronto!                   ║");
        System.out.println("╚════════════════════════════════════════════╝");
    }

    private void printErrorMessage(String message) {
        System.out.println("\n╔══════════════════ ERROR ══════════════════╗");
        System.out.printf("║ %-40s ║\n", message);
        System.out.println("╚════════════════════════════════════════════╝");
    }
}