package utils;

import java.util.Scanner;

public class UserInputHandler {
    private static UserInputHandler self = null;
    private Displayer displayer = Displayer.getDisplayer();
    private Scanner sc = null;
    private int curOption = -1;
    private boolean showWarning = false;

    private UserInputHandler() {}
    public static UserInputHandler getUserInputHandler() {
         if (self == null) {
            self = new UserInputHandler();
        } 
        return self;
    }

    public void newScanner() { sc = new Scanner(System.in); }
    public void closeScanner() { sc.close(); }
    public Scanner getScanner() {
        if (sc == null) { newScanner(); }
        return sc;
    }
    public void enter2Continue() {
        Scanner _sc = self.getScanner();
        System.out.println("Press enter to continue");
        _sc.nextLine();
    }

    // get user option, show warning when user enter an invalid option
    public void getUserOption() {
        System.out.println();
        displayer.singleSeperate();
        if (showWarning) {
            System.out.println("The option "+curOption+" is not a valid option, please choose again");
            showWarning = false;
        }
        curOption = sc.nextInt();
        sc.nextLine(); // clear \n
    }
    public int getCurrentOption () { return curOption; }
    public void raiseWarning() { showWarning = true; }
    public void resetOption() {
        curOption = -1;
        showWarning = false;
    }
}
