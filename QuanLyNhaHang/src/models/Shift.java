package models;

import java.util.HashSet;
import base.Worker;

public class Shift {
    private final String shiftName;
    private HashSet<Worker> workerList = new HashSet<Worker>();
    
    public Shift(String shiftName) {
        this.shiftName = shiftName;
    }

    // Getter
    public String getShiftName() {
        return shiftName;
    }

    // Show all workers in this shift
    public void display() {
        System.out.println(shiftName + "\nWorkers of this shift:\n ------------------");
        for (Worker worker : workerList) { 
            System.out.println("id: "+worker.getId()+" / "+worker.getName()+" / "+worker.getPosition());
        }
    }

    // Add new worker to current schedule
    public void addWorker(Worker worker) {
        boolean success = workerList.add(worker);
        if (!success) {
            String message = String.format("Nhan vien %s da co trong ca nay, khong the them vao", worker.getName());
            System.out.println(message);
        }
    }

    // Remvoe worker from current schedule
    public void removeWorker(Worker worker) {
        boolean success = workerList.remove(worker);
        if (!success) {
            String message = String.format("Nhan vien %s khong ton tai trong ca nay", worker.getName());
            System.out.println(message);
        }
    }

    // Check if this shift already has this worker
    public boolean contain(Worker worker) {
        for (Worker _worker : workerList) {
            if (_worker == worker ) { return true; }
        }
        return false;
    }
}