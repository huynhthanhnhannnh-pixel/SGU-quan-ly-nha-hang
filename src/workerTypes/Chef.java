package workerTypes;

import java.util.Map;
import controllers.EventHandler;
import controllers.SupplyManager;
import models.Order;
import models.Dish;

public class Chef extends base.Worker {
    
    public Chef(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void interact() {
        
    }

    @Override
    public void startWorking() {
        EventHandler event = EventHandler.getEventHandler();
        SupplyManager sm = SupplyManager.getManager();
        Order order = new Order(null);
        for(String dishName : order.getDishes()){
        if (dishName == null) {
            System.out.println("Chef: Không có order nào, nghỉ!");
            return;
        }
    }
        boolean allAvailable = true;

        for (String dishName : order.getDishes()) {
           Dish dish = SupplyManager.getManager().getDishByName(dishName); 
        Map<String, Integer> requiredIngredients = dish.readIngredients(); // nguyên liệu cần cho món

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
        String ingredientName = entry.getKey();
        int requiredAmount = entry.getValue();

        // Gọi hàm checkIngredients của bạn
        if (!sm.checkIngredients(ingredientName, requiredAmount)) {
            System.out.println("❌ Không đủ nguyên liệu cho món " + dishName);
            order.addExcludedDish(dishName);
            allAvailable = false;
            break; // Không cần kiểm tra tiếp nguyên liệu của món này
        }
    }
        }

        if (allAvailable) {
        System.out.println("Chef: Đủ nguyên liệu -> Bắt đầu nấu!");

        for (String dishName : order.getDishes()) {
           Dish dish = SupplyManager.getManager().getDishByName(dishName); 
        Map<String, Integer> requiredIngredients = dish.readIngredients(); // nguyên liệu cần cho món

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
        String ingredientName = entry.getKey();
        int requiredAmount = entry.getValue();

        sm.getIngredient(ingredientName, requiredAmount);
        }

        System.out.println("Chef: Đã nấu xong món " + dishName);
    }


            System.out.println("Chef: Hoàn tất! -> Báo waiter mang món ra");
            event.notifyWaiters();

        } else {
            System.out.println("Chef: Thiếu nguyên liệu -> Cập nhật lại Order và gửi về waiter");

            order.updateOrder(); // loại món thiếu ra khỏi dishes
            event.addTable(order.getTable()); // đưa bàn vào hàng chờ
            event.notifyWaiters(); // kêu waiter tới lấy lại order
        }
    }
        

    @Override
    public void stopWorking() {
        
    }
}   