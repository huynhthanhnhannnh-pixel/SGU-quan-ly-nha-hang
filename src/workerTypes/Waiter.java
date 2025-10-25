package workerTypes;

import controllers.*;
import enums.OrderState;
import java.util.List;

import models.*;


public class Waiter extends base.Worker {
     
    
    public Waiter(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void interact() {
        
    }

    @Override
    public void startWorking() {

        Order order = EventHandler.getEventHandler().getTable();
           if (order == null) {
              System.out.println("Waiter: khong co order,nghi! (startWorking returned null)");
            return;
            }

        OrderState state = order.getState();

        System.out.println("Waiter: picked up order for table: " + order.getTable() + " | state=" + state + " | dishesCount=" + order.getDishes().size());

            switch (state) {
                case NEW:

                    makeOrder(order);                    
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs();
                    break;

                case UNFINISHED:
                    retakeOrder(order);
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs();
                    break;

                case COMPLETED:
                    double bill = order.calculateAmount();
                    System.out.println("Ban " + order.getTable() + " da thanh toan: " + bill);
                    System.out.println("Waiter: order completed, preparing to record transaction. bill=" + bill);
                    java.time.LocalDate today = java.time.LocalDate.now();
                    System.out.println("Waiter: calling RevenueManager.addTransaction(date=" + today + ", amount=" + bill + ")");
                    controllers.RevenueManager.getManager().addTransaction(today, order);
                    EventHandler.getEventHandler().notifyTableManager();
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
        
        // Sử dụng Order.getNumOfUnsatisfiedRequest để chạy vòng lăp for
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món
        // Loại trừ các món có trong excludedOrders
    
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