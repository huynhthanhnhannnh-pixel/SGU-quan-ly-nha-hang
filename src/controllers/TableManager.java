package controllers;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import contracts.ManagerHandler;
import models.Table;
import utils.*;
import workerTypes.Chef;
import workerTypes.Waiter;
import controllers.*;
import enums.OrderState;
import models.Order;
import base.*;
import models.DailyRevenue;

public class TableManager implements ManagerHandler {
    private static TableManager self;
    private Displayer displayer = Displayer.getDisplayer();
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    private HashMap<Integer, Table> tableList = new HashMap<Integer, Table>(); // Danh sách bàn, <Mã bàn, bàn> ví dụ: tìm bàn số 5 => <5, bàn>
    private int numOfTable = 10; // Số lượng bàn ăn
    int GO_BACK_OPTION = 0;

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

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // khởi tạo bàn với số lương bàn đã cho
    private void loadTables(int numberOfTable) {
        // Sử dụng vòng lăp để tạo bàn rồi lưu vào dang sách bàn
        for ( int i = 1; i <= numberOfTable; i++){
            Table table = new Table(8);
            tableList.put(i,table);
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

    // Chuẩn bị bàn ăn cho khách
    // public void prepareTable(int tableID) {
        // dựa vào tableID lấy ra object Table từ tableList(danh sách bàn)

        // thêm khách vào bàn

        

    // }

    // 2.1 random khách hàng (chua lam gi) 
    // public Customer customerRGN() {
    //     return null;
    // }

    public void showSimulator(){
        Scanner sc = new Scanner(System.in);
        LocalDate today = LocalDate.now();
        String[] header = {"Nhap 0 de quay lai", "Mo phong ban hang"};
        String[] options ={
            "Bat dau ngay moi"
        };
        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) { inputHandler.resetOption(); break; }
            switch (choice) {
                case 1:{
                    System.out.println("Nguyen lieu het han su dung:   "); 
                    if(SupplyManager.getManager().deleteExpiredandLowQuantityIngredients(today)==0)
                    {System.out.println("Hang con han su dung");}
                    else{
                    SupplyManager.getManager().deleteExpiredandLowQuantityIngredients(today);}
                    System.out.print("Dat target hom nay:   "); 
                    Double money = sc.nextDouble(); 
                    do{
                    
                    if(money < 5000000){
                        System.out.print("Target thap qua, khong du tra tien nhan vien, dat lai target:  ");
                        money =sc.nextDouble(); }
                        else if( money >50000000){
                        System.out.print("Nha hang e lam, dat target chi cho cao vậy, dat lai target:  ");
                        money =sc.nextDouble(); 
                    }
                    
                }
                    
                    while(money >5000000 && money <50000000);
    
                       System.out.println("Target hom nay la: "+ money);
                       simulatorChefAndWaiter(money);

                   
                    break;
                        }
                    default:
                    System.out.println("Lua chon khong hop le");
    }
                   
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
}


    public void simulatorChefAndWaiter(double target){
        String[] header = {"Nhap 0 de quay lai", "Mo phong ban hang"};
        String[] options ={
            "OPEN",
            "CLOSE"
    };
        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) { inputHandler.resetOption(); break; }
        
            switch (choice) {
                case 1: {
                    System.out.println("=== BAT DAU GIA LAP NHA HANG ===");
                    Table table = tableList.get(1);
                    
                    EventHandler.getEventHandler().addTable(table);
                    EventHandler.getEventHandler().startShift(1);
                    // Sau đó gọi getTable() để Waiter tạo order
                    Order order = EventHandler.getEventHandler().getTable();
                    if (order == null) {
                        System.out.println("khong co order nao duoc tao");
                        return; // hoặc tiếp tục vòng lặp tùy logic
                    }
                    //double bill = order.calculateAmount(); 
                    double totalRevenue = 0.0;
                    int attempts = 0;
                    int maxAttempts = 1000;
                    java.time.LocalDate today = java.time.LocalDate.now();
                    DailyRevenue dr = new DailyRevenue(today);
                    RevenueManager.getManager().getRevenueRecords().put(today, dr);
                    double revenueBefore = RevenueManager.getManager().getRevenueOfDate(today);
                    while (totalRevenue < target&& attempts < maxAttempts) {
                        attempts++;
                       // Đưa bàn vào hàng chờ để waiter tạo order
                        EventHandler.getEventHandler().addTable(table);

                        // Kích hoạt waiter/chef xử lý ngay (thực thi đồng bộ trong cùng luồng)
                        
                        EventHandler.getEventHandler().notifyWaiters();
                        EventHandler.getEventHandler().notifyChefs();

                        // Đọc doanh thu hiện tại (Waiter sẽ ghi transaction khi thanh toán)
                        double revenueNow = controllers.RevenueManager.getManager().getRevenueOfDate(today);
                        double delta = revenueNow - revenueBefore;
                        if (delta > 0) {
                            totalRevenue += delta;
                            revenueBefore = revenueNow; // cập nhật baseline
                            displayer.dashSeperate(); 
                            System.out.println("Thu: " + delta + " | Tong: " + totalRevenue); 
                            displayer.dashSeperate(); 
                        }
                    }

                        if (totalRevenue >= target) {
                            System.out.println("Tong doanh thu: " + totalRevenue );
                            System.out.println("DAT DUONG TARGET DAT RA");
                        } else {
                            System.out.println("khong dat target sau " + attempts + " lan thu. Doanh thu hien tai:  " + totalRevenue);
                        }
                            
                        System.out.println("=== KET THUC GIA LAP ===");
                        break;
                        }
                case 2: {
                    System.out.println("=== DONG CUA HANG ===");
                    return; // hoặc break nếu muốn quay lại menu
                }
                default:
                    System.out.println("Lua chon khong hop le");
                    }
    }
            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
}
}
    

    
