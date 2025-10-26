package workerTypes;

import controllers.*;
import enums.OrderState;
import java.util.*;
import models.*;


public class Waiter extends base.Worker {
     
    
    public Waiter(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void interact() {
        
    }


    public void startWorking(Order order) {

        // Order order = EventHandler.getEventHandler().getOrderOfTable(); // SAI VKL ( CHƯA CÓ ORDER TRONG ORDERLIST -> VÔ DỤNG)
        //    if (order == null) {
        //       System.out.println("Waiter: khong co order,nghi! ");
        //     return;
        //     }
        
        // EventHandler.getEventHandler().addTable(table); // Đưa bàn vào hàng chờ để waiter tạo order
        // EventHandler.getEventHandler().getOrderList().add(order); // Thêm order vào danh sách order
        OrderState state = order.getState();

        System.out.println("");
            switch (state) {
                case NEW:

                    makeOrder(order);                    
                    System.out.println("Nhan vien phuc vu toi ban lay mon, so mon duoc goi: " + order.getDishes().size());
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs(order);
                    break;

                case UNFINISHED:
                    System.out.println("Nhan vien dang phuc vu lai ban");
                    retakeOrder(order);
                    System.out.println("Nhan vien phuc vu toi ban lay lai mon, so mon duoc goi lai: " + order.getNumOfUnsatisfiedRequest());
                    order.setNumOfUnsatisfiedRequest(0); // reset sau khi gọi lại
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs(order);
                    break;

                case COMPLETED:
                    // double bill = order.calculateAmount();
                    // System.out.println("Ban da thanh toan: " + bill);
                    // System.out.println("Nhan vien phuc phu tinh tien bill=" + bill);
                    // java.time.LocalDate today = java.time.LocalDate.now();
                    // System.out.println("Quan ly tinh cong vao tong doanh thu" + today + ", amount=" + bill + ")");
                    // controllers.RevenueManager.getManager().addTransaction(today, order);
                    // EventHandler.getEventHandler().notifyTableManager();
                    EventHandler.getEventHandler().getOrderList().remove(order); // Xóa order khỏi danh sách
                    break;

                default:
                    System.out.println(getName() + " -> Trang thai khong xac dinh " + state);
                    break;
            }

        
        // Nếu UNFINISHED thì dùng retakeOrder
    }

    @Override
    public void stopWorking() {
        
    }
    // Lấy order của bàn
    private void makeOrder(Order order) {
        for(String dish : order.getTable().orderRNG())
            order.writeOrder(dish);
    }
    
    // Lấy lại order nấu mấy món trước không đủ đồ để nấu
    private void retakeOrder(Order order) {
        Table table = order.getTable();
        int retryCount = order.getNumOfUnsatisfiedRequest();
        List<String> excluded = order.getExcludedDishes(); 
       
        for (int i = 0; i < retryCount; i++) {
            String dish;
            do {
                List<String> randomList = table.orderRNG(); // trả về List<String>
                dish = randomList.get(0);
            } while (excluded.contains(dish)); // không chọn lại món lỗi
            order.writeOrder(dish);

        }
    }

}
// if (table == null) return;

        // // Prefer using number of unsatisfied requests; fall back to excluded size if not set
        // int retryCount = order.getNumOfUnsatisfiedRequest();
        // if (retryCount <= 0) retryCount = Math.max(1, order.getExcludedDishes().size());

        // List<String> excluded = order.getExcludedDishes() != null ? order.getExcludedDishes() : new ArrayList<>();
        // Random rnd = new Random();

        // for (int i = 0; i < retryCount; i++) {
        //     String selected = null;
        //     // try a few times to find a non-excluded dish
        //     int attempts = 0;
        //     while (attempts < 10) {
        //         List<String> randomList = table.orderRNG(); // returns a random list of menu items
        //         if (randomList == null || randomList.isEmpty()) break;
        //         selected = randomList.get(rnd.nextInt(randomList.size()));
        //         if (!excluded.contains(selected)) break;
        //         attempts++;
        //     }
        //     if (selected == null || excluded.contains(selected)) {
        //         // cannot find a replacement dish; stop retrying
        //         break;
        //     }
        //     order.writeOrder(selected);
             
        // Sử dụng Order.getNumOfUnsatisfiedRequest để chạy vòng lăp for
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món
        // Loại trừ các món có trong excludedOrders