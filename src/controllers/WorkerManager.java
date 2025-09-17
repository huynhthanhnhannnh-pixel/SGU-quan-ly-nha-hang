package controllers;

import java.io.*;
import java.util.*;
import base.Worker;
import contracts.ManagerHandler;
import models.*;
import utils.Displayer;
import utils.UserInputHandler;

public class WorkerManager implements ManagerHandler {
    private static WorkerManager self = null;
    private int GO_BACK_OPTION = 0; 
    private final static String[] SCHEDULE_NAMES = {
        "Sang Thu 2", "Chieu Thu 2",
        "Sang Thu 3", "Chieu Thu 3",
        "Sang Thu 4", "Chieu Thu 4",
        "Sang Thu 5", "Chieu Thu 5",
        "Sang Thu 6", "Chieu Thu 6",
        "Sang Thu 7", "Chieu Thu 7",
    };

    private Displayer displayer = Displayer.getDisplayer();
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    private HashMap<Integer, Worker> workerToHire = new HashMap<Integer, Worker>(); // available workers
    private HashMap<Integer, Worker> hiredWorkers = new HashMap<Integer, Worker>(); // hired workers
    private LinkedHashMap<Integer, Shift> schedule = new LinkedHashMap<Integer, Shift>();

    @Override
    public void showGeneralInfo() {
        System.out.println("Class nay dung de quan ly nhan vien ...");
    }
    @Override
    public void createReport() {
        System.out.println("Danh thu cua thang nay la ...");
    }

    private WorkerManager() {
        // Init schedule
        for (int i = 0; i < SCHEDULE_NAMES.length; i++) {
            schedule.put((i + 1), new Shift(SCHEDULE_NAMES[i]));
        }
    }

    // Init a storage of worker objects
    private void initResources() {
        try (BufferedReader br = new BufferedReader(new FileReader("src\\resources\\Workers.txt"))) {
            String basicInfo;
            int counter = 1;
            while ((basicInfo = br.readLine()) != null) {
                String description = br.readLine(); // assumes even number of lines
                String[] parts = basicInfo.split(" ");
                String name = parts[0];
                int age = Integer.parseInt(parts[1]);
                String gender = parts[2];
                String position = parts[3];
                double salaries = Double.parseDouble(parts[4]);
                Worker worker = null;
                switch (position) {
                    case "waiter":
                        worker = new Waiter(counter, name, age, gender, position, salaries, description);
                        workerToHire.put(counter, worker);
                        break;
                    case "chef":
                        worker = new Chef(counter, name, age, gender, position, salaries, description);
                        workerToHire.put(counter, worker);
                        break;
                    default:
                        break;
                }

                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WorkerManager getManager() {
        if (self == null) {
            self = new WorkerManager();
            self.initResources();
            // Init Worker data in the resources folder
        } 
        return self;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //Schedule system

    private void editSelectedShift() {
        String[] message = {
            "To go back enter 0",
            "Enter the id of available workers to add to this shift",
            "Enter the id of workers that are in this shift to remove them"
        };   
        Shift shift = schedule.get(inputHandler.getCurrentOption());
        inputHandler.resetOption();
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.showMessage(message);
            shift.display();
            displayer.singleSeperate();
            System.out.println("Avaiable workers");

            for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
                Worker wkr = entry.getValue();
                if (shift.contain(wkr)) { continue; }
                wkr.shortDisplay();
            }

            inputHandler.getUserOption();

            Worker wkr = hiredWorkers.get(inputHandler.getCurrentOption());
            if (wkr == null) { inputHandler.raiseWarning(); continue; }
            if (shift.contain(wkr)) {
                shift.removeWorker(wkr);
            } else if (!shift.contain(wkr)) {
                shift.addWorker(wkr);
            }
        }
    }
    public void showSchedule() {
        String[] message = {
            "To go back enter 0",
            "This is our schedule",
            "To view add/remove workers enter the schedule number then choose a worker"
        };  
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.showMessage(message);

            for (Map.Entry<Integer, Shift> entry : schedule.entrySet()) {
                System.out.print(entry.getKey()+" / ");
                entry.getValue().display();
                displayer.dashSeperate();
            }

            inputHandler.getUserOption();

            if (schedule.containsKey(inputHandler.getCurrentOption())) {
                editSelectedShift();
            } else { inputHandler.raiseWarning(); }
        }
        inputHandler.resetOption();
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //Hire and fire workers system
    
    public void showWorkerDes(Worker worker) {
        String[] message = {
            "To go back enter 0",
            "This is woker desciption"
        };
        inputHandler.resetOption();
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.showMessage(message);

            worker.display();
            displayer.singleSeperate();

            if (worker.isEmployed()) {
                System.err.println("1 / Fire this worker");
            } else {
                System.err.println("1 / Hire this worker");
            }
            
            inputHandler.getUserOption();

            if (inputHandler.getCurrentOption() == 1) {
                if (worker.isEmployed()) {   
                    fireWorker(worker.getId());
                } else {
                    hireWorker(worker.getId());
                } 
                inputHandler.enter2Continue();
                // break; // include this break to end session after you fired or hired a worker
            } else { inputHandler.raiseWarning(); }
        }   
        inputHandler.resetOption();
    }
    public void showWorkerToHire() {
        String[] message = {
            "To go back enter 0",
            "This is a list of available workers to be hired",
            "To view worker description and hire them, enter their id"
        };       
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.showMessage(message);

            // Show avaiable workers to hire
            for (Map.Entry<Integer, Worker> entry : workerToHire.entrySet()) {
                entry.getValue().shortDisplay();
            }

            inputHandler.getUserOption();

            // check if the entered id exist
            if (workerToHire.containsKey(inputHandler.getCurrentOption())) {
                showWorkerDes(workerToHire.get(inputHandler.getCurrentOption())); // show the selected worker description
            } else { inputHandler.raiseWarning(); }
        }
        inputHandler.resetOption(); 
    }
    public void showWorkersInPosition(String position) {
        for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
            Worker wkr = entry.getValue();
            if (wkr.getPosition().equals(position)) { wkr.shortDisplay(); }
        }
    }
    public void showHiredWorker() {
        String[] message = {
            "To go back enter 0",
            "Enter a worker id to view their description"
        };          
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.showMessage(message);

            // Show hired workers
            System.out.println("Waiter: ");
            showWorkersInPosition("waiter");
            displayer.doubleSeperate();
            System.out.println("chef: ");
            showWorkersInPosition("chef");

            inputHandler.getUserOption();

            // check if the entered id exist
            if (hiredWorkers.containsKey(inputHandler.getCurrentOption())) {
                showWorkerDes(hiredWorkers.get(inputHandler.getCurrentOption())); // show the selected worker description
            } else { inputHandler.raiseWarning(); }
        }
        inputHandler.resetOption();
    }
    public void hireWorker(int workerID) {
        Worker worker = workerToHire.remove(workerID);
        hiredWorkers.put(workerID, worker);
        worker.setEmploymentState(true);
        System.out.println("Hired"+worker.getName());
    };
    public void fireWorker(int workerID) {
        Worker worker = hiredWorkers.remove(workerID);
        workerToHire.put(workerID, worker);
        worker.setEmploymentState(false);
        System.out.println("Fired"+worker.getName());
    };
}
