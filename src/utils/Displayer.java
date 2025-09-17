package utils;

import java.util.Scanner;

public class Displayer {
    public final int SEPERATOR_LENGTH = 100;
    private Scanner sc = null;
    private static Displayer self = null;

    private Displayer() {}
    public static Displayer getDisplayer() {
         if (self == null) {
            self = new Displayer();
        } 
        return self;
    }

    public Scanner getScanner() {
        if (sc == null) { newScanner(); }
        return sc;
    }
    public void newScanner() { sc = new Scanner(System.in); }
    public void closeScanner() { sc.close(); }

    public void singleSeperate() { System.out.println("-".repeat(SEPERATOR_LENGTH)); }
    public void doubleSeperate() { System.out.println("=".repeat(SEPERATOR_LENGTH)); }
    public void dashSeperate() { System.out.println(" --- ".repeat(20)); }

    public void showMessage(String[] messages) {
        doubleSeperate();
        System.out.println();
        for (String message : messages) { System.out.println(message); }
        System.out.println();
        singleSeperate();
    }

    public void enter2Continue() {
        Scanner _sc = self.getScanner();
        System.out.println("Press enter to continue");
        _sc.nextLine();
    }
 
    public void displayOptions(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    public void clearScreen() {
        System.out.print("\033\143");
        System.out.flush();
    }
}
