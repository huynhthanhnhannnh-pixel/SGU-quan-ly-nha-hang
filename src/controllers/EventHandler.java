package controllers;

import base.Worker;
import enums.*;
import java.util.*;
import models.*;

public class EventHandler {
    private WorkerManager wrkMgr = WorkerManager.getManager();
    private Shift curShift = wrkMgr.getShift(1); // lấy thông tin ca làm, mặc định là sáng thứ 2
    private static EventHandler self;

    private List<Worker> workerList;
    private List<Order> orderList; // danh sách các order đã được lấy
    private List<Table> unsatisfiedTables; // danh sách các bàn cần phục vụ hay chưa thảo order

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // Private constructor to enforce singleton
    private EventHandler() {}

    // Public method to get the single self
    public static EventHandler getEventHandler() {
        if (self == null) {
            self = new EventHandler();
        } 
        return self;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+

    // Bắt đầu ca làm
    public void startShift(int shiftID) {
        // lưu danh sách các chef và waiter
        curShift = wrkMgr.getShift(shiftID);
        if (curShift != null) {
            workerList = curShift.getAllWorkers();
        }
    }
    public void endShift() { 
        notifySupplyManager(); // thông báo cho quản lý thực phẩm
        workerList = null; 
    }
    
    // Khi có khách đặt bàn hay chef gửi lại order thì kêu waiter đầu tiên đang rảnh làm việc
    public void notifyWaiters() {
    for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.WAITER.getPosition())) {
                worker.startWorking();
                break;
            }
        }
    }

    // Sau khi waiter lấy order xong thì kêu chef đầu tiên đang rảnh bắt đầu nấu ăn
    public void notifyChefs() {
        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.CHEF.getPosition())) {
                worker.startWorking();
                break;
            }
        }
    }

    public void notifyTableManager() {
        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.TABLE_MANAGER.getPosition())) {
                worker.startWorking();
                break;
            }
        }
    }

    public void notifySupplyManager() {
        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.SUPPLY_MANAGER.getPosition())) {
                worker.startWorking();
                break;
            }
        }
    }

    // lấy Order chứa bàn cần được phục vụ, nếu order chưa có thì tạo mới
    public Order getTable() {
        Table table = unsatisfiedTables.remove(0);
        for (Order order : orderList) {
            if (table.equals(order.getTable())) {
                return order;
            }
        }
        return new Order(table);
    }

    // thêm bàn vào danh sách các bàn cần phục vụ
    public void addTable(Table table){
        if (unsatisfiedTables.contains(table)) {
            return;
        }
        unsatisfiedTables.add(table);
    }

    // lấy order đầu tiên trong orderList
    public Order getOrder() {
        return orderList.remove(0);
    }

    // tạo order mới
    public void addOrder(Order newOrder) {
        orderList.add(newOrder);
    }
    
}