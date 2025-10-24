package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import utils.Displayer;

import enums.WorkerType;
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

    // Lấy Nhân viên đâu tiên có vị trí trùng với vị trí đã cho
    public Worker findFirstWorkerWithPosition(WorkerType position) {
        for (Worker worker : workerList) {
            if (worker.getPosition().equals(position.getPosition())) {
                return worker;
            }
        }
        return null;
    }

    // Lấy danh sách các nhân viên không phải là quản lý
    public List<Worker> getWorkersThatIsNotManager() {
        List<Worker> temp = new ArrayList<>();
        for (Worker worker : workerList) {
            if (
                worker.getPosition().equals(WorkerType.SUPPLY_MANAGER.getPosition()) ||
                worker.getPosition().equals(WorkerType.WORKER_MANAGER.getPosition()) ||
                worker.getPosition().equals(WorkerType.TABLE_MANAGER.getPosition())
            ) { continue; }
            temp.add(worker);
        }
        return temp;
    }
}