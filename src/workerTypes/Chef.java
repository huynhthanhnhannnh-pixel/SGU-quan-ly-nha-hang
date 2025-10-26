package workerTypes;

import controllers.*;
import java.time.LocalDate;
import java.util.Map;
import models.Dish;
import models.Order;

public class Chef extends base.Worker {
    
    public Chef(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void interact() {}

    @Override
    public void startWorking(Order order) {
        // Order order = EventHandler.getEventHandler().getOrder(); 
        if (order == null) {
            System.out.println("Chef: khong co order nao, nghi!");
            return;
        }
    
        boolean allAvailable = true;

        for (String dish : order.getDishes()) {
            System.out.println("---------" + dish+"");
            loop1: // khai bao loop bên ngoài để break
            for (Dish dishMENU : DishManager.getManager().getDishList()){
                if (dish.equalsIgnoreCase(dishMENU.getName())){
                    Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                
                    for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                        String ingredientName = entry.getKey();
                        int requiredAmount = entry.getValue();

                        if (!SupplyManager.getManager().checkIngredients(ingredientName, requiredAmount)) {
                            System.out.println("Chef: Khong du nguyen lieu cho mon:  " + dishMENU.getName()+"");
                            
                            order.addExcludedDish(dishMENU.getName());
                            allAvailable = false;
                            break loop1; // Không cần kiểm tra tiếp nguyên liệu của món này
                        }
                    }
                }
            }
        }

        if (allAvailable) {
            System.out.println("Chef: du nguyen lieu - bat dau nau!");
            System.out.println("");
            for (String dish : order.getDishes()) {
                for (Dish dishMENU : DishManager.getManager().getDishList()){
                    if (dish.equalsIgnoreCase(dishMENU.getName())){
                        Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                    
                        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                            String ingredientName = entry.getKey();
                            int requiredAmount = entry.getValue();
                            SupplyManager.getManager().getIngredient(ingredientName, requiredAmount);
                        }

                        System.out.println("Chef: Da nau xong mon:  " + dish);
        
                    }
                }
            }

            System.out.println("Chef: Hoan thanh tat ca mon do");
            EventHandler.getEventHandler().notifyWaiters(order);
            double bill = order.calculateAmount(); // vừa cộng tiền vào amount trong order luôn, vừa lưu amount vào biến bill
            System.out.println("Khach hang da thanh toan: " + bill);
            LocalDate today = TableManager.getManager().getDate();          
            RevenueManager.getManager().addTransaction(today, order);
            // EventHandler.getEventHandler().notifyTableManager();

        } else {
            System.out.println("Chef: Thieu nguyen lieu - yeu cau lay lai order");
            System.out.println("");

            order.updateOrder(); // loại món thiếu ra khỏi dishes
            EventHandler.getEventHandler().addOrder(order); // đưa bàn vào hàng chờ
            EventHandler.getEventHandler().notifyWaiters(order); // kêu waiter tới lấy lại order
        }
    }
        

    @Override
    public void stopWorking() {
        
    }
}