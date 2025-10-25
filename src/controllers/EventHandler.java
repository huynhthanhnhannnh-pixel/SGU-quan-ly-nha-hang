package controllers;

import base.Worker;
import enums.*;
import java.util.*;
import models.*;
import utils.UserInputHandler;

public class EventHandler {
    private WorkerManager wrkMgr = WorkerManager.getManager();
    private Shift curShift = wrkMgr.getShift(1); // lấy thông tin ca làm, mặc định là sáng thứ 2
    private static EventHandler self;

    private HashSet<Worker> workerList = new HashSet<>(); // danh sách các nhân viên trong ca làm hiện tại 
    private List<Order> orderList= new ArrayList<>(); // danh sách các order đã được lấy
    private List<Table> unsatisfiedTables = new ArrayList<>(); // danh sách các bàn cần phục vụ hay chưa thảo order

    // Số ngày của chương trình, sử dụng số ngày để tìm shift
    private int totalDays = 0;
    private Integer currentDay = 0; //  Nếu currentDay = 0 -> chủ nhật nghỉ làm, 1->thứ 2, 2->thứ 3,...
    private boolean isNotActive = false; // Trạng thái nhà hàng đang mở hay đóng

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
    public void startShift() {
        // lưu danh sách các chef và waiter
        boolean notReady = false; // Kiểm tra là tiếp tục có được không
        totalDays++;
        currentDay = totalDays % 7;
        if (currentDay == 0) { // Nghỉ làm vào chủ nhật
            System.out.println("Chu nhat nha hang nghi lam");
            notReady = true;
        }
        // Dừng nếu ca làm không tồn tại
        curShift = wrkMgr.getShift(currentDay);
        System.out.println("Current day: " + currentDay);
        if (curShift != null) { 
            System.out.println("Ca lam khong ton tai: " + currentDay);
            notReady = true;
        }

        boolean shiftValid = curShift.isShiftValid();
        // Nếu shift không có nhân viên hay không đủ nhân viên thì kết thúc ngày
        if (shiftValid) {
            System.out.println("Ca lam khong co du waiter hoac chef");
            notReady = true;
        }

        // Skip ngày nếu không thỏa mãn
        if (notReady) {
            endShift();
            return;
        }

        System.out.println("Bat dau ca lam: " + curShift.getShiftName() + "(Tong ngay: " + totalDays + " )");
        workerList = curShift.getAllWorkers(); // lây danh sách các nhân viên
        isNotActive = false;
    }

    public void endShift() { 
        if (currentDay == 0) { // Nghỉ làm vào chủ nhật
            System.out.println("Nghi chu nhat");
            return;
        }
        if (workerList == null) {
            System.out.println("Khong co nhan vien trong ca lam hien tai");
            UserInputHandler.getUserInputHandler().enter2Continue();
            return;
        }
        notifySupplyManager(); // thông báo cho quản lý thực phẩm
        // FIXME: Gán workerList = null sẽ gây NullPointerException tại các phương thức
        // notifyXXX() khác (chúng lặp qua workerList mà không kiểm tra null).
        // Đề xuất: gọi workerList.clear() hoặc kiểm tra null trước khi lặp.
        // workerList.clear();
        workerList = null; 
        isNotActive = true;
    }
    
    // Khi có khách đặt bàn hay chef gửi lại order thì kêu waiter đầu tiên đang rảnh làm việc
    public void notifyWaiters() {
        if (isNotActive) { // Không thể gọi nếu nhà hàng chưa mở cửa
            endShift();
            return;
        } 

        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.WAITER.getPosition())) {
                worker.startWorking();
                break;
            }
        }
    }

    // Sau khi waiter lấy order xong thì kêu chef đầu tiên đang rảnh bắt đầu nấu ăn
    public void notifyChefs() {
        if (isNotActive) { // Không thể gọi nếu nhà hàng chưa mở cửa
            endShift();
            return;
        } 

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