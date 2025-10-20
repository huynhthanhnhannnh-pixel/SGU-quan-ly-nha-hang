package main;

import controllers.*;
import utils.*;

public class program {
    public static void main(String[] args) {
        Displayer displayer = Displayer.getDisplayer();
        UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
        WorkerManager workerMgr = WorkerManager.getManager();
        SupplyManager splManager = SupplyManager.getManager();
        dishManager dishMgr = dishManager.getManager();

        String[] message = {"WELCOME TO QUAN LY NHA HANG"};
        String[] roles = {
            "Exit",
            "You are Supply Manager",
            "You are Worker Manager",
            "Other..."
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(message);
            displayer.displayOptions(roles);
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == 0) { inputHandler.closeScanner(); return; }

            switch (choice) {
                case 1:
                    // Run interactive flow for supply manager
                    splManager.runSupplyManagerConsole();
                    break;
                case 2:
                    // show worker manager general info / menu
                    workerMgr.showGeneralInfo();
                    break;
                default:
                    System.out.println("Role not implemented yet.");
                    inputHandler.enter2Continue();
            }
        }
    }
}

