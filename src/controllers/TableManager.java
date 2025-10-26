package controllers;
import java.util.*;
import java.time.LocalDate;

import contracts.ManagerHandler;
import models.Table;
import utils.*;
import models.Order;
import models.DailyRevenue;

public class TableManager implements ManagerHandler {
    private static TableManager self;
    private EventHandler eventHlr = EventHandler.getEventHandler();
    private Displayer displayer = Displayer.getDisplayer();
    private LocalDate date = LocalDate.now();

    private int lowerLimit = 5000000; // 5,000,000
    private int upperLimmit = 500000000; // 500,000,000
    private double target = lowerLimit; // Target cua ngay

    private HashMap<Integer, Table> tableList = new HashMap<Integer, Table>(); // Danh sách bàn, <Mã bàn, bàn> ví dụ: tìm bàn số 5 => <5, bàn>
    private int numOfTable = 10; // Số lượng bàn ăn

    @Override
    public void showGeneralInfo() {
        String[] message = {
            "Day la trinh quan ly ban an sieu nhanh, con khach an co nhanh ko thi deo biet",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. Kiem tra xem ban nao trong, ban nao da duoc dat",
            "2. add/remove nhom khach hang vao tung ban",
            "Va dat biet la Menu cua chung ta duoc lam boi sieu dau bep ratatouille va And Cong Nhan(ACN)"
        };
        displayer.displayMessage(message);
    }
    
    @Override
    public void createReport() {
        System.out.println("Ban 1, 2, 3 con trong, Ban 4 thi bi con nho chubby an me roi");
    }
    @Override
    public void add(Object obj){

    }
    @Override
    public Object remove(Object objID){
        return null;
    }
    @Override
    public Object search(Object objID){
        return null;
    }

    @Override
    public Object Input(){
        return null;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // khởi tạo bàn với số lương bàn đã cho
    private void loadTables(int numberOfTable) {
        // Sử dụng vòng lăp để tạo bàn rồi lưu vào dang sách bàn
        for ( int i = 1; i <= numberOfTable; i++){
            Table table = new Table(8);
            tableList.put(i, table);
        }
    }
    
    // Private constructor to enforce singleton
    private TableManager() {
        loadTables(numOfTable);
    }

    // Public method to get the single self
    public static TableManager getManager() {
        if (self == null) {
            self = new TableManager();
        }
        return self;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Your codes go here

    private int getDayNumber(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // Thứ Hai=1, ... Chủ nhật=7
        return dayOfWeek == 7 ? 0 : dayOfWeek; // Nếu là Chủ nhật => 0
    }
    
    public HashMap<Integer, Table> getTableList() { return tableList; }

    public LocalDate getDate() { return date; } 
    
    // Đặt mục tiêu danh thu
    private void setTargetProfit() {
        System.out.println("Target hien tai: " + target); 
        System.out.print("Dat target moi: "); 
        inputHandler.getUserOption();
        target = (double) inputHandler.getCurrentOption(); 
        do{
            if(target < lowerLimit){ 
                System.out.print("Target thap qua, khong du tra tien nhan vien, dat lai target:  ");
                inputHandler.getUserOption();
                target = (double) inputHandler.getCurrentOption();  
            }
            else if(target > upperLimmit){ // 500,000,000
                System.out.print("Nha hang e lam, dat target chi cho cao vay, dat lai target:  ");
                inputHandler.getUserOption();
                target = (double) inputHandler.getCurrentOption();  
            }
        }
        while(target < lowerLimit || target > upperLimmit);

        System.out.println("Target hom nay la: "+ target);   
    }

    // Đóng cửa nhà hàng(được tự động gọi bởi startSimation)
    private void closeRestaurant() {
        System.out.println("==== DONG CUA HANG ====");
        eventHlr.endShift();
        date = date.plusDays(1);
        inputHandler.enter2Continue();
    }

    // Bắt đầu chạy giả lập, gọi waiter và chef(eventHandler.notifyWaiter/notifyChef)
    private void startSimulation() {
        // ================================================================================
        // Kiểm tra trước khi bắt đầu

        System.out.println("Check co nguyen lieu nao het han su dung hay khong: "); 
        // Kiểm tra và xóa nguyên liệu ko đủ hsd
        if(SupplyManager.getManager().deleteExpiredandLowQuantityIngredients(date) == 0) 
            System.out.println("Hang con han su dung");
        else{
            SupplyManager.getManager().deleteExpiredandLowQuantityIngredients(date);
        }

        // ================================================================================

        System.out.println("===== BAT DAU GIA LAP NHA HANG =====");
        System.out.println("Ngay: " + date + "\n\n");  

        eventHlr.startShift(getDayNumber(date)); // bắt đầu làm
        // Nếu không đủ nhân viên hoặc chủ nhật thì nghỉ
        if (eventHlr.isNotActive()) {
            closeRestaurant(); // Đóng cửa nhà hàng
            return; // Nếu nhà hàng không hoạt động, thoát khỏi mô phỏng
        }

        // ================================================================================
        // Phục vụ cho đến khi đủ danh thu

        
        double todayProgress = 0.0;
        while (todayProgress < target) {
            
            Table table = TableManager.getManager().getTableList().get(1); // Lấy bàn số 1 để mô phỏng
            EventHandler.getEventHandler().addTable(table);
            Order order = new Order(table);
            EventHandler.getEventHandler().addOrder(order);  // Tạo order cho bàn số 1
            eventHlr.notifyWaiters(EventHandler.getEventHandler().getOrderOfTable()); // Bắt đầu kêu waiter ra phục vụ


            todayProgress += 1000000; // Testing, ô fix dòng này sau


            // Đọc doanh thu hiện tại (Waiter sẽ ghi transaction khi thanh toán)
            // double revenueNow = controllers.RevenueManager.getManager().getRevenueOfDate(date);
            // double delta = revenueNow - revenueBefore;
            // if (delta > 0) {
            //     totalRevenue += delta;
            //     revenueBefore = revenueNow; // cập nhật baseline
            //     displayer.dashSeperate(); 
            //     System.out.println("Thu: " + delta + " | Tong: " + totalRevenue); 
            //     displayer.dashSeperate(); 
            // }
            // inputHandler.enter2Continue();
        }

        closeRestaurant();
    }
    
    // Hiện thị bản điều khiển giả lập
    public void showSimulator(){
        String[] header = {
            "Nhap 0 de quay lai",
            "De them/xoa nguyen lieu thi hay quay lai(bam 0) va thuc hien thu cong",
        };
        String[] options ={
            "Dat target",
            "OPEN",
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);
            inputHandler.getUserOption();

            switch (inputHandler.getCurrentOption()) {
                case 1:
                    // Đặt target của cửa hàng, mặc định là lowerLimmit(5,000,000) xem ở hàng 17
                    setTargetProfit();

                    break;
                case 2:
                    // Bắt đầu chạy giả lập, gọi waiter và chef(eventHandler.notifyWaiter/notifyChef)
                    startSimulation();

                    break;
                default:
                    inputHandler.raiseWarning(); // Nói người dùng là lựa chọn không phù hợp
                    break;
            }
        }
        displayer.singleSeperate();
        inputHandler.resetOption();
        date = date.plusDays(1); // Chuyển sang ngày mới
    }
}
