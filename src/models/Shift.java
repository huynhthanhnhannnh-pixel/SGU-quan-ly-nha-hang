package models;

import java.util.HashSet;
import utils.Displayer;
import base.Worker;

public class Shift {
    private final String shiftName;
    private final int id;
    private Displayer displayer = Displayer.getDisplayer();
    private HashSet<Worker> workerList = new HashSet<Worker>();
    
    public Shift(String shiftName, int id) {
        this.shiftName = shiftName;
        this.id = id;
    }

    // Getter
    public String getShiftName() { return shiftName; }
    public int getID() { return id; }

    // Hiện thị tất cả nhân viên trong ca này
    public void display() {
        System.out.println();
        displayer.printFormatLine(new int[]{4, 20});
        System.out.printf("| %-4d | %-20s |\n", id, shiftName);
        displayer.printFormatLine(new int[]{27});
        System.out.printf("| %-27s |\n", "Nhan vien trong ca:");
        displayer.printFormatLine(new int[]{4, 20});
        System.out.printf("| %-4s | %-20s |\n", "ID", "Ten");
        displayer.printFormatLine(new int[]{4, 20});
        for (Worker worker : workerList) { 
           worker.shortDisplay();
        }
        displayer.printFormatLine(new int[]{4, 20});
    }

    // Thêm nhân viên vào ca làm
    public void addWorker(Worker worker) {
        boolean success = workerList.add(worker);
        if (!success) {
            String message = String.format("Nhan vien %s da co trong ca nay, khong the them vao", worker.getName());
            System.out.println(message);
        }
    }

    // Loại bỏ nhân viên khỏi ca làm
    public void removeWorker(Worker worker) {
        boolean success = workerList.remove(worker);
        if (!success) {
            String message = String.format("Nhan vien %s khong ton tai trong ca nay", worker.getName());
            System.out.println(message);
        }
    }

    // Kiểm tra nếu ca này đã có nhân viên đó
    public boolean contain(Worker worker) {
        for (Worker _worker : workerList) {
            if (_worker == worker ) { return true; }
        }
        return false;
    }

    // Lấy danh sách các nhân viên của ca làm
    public HashSet<Worker> getAllWorkers() {
        return workerList;
    }

    // Kiểm tra xem shift có ít nhất 1 waiter và 1 chef
    public boolean isShiftValid() {
        boolean hasWaiter = false;
        boolean hasChef = false;
        for (Worker worker : workerList) {
            if (worker.getPosition().equalsIgnoreCase("Waiter")) {
                hasWaiter = true;
            } 
            else if (worker.getPosition().equalsIgnoreCase("Chef")) {
                hasChef = true;
            }
            if (hasWaiter && hasChef) {
                return true;
            }
        }
        return false;
    }

}