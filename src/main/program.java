package main;

import controllers.*;
import utils.*;

public class program {
    public static void main(String[] args) {
        WorkerManager workerMgr = WorkerManager.getManager();
        Displayer displayer = Displayer.getDisplayer();
        UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
        EventHandler eventHlr = EventHandler.getEventHandler();

        String[] message = {
            "WELCOME TO QUAN LY NHA HANG"
        };
        String[] options = { 
            "Exit", 
            "Thue nhan vien moi", 
            "Hien thi nhan vien cua nha hang",
            "Hien thi lich lam",
            "Thu nghiem tinh nang"
        };
        boolean sessionActive = true;

        while (sessionActive) {
            displayer.clearScreen();
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
                    // sử dụng eventHandler để test tính năng
                    // nhập EvenHandler.[Hàm cần test]

                    //==================================






                    //==================================
                    inputHandler.enter2Continue();
                    break;
                default:
                    System.out.println("Invalid input!");
            }
        }
    }
}

