package workerTypes;

import controllers.*;
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
    public void startWorking() {
        Order order = EventHandler.getEventHandler().getOrder(); 
        if (order == null) {
            System.out.println("Chef: khong co order nao, nghi!");
            return;
        }
    
        boolean allAvailable = true;

        for (String dish : order.getDishes()) {
            loop1: // khai bao loop bên ngoài để break
            for (Dish dishMENU : dishManager.getManager().getDishList()){
                if (dish.equalsIgnoreCase(dishMENU.getName())){
                    Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                
                    for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                        String ingredientName = entry.getKey();
                        int requiredAmount = entry.getValue();

                        if (!SupplyManager.getManager().checkIngredients(ingredientName, requiredAmount)) {
                            System.out.println("Khong du nguyen lieu cho mon:  " + dishMENU.getName());
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

            for (String dish : order.getDishes()) {
                for (Dish dishMENU : dishManager.getManager().getDishList()){
                    if (dish.equalsIgnoreCase(dishMENU.getName())){
                        Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                    
                        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                            String ingredientName = entry.getKey();
                            int requiredAmount = entry.getValue();
                            SupplyManager.getManager().getIngredient(ingredientName, requiredAmount);
                        }

                        System.out.println("Chef: Da nau xong " + dish);
        
                    }
                }
            }

            System.out.println("Chef: Hoan thanh mon do");
            EventHandler.getEventHandler().notifyWaiters();

        } else {
            System.out.println("Chef: Thieu nguyen lieu - yeu cau lay lai order");

            order.updateOrder(); // loại món thiếu ra khỏi dishes
            EventHandler.getEventHandler().addOrder(order); // đưa bàn vào hàng chờ
            EventHandler.getEventHandler().notifyWaiters(); // kêu waiter tới lấy lại order
        }
    }
        

    @Override
    public void stopWorking() {
        
    }
}