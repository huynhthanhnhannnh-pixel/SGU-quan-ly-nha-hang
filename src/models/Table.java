package models;

import java.util.*;
import controllers.SupplyManager;
import enums.TableState;

public class Table {
    private final int size;
    private final List<Customer> customers;
    private List<String> FOOD_MENU = null; // menu của bàn

    // Constructor
    public Table(int size) {
        this.size = size;
        this.customers = new ArrayList<>();
        addMenu();
    }

    // Hiện các khách hàng trong bàn và các món ăn đã gọi(nếu có)
    public void display() {
        
    }

    // add menu cho bàn
    public void addMenu() {
        FOOD_MENU = SupplyManager.getManager().randomMenuToday(10); // Lấy menu của quán
    }

    // Đưa khách hàng vào bàn nếu như vẫn còn chỗ ngồi
    public boolean addCustomer(Customer customer) {
        if (isFull()) {
            return false;
        }
        customers.add(customer);
        return true;
    }

    // Reset bàn(đưa bàn về trạng thái trống)
    public void reset() {
        customers.clear();
    }

    // Lấy trạng thái hiện tại của bàn
    public TableState getState() {
        // bàn trống
        if (customers.isEmpty()) {
            return TableState.EMPTY;
        // bàn đã đầy
        } else if (customers.size() >= size) {
            return TableState.FULL;
        // bàn đang có người ngồi
        } else {
            return TableState.OCCUPIED;
        }
    }

    // Kiểm tra xem bàn có trông không
    public boolean isEmpty() {
        return customers.isEmpty();
    }

    // Kiểm tra xem bàn có loại khách hàng nào 
    public boolean containsCustomer(Customer customer) {
        return customers.contains(customer);
    }

    // Kiểm tra xem bàn đã đầy chưa
    public boolean isFull() {
        return customers.size() >= size;
    }

    // Kiểm tra số khách của bàn
    public int getNumOfCustomer() {
        return customers.size();
    }

    // Hàm này để random món ăn của bàn
    public String orderRNG() {
        // List<String> orders = new ArrayList<>();
        // Random random = new Random();

        

        // return orders;
        return "Testing";
    }
}