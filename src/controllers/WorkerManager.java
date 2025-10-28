package controllers;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import base.Worker;
import contracts.ManagerHandler;
import enums.WorkerType;
import utils.*;
import workerTypes.*;
import models.*;

public class WorkerManager implements ManagerHandler {
    private static WorkerManager self = null;
    private Displayer displayer = Displayer.getDisplayer();
    private int[] displayLineConfig = {4, 20, 5, 10, 25, 30};
    // private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    // private final int GO_BACK_OPTION = 0; 
    private final String[] SHIFT_NAMES = {
        "Thu 2",
        "Thu 3",
        "Thu 4",
        "Thu 5",
        "Thu 6",
        "Thu 7",
    };

    private HashMap<Integer, Worker> workerToHire = new HashMap<Integer, Worker>(); // available workers
    private HashMap<Integer, Worker> hiredWorkers = new HashMap<Integer, Worker>(); // hired workers
    private static boolean context = false; // true if in hiring context/ false firing context
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
        System.out.println("Nhan vien A ban 10 mon");
        System.out.println("Nhan vien B ban 20 mon");
        System.out.println("Nhan vien C nau 30 mon");
    }

    @Override
    public void add(Object worker) {
        Worker wkr = (Worker) worker;
        hiredWorkers.put(wkr.getId(), workerToHire.remove(wkr.getId()));
        wkr.setEmploymentState(true);
        System.out.println("Ban da thue "+wkr.getName());
    }
    @Override
    public Object remove(Object worker) {
        Worker wrk = hiredWorkers.remove(((Worker) worker).getId());
        if (wrk != null) {
            wrk.setEmploymentState(false);
            workerToHire.put(wrk.getId(), wrk);
            System.out.println("Ban da sa thai " + wrk.getName());
        } else {
            System.out.println("Khong tim thay nhan vien de sa thai voi id: " + wrk);
        }

        // Xóa nhân viên ra khỏi lịch
        for (Map.Entry<Integer, Shift> entry : schedule.entrySet()) {
            System.out.println();
            entry.getValue().removeWorker(wrk);
        }

        return wrk;
    }
    @Override
    public Object search(Object worker) { 
        int workerID = ((Worker) worker).getId();
        Worker wkr = hiredWorkers.get(workerID);
        if (wkr != null) {
            return wkr;
        } else {
            System.out.println("Khong tim thay nhan vien voi id: " + workerID);
            return null;
        }
     }
    @Override
    public Object Input() {
        Displayer.getDisplayer().singleSeperate();
        System.out.println();
        System.out.println("Nhap ID nhan vien:");
        inputHandler.getUserOption();
        int id = inputHandler.getCurrentOption();
        Worker worker = null;

        if (context) {
            worker = self.workerToHire.get(id);
        } else {
            worker = self.hiredWorkers.get(id);
        }

        return worker;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // Writers
    private void saveSchedule() {
        BufferedWriter bw = null;
        try {
            Path p1 = Paths.get("cache", "Schedule.txt");
            Path p2 = Paths.get("src", "cache", "Schedule.txt");

            if (Files.exists(p1)) {
                bw = Files.newBufferedWriter(p1, StandardCharsets.UTF_8);
            } else if (Files.exists(p2)) {
                bw = Files.newBufferedWriter(p2, StandardCharsets.UTF_8);
            } else {
                throw new FileNotFoundException("Schedule.txt not found in classpath or cache folders");
            }

            
            // Write each shift and its workers
            for (Map.Entry<Integer, Shift> entry : schedule.entrySet()) {
                Shift shift = entry.getValue();
                bw.write(String.valueOf(shift.getID()));
                bw.newLine();
                for (Worker worker : shift.getAllWorkers()) {
                    bw.write(String.valueOf(worker.getId()));
                    bw.newLine();
                }
                bw.newLine(); // empty line to separate shifts
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException ignored) {}
            }
        }
    }
    private void saveHiredWorkers() {
        BufferedWriter bw = null;
        try {
            Path p1 = Paths.get("cache", "HiredWorkers.txt");
            Path p2 = Paths.get("src", "cache", "HiredWorkers.txt");

            if (Files.exists(p1)) {
                bw = Files.newBufferedWriter(p1, StandardCharsets.UTF_8);
            } else if (Files.exists(p2)) {
                bw = Files.newBufferedWriter(p2, StandardCharsets.UTF_8);
            } else {
                throw new FileNotFoundException("HiredWorkers.txt not found in classpath or cache folders");
            }

            // Write each hired worker ID in a new line
            for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
                bw.write(String.valueOf(entry.getKey()));
                bw.newLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException ignored) {}
            }
        }
    }

    // Loaders
    private void loadSchedule() {
        // do nothing if file is null
        BufferedReader br = null;
        try {
            // try classpath resource first
            InputStream is = WorkerManager.class.getClassLoader().getResourceAsStream("cache/Schedule.txt");
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            // Check secondary location 
            } else {
                Path path = Paths.get("src", "cache", "Schedule.txt");
                if (Files.exists(path)) {
                    br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                } else {
                    throw new FileNotFoundException("Schedule.txt not found in classpath or cache folders");
                }
            }

            String line;
            // read each line as worker id, the first number is shift ID
            // an empty line means the shift end
            boolean shiftEnded = true;
            int shiftID = -1;
            while ((line = br.readLine()) != null) {
                // end shift when empty line is found
                if (line.trim().isEmpty()) {
                    shiftEnded = true;

                // if shift ended then read the next number as shift ID else use it as worker ID
                } else if (shiftEnded) {
                    shiftID = Integer.parseInt(line.trim());
                    shiftEnded = false;

                } else if (!shiftEnded) {
                    int workerID = Integer.parseInt(line.trim());
                    Worker worker = hiredWorkers.get(workerID);
                    if (worker != null) {
                        Shift shift = schedule.get(shiftID); // Get current shift
                        shift.addWorker(worker);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
        }
    }
    private void loadHiredWorkers() {
        // do nothing if file is null
        BufferedReader br = null;
        try {
            // try classpath resource first
            InputStream is = WorkerManager.class.getClassLoader().getResourceAsStream("cache/HiredWorkers.txt");
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            // Check secondary location 
            } else {
                Path path = Paths.get("src", "cache", "HiredWorkers.txt");
                if (Files.exists(path)) {
                    br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                } else {
                    throw new FileNotFoundException("HiredWorkers.txt not found in classpath or cache folders");
                }
            }


            String line;
            // read each line as worker ID then move worker from workerToHire to hiredWorkers
            while ((line = br.readLine()) != null) {
                int workerID = Integer.parseInt(line.trim());
                Worker worker = workerToHire.remove(workerID);
                if (worker != null) {
                    hiredWorkers.put(workerID, worker);
                    worker.setEmploymentState(true);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
        }
    }
    
    // Init a storage of worker objects, read from Worker.txt
    private void initResources() {
        BufferedReader br = null;
        try {
            // try classpath resource first
            InputStream is = WorkerManager.class.getClassLoader().getResourceAsStream("resources/Workers.txt");
            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
            
            // check secondary location
            } else { 
                
                Path path = Paths.get("src", "resources", "Workers.txt");
                if (Files.exists(path)) {
                    br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                } else {
                    throw new FileNotFoundException("Workers.txt not found in classpath or resources folders");
                }
            }

            String basicInfo; // read the odd lines
            while ((basicInfo = br.readLine()) != null) {
                String description = br.readLine(); // go to next line(even line) then read it
                if (description == null) description = "";
                String[] parts = basicInfo.split(" ");
                if (parts.length < 6) continue; // skip invalid lines

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                int age = Integer.parseInt(parts[2]);
                String gender = parts[3];
                String position = parts[4];
                double salaries = Double.parseDouble(parts[5]);

                Worker worker = null;
                switch (WorkerType.fromPosition(position)) {
                    case WAITER:
                        worker = new Waiter(id, name, age, gender, position, salaries, description);
                        workerToHire.put(id, worker);
                        break;
                    case CHEF:
                        worker = new Chef(id, name, age, gender, position, salaries, description);
                        workerToHire.put(id, worker);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
        }

        // Init schedule, create shift each shift name in SHIFT_NAMES
        for (int i = 0; i < SHIFT_NAMES.length; i++) {
            schedule.put((i + 1), new Shift(SHIFT_NAMES[i], (i + 1)));
        }

        loadHiredWorkers();
        loadSchedule();
    }

    // Private constructor to enforce singleton
    private WorkerManager() {}

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
            "Nhap id cua nhan vien de add/remove nhan vien do vao/khoi ca lam"
        };   
        Shift shift = schedule.get(inputHandler.getCurrentOption());
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);
            shift.display();
            displayer.singleSeperate();
            System.out.println("Cac nhan vien chua co trong ca nay");

            displayer.printFormatLine(new int[]{4, 20});
            System.out.printf("| %-4s | %-20s |\n", "ID", "Ten");
            displayer.printFormatLine(new int[]{4, 20});
            for (Map.Entry<Integer, Worker> entry : hiredWorkers.entrySet()) {
                Worker wkr = entry.getValue();
                if (shift.contain(wkr)) { continue; } // Khong in nhung nhan vien da co trong ca lam
                wkr.shortDisplay();
            }
            displayer.printFormatLine(new int[]{4, 20});

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
                entry.getValue().display();
            }

            inputHandler.getUserOption();

            if (schedule.containsKey(inputHandler.getCurrentOption())) {
                editSelectedShift();
            } else { inputHandler.raiseWarning(); }
        }
        inputHandler.resetOption();

        saveSchedule();
    }
    public Shift getShift(int shiftID) {
        return schedule.get(shiftID);
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Hire and fire workers system
    
    private void displayInfoBar() {
        displayer.printFormatLine(displayLineConfig);
        System.out.printf("| %-4s | %-20s | %-5s | %-10s | %-25s | %-30s |\n", "ID", "Ten", "Tuoi", "Gioi tinh", "Chuc vu", "Luong thang(nghin dong)");
        displayer.printFormatLine(displayLineConfig);
    }

    // Hien thi thong tin chi tiet cua nhan vien
    public void showWorkerDes(Worker worker) {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la thong tin nhan vien"
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            System.out.println("\n");            
            displayInfoBar();
            worker.gridDisplay();
            displayer.printFormatLine(displayLineConfig);
            System.out.println("\nMo ta:\n" + worker.getDescription() + "\n");
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
                        remove(worker);
                    } else {
                        // hireWorker(worker.getId());
                        add(worker);
                    } 
                    inputHandler.enter2Continue();

                    break;

                // case 2:
                //     // worker.interact();
                //     break;
        
                default:
                    inputHandler.raiseWarning();
                    break;
            }
        }   
        inputHandler.resetOption();
        saveHiredWorkers();
    }
    public void showWorkerToHire() {
        String[] message = {
            "Nhap 0 de quay lai",
            "Day la danh sach nhung nguoi lao dong ma ban co the thue",
            "De thue nguoi lao dong hay nhap id cua ho"
        };       
        context = true;
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            // Show available workers to hire
            System.out.printf("| %-4s | %-20s |\n", "ID", "Ten");
            displayer.printFormatLine(new int[]{4, 20});
            for (Map.Entry<Integer, Worker> entry : workerToHire.entrySet()) {
                entry.getValue().shortDisplay();
            }
            displayer.printFormatLine(new int[]{4, 20});

            Object obj = Input();
            Worker worker = (Worker) obj;
            if (worker == null) { inputHandler.raiseWarning(); continue; }

            // check if the entered id exist
            if (workerToHire.containsKey(worker.getId())) {
                showWorkerDes(workerToHire.get(worker.getId())); // show the selected worker description
            } else { inputHandler.raiseWarning(); }
        }
        inputHandler.resetOption(); 

        // Save hired workers to cache
        saveHiredWorkers();
        context = false;
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

            System.out.println("\nPhuc vu: ");
            displayInfoBar();
            showWorkersInPosition(WorkerType.WAITER);
            displayer.printFormatLine(displayLineConfig);

            System.out.println("\nDau bep: ");
            displayInfoBar();
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

}
