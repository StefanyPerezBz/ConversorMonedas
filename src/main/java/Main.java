import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.out.println("\n╔══════════════════ ERROR ══════════════════╗");
            System.out.printf("║ %-40s ║\n", "Error inesperado en la aplicación");
            System.out.printf("║ %-40s ║\n", e.getMessage());
            System.out.println("╚════════════════════════════════════════════╝");
            e.printStackTrace();
        }
    }
}