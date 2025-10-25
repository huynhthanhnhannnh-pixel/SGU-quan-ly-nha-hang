package models;

import java.util.*;

public class Table {
    private final int size;
    private List<String> FOOD_MENU = new ArrayList<String>(); // menu của bàn

    // Constructor
    public Table(int size) {
        this.size = size;
        addMenu();
    }

    // Hiện các món ăn đã gọi(nếu có)
    public void display() {
        
    }

    // add menu cho bàn
    public void addMenu() {
        List<Dish> convertToMenu = controllers.DishManager.getManager().getDishList();
        for (Dish dish : convertToMenu){
            FOOD_MENU.add(dish.getName());
        }
    }


    // Hàm này để random món ăn của bàn
    public List<String> orderRNG() {
        List<String> orders = new ArrayList<>();
        Random random = new Random();
        // Danh sách món ăn có thể order
        List<String> menuItems = FOOD_MENU;
        
        // Random số lượng món (từ 7 đến 10 món)
        int numberOfItems = random.nextInt(10) + 7;
        
        // Thêm món ngẫu nhiên vào order
        for (int i = 0; i < numberOfItems; i++) {
            int randomIndex = random.nextInt(menuItems.size());
            orders.add(menuItems.get(randomIndex));
        }

        return orders;
    }



        // Đưa khách hàng vào bàn nếu như vẫn còn chỗ ngồi
    // public boolean addCustomer(Customer customer) {
    //     if (isFull()) {
    //         return false;
    //     }
    //     customers.add(customer);
    //     return true;
    // }

    // Reset bàn(đưa bàn về trạng thái trống)
    // public void reset() {
    //     customers.clear();
    // }

    // Lấy trạng thái hiện tại của bàn
    // public TableState getState() {
        // bàn trống
        // if (customers.isEmpty()) {
        //     return TableState.EMPTY;
        // bàn đã đầy
        // } else if (customers.size() >= size) {
        //     return TableState.FULL;
        // bàn đang có người ngồi
    //     } else {
    //         return TableState.OCCUPIED;
    //     }
    // }

    // Kiểm tra xem bàn có trông không
    // public boolean isEmpty() {
    //     return customers.isEmpty();
    // }

    // Kiểm tra xem bàn có loại khách hàng nào 
    // public boolean containsCustomer(Customer customer) {
    //     return customers.contains(customer);
    // }

    // Kiểm tra xem bàn đã đầy chưa
    // public boolean isFull() {
    //     return customers.size() >= size;
    // }

    // Kiểm tra số khách của bàn
    // public int getNumOfCustomer() {
    //     return customers.size();
    // }

}