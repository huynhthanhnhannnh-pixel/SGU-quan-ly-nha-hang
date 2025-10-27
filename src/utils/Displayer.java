package utils;

public class Displayer {
    public final int SEPERATOR_LENGTH = 113;
    private static Displayer self = null;
    private int[] optionLength = {100};

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
        printFormatLine(optionLength);
        System.out.printf("| %-100s |\n", "Options |");
        printFormatLine(optionLength);
        for (int i = 0; i < options.length; i++) {
            //System.out.println((i + 1) + ". " + options[i]);
            System.out.printf("| %-1s %-97s |\n", (i+1) + ".", options[i]);
        }
        printFormatLine(optionLength);
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

    // Hien thi string can giua
    public String centerString(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int totalPadding = width - text.length();
        int paddingLeft = totalPadding / 2;
        int paddingRight = totalPadding - paddingLeft; // ensures total padding is correct

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paddingLeft; i++) sb.append(" ");
        sb.append(text);
        for (int i = 0; i < paddingRight; i++) sb.append(" ");

        return sb.toString();
    }

    public String centerSplit(String word, int totalLength) {
        int mid = word.length() / 2;
        String left = word.substring(0, mid);
        String right = word.substring(mid);

        String combined = left + "------" + right;
        int padding = totalLength - combined.length();
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;

        return "-".repeat(leftPadding) + combined + "-".repeat(rightPadding);
    }

    public String wrapWord(String word, int count, String symbol) {
        String padding = String.valueOf(symbol).repeat(count);
        return padding + " " + word + " " + padding;
    }


}