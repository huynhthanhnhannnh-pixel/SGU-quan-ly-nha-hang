package main;

import java.util.Scanner;

import controllers.*;
import utils.*;

public class program {
    public static void main(String[] args) {
        WorkerManager workerMgr = WorkerManager.getManager();
        Displayer displayer = Displayer.getDisplayer();
        String[] options = { 
            "Exit", 
            "Hire new workers", 
            "Show all workers in our restaurant",
            "Show schedule"
        };
        boolean sessionActive = true;
        int curOption = 0;

        // tasks
        // Hien thi danh sach nhan vien co the thue
        Scanner sc = displayer.getScanner();

        while (sessionActive) {
            displayer.clearScreen();
            displayer.displayOptions(options);
            curOption = sc.nextInt();
            sc.nextLine(); // clear \n
            switch (curOption) {
                case 1:
                    displayer.closeScanner();
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

