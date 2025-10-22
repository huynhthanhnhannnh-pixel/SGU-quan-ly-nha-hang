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
        dishManager dishMgr = dishManager.getManager();

        String[] message = {
            "WELCOME TO QUAN LY NHA HANG"
        };
        String[] options = { 
            "Exit", 
            "Thue nhan vien moi", 
            "Hien thi nhan vien cua nha hang",
            "Hien thi lich lam",
            "Kiem tra kho nguyen lieu", 
            "Kiem tra danh sach menu"
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
                // Xem lại file txt của ingredients và dishes, tại có những món trùng hoặc bị dư, sửa lại cho chuẩn là oke 
                case 5:
                    splManager.showGeneralInfo();
                    break;
                case 6:
                    dishMgr.showGeneralInfo();
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