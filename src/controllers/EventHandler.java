package controllers;

import base.Worker;
import workerTypes.Waiter;
import workerTypes.Chef;
import enums.*;
import java.util.*;
import models.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class EventHandler {
    private WorkerManager wrkMgr = WorkerManager.getManager();
    // FIXME: Gọi getShift() ngay ở thời điểm khởi tạo trường có thể gây vấn đề nếu
    // WorkerManager chưa hoàn toàn khởi tạo hoặc phụ thuộc vào thứ tự khởi tạo singleton.
    // Đề xuất: chuyển sang lazy-init trong constructor hoặc khi bắt đầu ca.
    private Shift curShift = wrkMgr.getShift(1); // lấy thông tin ca làm, mặc định là sáng thứ 2
    private static EventHandler self;

    private List<Worker> workerList = new ArrayList<>();
    // FIXME: Sử dụng raw ArrayList với tham số (0) gây cảnh báo unchecked conversion.
    // Đề xuất: sử dụng generic: new ArrayList<Order>() hoặc new ArrayList<>()
    private List<Order> orderList= new ArrayList<>(); // danh sách các order đã được lấy
    private List<Table> unsatisfiedTables = new ArrayList<>(); // danh sách các bàn cần phục vụ hay chưa thảo order

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // Private constructor to enforce singleton
    private EventHandler() {

    }

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
            System.out.println("EventHandler: startShift(" + shiftID + ") loaded shift='" + curShift.getShiftName() + "' with workersCount=" + (workerList == null ? 0 : workerList.size()));

            // If shift has no workers, create temporary waiter and chef so simulator can run without manual staffing.
            if (workerList == null || workerList.isEmpty()) {
                System.out.println("EventHandler: no workers in shift " + shiftID + ", creating temporary chef & waiter for simulation");
                // create simple temporary workers with negative ids to avoid colliding with real workers
                Waiter tempWaiter = new Waiter(-1000, "TempWaiter", 20, "N/A", WorkerType.WAITER.getPosition(), 0.0, "auto-created");
                Chef tempChef = new Chef(-1001, "TempChef", 30, "N/A", WorkerType.CHEF.getPosition(), 0.0, "auto-created");
                // add to the current shift so other parts that query the shift can see them
                curShift.addWorker(tempWaiter);
                curShift.addWorker(tempChef);
                // refresh workerList from shift
                workerList = curShift.getAllWorkers();
            }

            if (workerList != null) {
                for (Worker w : workerList) {
                    System.out.println("EventHandler: worker -> id=" + w.getId() + ", name=" + w.getName() + ", position=" + w.getPosition());
                }
            }
        }

    }
    // public void startShift(int shiftID) {
    //     // lưu danh sách các chef và waiter
    //     curShift = wrkMgr.getShift(shiftID);
    //     if (curShift != null) {
    //         workerList = curShift.getAllWorkers();

    //         System.out.println("EventHandler: startShift(" + shiftID + ") loaded shift='" + curShift.getShiftName() + "' with workersCount=" + (workerList == null ? 0 : workerList.size()));

    //         // If shift has no workers, create temporary waiter and chef so simulator can run without manual staffing.
    //         if (workerList == null || workerList.isEmpty()) {
    //             System.out.println("EventHandler: no workers in shift " + shiftID + ", creating temporary chef & waiter for simulation");
    //             // create simple temporary workers with negative ids to avoid colliding with real workers
    //             Waiter tempWaiter = new Waiter(-1000, "TempWaiter", 20, "N/A", WorkerType.WAITER.getPosition(), 0.0, "auto-created");
    //             Chef tempChef = new Chef(-1001, "TempChef", 30, "N/A", WorkerType.CHEF.getPosition(), 0.0, "auto-created");
    //             // add to the current shift so other parts that query the shift can see them
    //             curShift.addWorker(tempWaiter);
    //             curShift.addWorker(tempChef);
    //             // refresh workerList from shift
    //             workerList = curShift.getAllWorkers();
    //         }

    //         if (workerList != null) {
    //             for (Worker w : workerList) {
    //                 System.out.println("EventHandler: worker -> id=" + w.getId() + ", name=" + w.getName() + ", position=" + w.getPosition());
    //             }
    //         }
    //     }
    public void endShift() { 
        notifySupplyManager(); // thông báo cho quản lý thực phẩm
        // FIXME: Gán workerList = null sẽ gây NullPointerException tại các phương thức
        // notifyXXX() khác (chúng lặp qua workerList mà không kiểm tra null).
        // Đề xuất: gọi workerList.clear() hoặc kiểm tra null trước khi lặp.
        workerList = null; 
    }
    
    // Khi có khách đặt bàn hay chef gửi lại order thì kêu waiter đầu tiên đang rảnh làm việc
    public void notifyWaiters() {
    // NOTE: workerList có thể null nếu endShift() đã được gọi; cần kiểm tra trước khi lặp.
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
    // public Order getTable() {
    //     Table table;
    //     if (!orderList.isEmpty()) {
    //         table = unsatisfiedTables.remove(0);
    //         for (Order order : orderList) {
    //             if (table.equals(order.getTable())) {
    //                 return order;
    //             }
    //         }
    //         return new Order(table);

    //     } else {
    //         return null;
    //     }
    // }
    public Order getTable() {
        if (unsatisfiedTables == null || unsatisfiedTables.isEmpty()) return null;
            Table table = unsatisfiedTables.remove(0);
            for (Order order : orderList) {
                if (table.equals(order.getTable())) {
                    return order;
                }
            }
            return new Order(table);
    }

    // thêm bàn vào danh sách các bàn cần phục vụ
    // public void addTable(Table table){
    //     // FIXME: unsatisfiedTables có thể null; cần khởi tạo trước khi dùng.
    //     // Đề xuất: nếu (unsatisfiedTables == null) unsatisfiedTables = new ArrayList<>();
    //     if (unsatisfiedTables.contains(table)) {
    //         return;
    //     }
    //     unsatisfiedTables.add(table);
    // }
    public void addTable(Table table) {
        if (unsatisfiedTables == null) unsatisfiedTables = new ArrayList<>();
        if (!unsatisfiedTables.contains(table)) unsatisfiedTables.add(table);
    }
    // // lấy order đầu tiên trong orderList, order co the null
    public Order getOrder() {
        // FIXME: Nếu orderList rỗng, remove(0) sẽ ném IndexOutOfBoundsException.
        // Đề xuất: kiểm tra orderList != null && !orderList.isEmpty() trước khi remove.
         if (!orderList.isEmpty()) {
            return orderList.remove(0);
        } else {
            return null;
        }
    }

    // tạo order mới
    public void addOrder(Order newOrder) {
        if (orderList == null) orderList = new ArrayList<>();
        orderList.add(newOrder);
        // Ensure the table is in the unsatisfiedTables queue so waiters can pick it up
        if (unsatisfiedTables == null) unsatisfiedTables = new ArrayList<>();
        if (newOrder != null && newOrder.getTable() != null && !unsatisfiedTables.contains(newOrder.getTable())) {
            unsatisfiedTables.add(newOrder.getTable());
        }
    }
    public Order orderList() {
        return null;
    }
}