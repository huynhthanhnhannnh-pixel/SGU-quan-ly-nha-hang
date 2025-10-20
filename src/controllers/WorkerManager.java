package controllers;

import java.io.*;
import java.util.*;

import base.Worker;
import contracts.ManagerHandler;
import enums.WorkerType;
import utils.*;
import workerTypes.*;
import models.*;

public class WorkerManager implements ManagerHandler {
    private static WorkerManager self = null;
    private Displayer displayer = Displayer.getDisplayer();
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    private final int GO_BACK_OPTION = 0; 
    private final String[] SHIFT_NAMES = {
        "Sang Thu 2", "Chieu Thu 2",
        "Sang Thu 3", "Chieu Thu 3",
        "Sang Thu 4", "Chieu Thu 4",
        "Sang Thu 5", "Chieu Thu 5",
        "Sang Thu 6", "Chieu Thu 6",
        "Sang Thu 7", "Chieu Thu 7",
    };



    private HashMap<Integer, Worker> workerToHire = new HashMap<Integer, Worker>(); // available workers
    private HashMap<Integer, Worker> hiredWorkers = new HashMap<Integer, Worker>(); // hired workers
    private LinkedHashMap<Integer, Shift> schedule = new LinkedHashMap<Integer, Shift>();

    @Override
    public void showGeneralInfo() {
        String[] message = {
            "Day la trinh quan ly nhan vien sieu uy tin",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. thue/sa thai nhan vien -- tat nhien roi",
            "2. xep lich cho nhan vien",
            "3. kiem tra lich trong tuan",
            "Nhan vien cua chung ta se luon duoc an nhung mon an do dau bep Hannibal nau",
            "",
            "",
            "",
            "Ngai Hannibal se chuan bi mon ung thu than giai doan cuoi cho chung ta"
        };
        displayer.displayMessage(message);
    }
    @Override
    public void createReport() {
        System.out.println("Nhan vien A vua hut thuoc vua choi da");
        System.out.println("Nhan vien B moi mo tai khoan only quat");
        System.out.println("Nhan vien C vua bi cooked");
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // Init a storage of worker objects, read from Worker.txt
    private void initResources() {
        try (BufferedReader br = new BufferedReader(new FileReader("src\\resources\\Workers.txt"))) {
            String basicInfo; // read the odd lines
            int counter = 1;
            while ((basicInfo = br.readLine()) != null) {
                String description = br.readLine(); // go to next line(even line) then read it
                String[] parts = basicInfo.split(" ");
                String name = parts[0];
                int age = Integer.parseInt(parts[1]);
                String gender = parts[2];
                String position = parts[3];
                double salaries = Double.parseDouble(parts[4]);
                Worker worker = null;
                switch (WorkerType.fromPosition(position)) {
                    case WAITER:
                        worker = new Waiter(counter, name, age, gender, position, salaries, description);
                        workerToHire.put(counter, worker);
                        break;
                    case CHEF:
                        worker = new Chef(counter, name, age, gender, position, salaries, description);
                        workerToHire.put(counter, worker);
                        break;
                    case SUPPLY_MANAGER, WORKER_MANAGER, TABLE_MANAGER:
                        worker = new Manager(counter, name, age, gender, position, salaries, description);
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

    // Private constructor to enforce singleton
    private WorkerManager() {
        // Init schedule, create shift each shift name in SHIFT_NAMES
        for (int i = 0; i < SHIFT_NAMES.length; i++) {
            schedule.put((i + 1), new Shift(SHIFT_NAMES[i]));
        }
    }

    // Public method to get the single self
    public static WorkerManager getManager() {
        if (self == null) {
            self = new WorkerManager();
            self.initResources();
        } 
        return self;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Schedule system

    private void editSelectedShift() {
        String[] message = {
            "Nhap 0 de quay lai",
            "Nhap id cua nhan vien de add/remove nhan vien do vao/khoi lich"
        };   
        Shift shift = schedule.get(inputHandler.getCurrentOption());
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);
            shift.display();
            displayer.singleSeperate();
            System.out.println("Cac nhan vien chua co trong lich nay");

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
        inputHandler.resetOption();
    }
    public void showSchedule() {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la lich lam viec cua chung ta",
            "De xem lich hay nhap id cua lich"
        };  
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

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
    public Shift getShift(int shiftID) {
        return schedule.get(shiftID);
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Hire and fire workers system
    
    public void showWorkerDes(Worker worker) {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la thong tin nhan vien"
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            worker.display();
            displayer.singleSeperate();

            if (worker.isEmployed()) {
                System.out.println("1 / Sa thai nhan vien nay");
                System.out.println("2 / Tuong tac");
            } else {
                System.out.println("1 / Thue nguoi nay");
            }
            
            inputHandler.getUserOption();

            switch (inputHandler.getCurrentOption()) {
                case 1:

                    if (worker.isEmployed()) {   
                        fireWorker(worker.getId());
                    } else {
                        hireWorker(worker.getId());
                    } 
                    inputHandler.enter2Continue();

                    break;

                case 2:
                    worker.interact();
                    break;
        
                default:
                    inputHandler.raiseWarning();
                    break;
            }
        }   
        inputHandler.resetOption();
    }
    public void showWorkerToHire() {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la danh sach nhung nguoi lao dong ma ban co the thue",
            "De thue nguoi lao dong hay nhap id cua ho"
        };       
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            // Show available workers to hire
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
    public void showWorkersInPosition(WorkerType position) {
        for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
            Worker wkr = entry.getValue();
            if (WorkerType.fromPosition(wkr.getPosition()) == position) { wkr.shortDisplay(); }
        }
    }
    public void showHiredWorker() {
        String[] message = {
            "Nhap 0 de quay lai",
            "Nhap id cua nhan vien de xem thong tin cua ho"
        };          
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            // Show hired workers
            System.out.println("Managers: ");
            showWorkersInPosition(WorkerType.SUPPLY_MANAGER);
            showWorkersInPosition(WorkerType.WORKER_MANAGER);
            showWorkersInPosition(WorkerType.TABLE_MANAGER);
            displayer.doubleSeperate();
            System.out.println("Waiter: ");
            showWorkersInPosition(WorkerType.WAITER);
            displayer.doubleSeperate();
            System.out.println("chef: ");
            showWorkersInPosition(WorkerType.CHEF);

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
        add(worker);
    };
    public void fireWorker(int workerID) {
        remove(workerID);
    };

    @Override
    public void add(Object obj) {
        Worker worker = (Worker) obj;
        hiredWorkers.put(worker.getId(), worker);
        worker.setEmploymentState(true);
        System.out.println("Ban da thue "+worker.getName());
    };

    @Override
    public Worker remove(int objID) {
        Worker worker = hiredWorkers.remove(objID);
        workerToHire.remove(objID);
        worker.setEmploymentState(false);
        System.out.println("Ban da sa thai "+worker.getName());
        return worker;
    };

    @Override
    public Object search(Object objID) {

        return null;
    };
}
