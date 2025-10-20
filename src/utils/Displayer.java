package utils;

public class Displayer {
    public final int SEPERATOR_LENGTH = 100;
    private static Displayer self = null;

    private Displayer() {}
    public static Displayer getDisplayer() {
         if (self == null) {
            self = new Displayer();
        } 
        return self;
    }

    public void singleSeperate() { System.out.println("-".repeat(SEPERATOR_LENGTH)); }
    public void doubleSeperate() { System.out.println("=".repeat(SEPERATOR_LENGTH)); }
    public void dashSeperate() { System.out.println(" --- ".repeat(20)); }

    public void displayMessage(String[] messages) {
        doubleSeperate();
        System.out.println();
        for (String message : messages) { System.out.println(message); }
        System.out.println();
        singleSeperate();
    }
    public void displayOptions(String[] options) {
        if (options == null || options.length == 0) return;

        // If first option is an "Exit" or "Quay lai" (or explicitly starts with "0.")
        // we print it as 0 and then print remaining options numbered from 1.
        String first = options[0] != null ? options[0].trim().toLowerCase() : "";
        boolean useZero = first.equals("exit") || first.equals("quay lai") || first.startsWith("0.") || first.equals("0");
        if (useZero) {
            System.out.println("0. " + options[0]);
            for (int i = 1; i < options.length; i++) {
                System.out.println(i + ". " + options[i]);
            }
        } else {
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }
        }
    }
    public void clearScreen() {
        System.out.print("\033\143");
        System.out.flush();
    }
}
