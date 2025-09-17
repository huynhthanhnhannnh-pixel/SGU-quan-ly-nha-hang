package main;

import java.util.Scanner;

import controllers.*;
import utils.*;

public class program {
    public static void main(String[] args) {
        WorkerManager workerMgr = WorkerManager.getManager();
        Displayer displayer = Displayer.getDisplayer();
        UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
        String[] options = { 
            "Exit", 
            "Hire new workers", 
            "Show all workers in our restaurant",
            "Show schedule"
        };
        boolean sessionActive = true;

        // tasks
        // Hien thi danh sach nhan vien co the thue
        Scanner sc = inputHandler.getScanner();

        while (sessionActive) {
            displayer.clearScreen();
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            sc.nextLine(); // clear \n
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
                    break;
                default:
                    System.out.println("Invalid input!");
            }
        }
    }
}

