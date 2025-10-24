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
    private int[] displayLineConfig = {3, 20, 5, 10, 25, 30};
    // private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    // private final int GO_BACK_OPTION = 0; 
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
        String[] intro = { "Trinh quan ly nhan vien" };
        String[] options = {
            "Xem danh sach nhan vien co the thue",
            "Xem danh sach nhan vien da thue",
            "Xem lich lam viec",
            "Quay lai"
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(intro);
            displayer.displayOptions(options);

            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) { inputHandler.resetOption(); break; }

            switch (choice) {
                case 1:
                    showWorkerToHire();
                    break;
                case 2:
                    showHiredWorker();
                    break;
                case 3:
                    showSchedule();
                    break;
                default:
                    inputHandler.raiseWarning();
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
        }
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
        BufferedReader br = null;
        try {
            // try classpath resource first
            InputStream is = WorkerManager.class.getClassLoader().getResourceAsStream("resources/Workers.txt");
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
            } else {
                java.nio.file.Path p1 = java.nio.file.Paths.get("resources", "Workers.txt");
                java.nio.file.Path p2 = java.nio.file.Paths.get("src", "resources", "Workers.txt");
                if (java.nio.file.Files.exists(p1)) {
                    br = java.nio.file.Files.newBufferedReader(p1, java.nio.charset.StandardCharsets.UTF_8);
                } else if (java.nio.file.Files.exists(p2)) {
                    br = java.nio.file.Files.newBufferedReader(p2, java.nio.charset.StandardCharsets.UTF_8);
                } else {
                    throw new FileNotFoundException("Workers.txt not found in classpath or resources folders");
                }
            }

            String basicInfo; // read the odd lines
            int counter = 1;
            while ((basicInfo = br.readLine()) != null) {
                String description = br.readLine(); // go to next line(even line) then read it
                if (description == null) description = "";
                String[] parts = basicInfo.split(" ");
                if (parts.length < 5) continue;
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
                    case SUPPLY_MANAGER:
                    case WORKER_MANAGER:
                    case TABLE_MANAGER:
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
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
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

            displayer.printFormatLine(new int[]{3, 20});
            System.out.printf("| %-3s | %-20s |\n", "ID", "Ten");
            displayer.printFormatLine(new int[]{3, 20});
            for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
                Worker wkr = entry.getValue();
                if (shift.contain(wkr)) { continue; } // Khong in nhung nhan vien da co trong ca lam
                wkr.shortDisplay();
            }
            displayer.printFormatLine(new int[]{3, 20});

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
                System.out.println();
                System.out.print(entry.getKey()+" / ");
                entry.getValue().display();
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
    
    // Hien thi thong tin chi tiet cua nhan vien
    public void showWorkerDes(Worker worker) {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la thong tin nhan vien"
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            System.out.printf("| %-3s | %-20s | %-5s | %-10s | %-25s | %-30s |\n", "ID", "Ten", "Tuoi", "Gioi tinh", "Chuc vu", "Luong thang(nghin dong)");
            displayer.printFormatLine(displayLineConfig);
            worker.gridDisplay();
            displayer.printFormatLine(displayLineConfig);
            System.out.println("Mo ta:\n" + worker.getDescription() + "\n");
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
            System.out.printf("| %-3s | %-20s |\n", "ID", "Ten");
            displayer.printFormatLine(new int[]{3, 20});
            for (Map.Entry<Integer, Worker> entry : workerToHire.entrySet()) {
                entry.getValue().shortDisplay();
            }
            displayer.printFormatLine(new int[]{3, 20});

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
            if (WorkerType.fromPosition(wkr.getPosition()) == position) { wkr.gridDisplay(); }
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
            System.out.println("Quan ly: ");
            displayer.printFormatLine(displayLineConfig);
            System.out.printf("| %-3s | %-20s | %-5s | %-10s | %-25s | %-30s |\n", "ID", "Ten", "Tuoi", "Gioi tinh", "Chuc vu", "Luong thang(nghin dong)");
            displayer.printFormatLine(displayLineConfig);
            showWorkersInPosition(WorkerType.SUPPLY_MANAGER);
            showWorkersInPosition(WorkerType.WORKER_MANAGER);
            showWorkersInPosition(WorkerType.TABLE_MANAGER);
            displayer.printFormatLine(displayLineConfig);

            displayer.doubleSeperate();

            System.out.println("Phuc vu: ");
            displayer.printFormatLine(displayLineConfig);
            System.out.printf("| %-3s | %-20s | %-5s | %-10s | %-25s | %-30s |\n", "ID", "Ten", "Tuoi", "Gioi tinh", "Chuc vu", "Luong thang(nghin dong)");
            displayer.printFormatLine(displayLineConfig);
            showWorkersInPosition(WorkerType.WAITER);
            displayer.printFormatLine(displayLineConfig);

            displayer.doubleSeperate();

            System.out.println("Dau bep: ");
            displayer.printFormatLine(displayLineConfig);
            System.out.printf("| %-3s | %-20s | %-5s | %-10s | %-25s | %-30s |\n", "ID", "Ten", "Tuoi", "Gioi tinh", "Chuc vu", "Luong thang(nghin dong)");
            displayer.printFormatLine(displayLineConfig);
            showWorkersInPosition(WorkerType.CHEF);
            displayer.printFormatLine(displayLineConfig);

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
    public Object remove(Object objID) {
        Worker worker = hiredWorkers.remove(objID);
        workerToHire.remove(objID);
        if (worker != null) {
            worker.setEmploymentState(false);
            System.out.println("Ban da sa thai " + worker.getName());
        } else {
            System.out.println("Khong tim thay nhan vien de sa thai voi id: " + objID);
        }
        return worker;
    };

    // Chua co sua dung den(ko can sua dung den)
    @Override
    public Object search(Object objID) {

        return null;
    };
}
