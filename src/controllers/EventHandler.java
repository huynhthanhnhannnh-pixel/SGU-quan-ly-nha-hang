package controllers;

import base.Worker;
import enums.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import models.*;
import utils.UserInputHandler;

public class EventHandler {
    private WorkerManager wrkMgr = WorkerManager.getManager();
    private SupplyManager spMgr = SupplyManager.getManager();
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    private Shift curShift = wrkMgr.getShift(1); // lấy thông tin ca làm, mặc định là sáng thứ 2
    private static EventHandler self;

    private HashSet<Worker> workerList = new HashSet<>(); // danh sách các nhân viên trong ca làm hiện tại 
    private List<Order> orderList= new ArrayList<>(); // danh sách các order đã được lấy
    private List<Table> unsatisfiedTables = new ArrayList<>(); // danh sách các bàn cần phục vụ hay chưa thảo order

    // Số ngày của chương trình, sử dụng số ngày để tìm shift
    private int totalDays = 0;
    // private Integer currentDay = 0; //  Nếu currentDay = 0 -> chủ nhật nghỉ làm, 1->thứ 2, 2->thứ 3,...
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

    public boolean isNotActive() {
        return isNotActive;
    }

    // Bắt đầu ca làm
    public void startShift(int id) {
        totalDays++;

        // Nghỉ làm vào chủ nhật
        if (id == 0) { 
            System.out.println("Chu nhat nha hang nghi lam");
            return;
        }
        
        // Dừng nếu ca làm không tồn tại
        curShift = wrkMgr.getShift(id);
        if (curShift == null) { 
            System.out.println("Ca lam khong ton tai: " + id);
            return;
        }

        // Nếu shift không có nhân viên hay không đủ nhân viên thì kết thúc ngày
        if (curShift != null) {
            boolean shiftValid = curShift.isShiftValid();
            if (!shiftValid) {
                System.out.println("Ca lam hien tai khong du nhan vien de mo cua");
            return;
            }
        }

        System.out.println("Bat dau ca lam: " + curShift.getShiftName() + "(Tong ngay: " + totalDays + " )");
        workerList = curShift.getAllWorkers(); // lây danh sách các nhân viên
        isNotActive = false;
    }

    public void endShift() { 
        // if (currentDay == 0) { // Nghỉ làm vào chủ nhật
        //     System.out.println("Nghi chu nhat");
        //     return;
        // }
        if (workerList == null) {
            System.out.println("Khong co nhan vien trong ca lam hien tai");
            UserInputHandler.getUserInputHandler().enter2Continue();
            return;
        }

        // ====================================================
        // Thông tin được hiện thị vào cuối ngày
        spMgr.createReport(); // thông báo cho quản lý thực phẩm

        



        // ====================================================

        System.out.println("Ket thuc ngay lam");
        workerList = null; 
        isNotActive = true; // dat ket thuc ngay
    }
    
    // Khi có khách đặt bàn hay chef gửi lại order thì kêu waiter đầu tiên đang rảnh làm việc
    public void notifyWaiters(Order order) {
        if (isNotActive) { // Không thể gọi nếu nhà hàng chưa mở cửa
            endShift();
            return;
        } 

        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.WAITER.getPosition())) {
                worker.startWorking(order);
                break;
            }
        }
    }

    // Sau khi waiter lấy order xong thì kêu chef đầu tiên đang rảnh bắt đầu nấu ăn
    public void notifyChefs(Order order) {
        if (isNotActive) { // Không thể gọi nếu nhà hàng chưa mở cửa
            endShift();
            return;
        } 

        for (Worker worker : workerList) {
            if (worker.getPosition().equals(WorkerType.CHEF.getPosition())) {
                worker.startWorking(order);
                break;
            }
        }
    }

    // Lấy order từ bàn cần được phục vụ, chọn bàn đầu tiên trong queue(hàng đợi)
    public Order getOrderOfTable() {
        if (unsatisfiedTables == null || unsatisfiedTables.isEmpty()) return null;
            Table table = unsatisfiedTables.remove(0);
            for (Order order : orderList) {
                if (table.equals(order.getTable())) {
                    return order;
                }
            }
            return new Order(table);
    }

    // Bỏ bàn vào danh sách cần được phục vụ
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
    
    public List<Order> getOrderList() {
        return orderList;
    }
    
    /**
     * Truncate common cache copy files so they become empty.
     * This will try both "src/cache" and "cache" folders and clear
     * Dishes(copy).txt, Ingredients(copy).txt, HiredWorkers.txt and Schedule.txt if present.
     */
    public void clearCache() {
        Path[] dirs = { Paths.get("src", "cache"), Paths.get("cache") };
        String[] filenames = { "Dishes(copy).txt", "Ingredients(copy).txt", "HiredWorkers.txt", "Schedule.txt" };
        for (Path dir : dirs) {
            for (String name : filenames) {
                try {
                    Path p = dir.resolve(name);
                    if (Files.exists(p)) {
                        // write zero bytes -> truncate file
                        Files.write(p, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi khi xóa nội dung file cache " + name + " tại " + dir + ": " + e.getMessage());
                }
            }
        }
    }

    public void End(){
        // register shutdown hook to clear cache copy files when program exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                clearCache();
                System.out.println("Cache files cleared on shutdown.");
            } catch (Exception ex) {
                System.err.println("Error clearing cache on shutdown: " + ex.getMessage());
            }
        }));
    }
}   
