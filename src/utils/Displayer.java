package utils;

public class Displayer {
    public final int SEPERATOR_LENGTH = 113;
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
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }
    public void clearScreen() {
        System.out.print("\033\143");
        System.out.flush();
    }

    // Dung de hien thi danh sach nhan vien theo grid
    public void printFormatLine(int[] colWidths) {
        for (int width : colWidths) {
            System.out.print("+");
            System.out.print("-".repeat(width + 2));
        }
        System.out.println("+");
    }
}