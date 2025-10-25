package workerTypes;

import controllers.EventHandler;
import controllers.SupplyManager;
import java.util.Map;
import models.Dish;
import models.Order;

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
        controllers.dishManager dm = controllers.dishManager.getManager();
        SupplyManager sm = SupplyManager.getManager();
        Order order = new Order(null);   
        if (order.getDishes().isEmpty()) {
            System.out.println("Chef: Không có order nào, nghỉ!");
            return;
        }
    
        boolean allAvailable = true;

        for (String dish : order.getDishes()) {
            loop1:
            for (Dish dishMENU : dm.getDishList()){
                if (dish.equalsIgnoreCase(dishMENU.getName())){
                    Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                
                for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                    String ingredientName = entry.getKey();
                    int requiredAmount = entry.getValue();

                    // Gọi hàm checkIngredients của bạn
                    if (!sm.checkIngredients(ingredientName, requiredAmount)) {
                        System.out.println("❌ Không đủ nguyên liệu cho món " + dishMENU.getName());
                        order.addExcludedDish(dishMENU.getName());
                        allAvailable = false;
                        break loop1; // Không cần kiểm tra tiếp nguyên liệu của món này
            }
        }
    }
    }
        }

        if (allAvailable) {
        System.out.println("Chef: Đủ nguyên liệu -> Bắt đầu nấu!");

        for (String dish : order.getDishes()) {
            for (Dish dishMENU : dm.getDishList()){
                if (dish.equalsIgnoreCase(dishMENU.getName())){
        Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
        String ingredientName = entry.getKey();
        int requiredAmount = entry.getValue();

        sm.getIngredient(ingredientName, requiredAmount);
        }

        System.out.println("Chef: Đã nấu xong món " + dish);
    }
}
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