package main;

import controllers.*;
import utils.*;

public class program {
    public static void main(String[] args) {
        WorkerManager workerMgr = WorkerManager.getManager();
        Displayer displayer = Displayer.getDisplayer();
        UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
        EventHandler eventHlr = EventHandler.getEventHandler();
        SupplyManager splManager = SupplyManager.getManager();

        String[] message = {
            "WELCOME TO QUAN LY NHA HANG"
        };
        String[] options = { 
            "Exit", 
            "Thue nhan vien moi", 
            "Hien thi nhan vien cua nha hang",
            "Hien thi lich lam",
            "Kiem tra kho nguyen lieu",
            "Nhap them/xoa nguyen lieu trong kho" 
        };
        boolean sessionActive = true;

        while (sessionActive) {

            displayer.displayMessage(message);
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            
            switch (inputHandler.getCurrentOption()) {
                case 1:
                    inputHandler.closeScanner();
                    return;
                case 2:
                    workerMgr.showWorkerToHire();
                    break;
                case 3:
                    workerMgr.showHiredWorker();
                    break;
                case 4:
                    workerMgr.showSchedule();
                    break;
                case 5:
                    splManager.showStorage();
                    break;
                case 6:
                    splManager.showAddRemoveIngrediens();
                    break;
                default:
                    System.out.println("Invalid input!");
                    inputHandler.enter2Continue();
                    break;
            }
            
            displayer.clearScreen();
        }
    }
}

