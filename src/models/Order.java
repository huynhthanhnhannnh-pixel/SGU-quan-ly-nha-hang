package models;

import java.util.*;

import controllers.DishManager;
import enums.*;

public class Order {
    private Table table;
    private double amount;
    List<String> dishes = new ArrayList<String>();
    List<String> excludedDishes = new ArrayList<String>();
    private int numOfUnsatisfiedRequest = 0; // chef đọc từ numOfUnsatisfiedRequest đến dishes.size để nấu

    public Order(Table table) {
        this.table = table;
    }

    public Table getTable() { return table; }
    public  int getNumOfUnsatisfiedRequest() { return numOfUnsatisfiedRequest; }
    public double getAmount() { return amount; }
    public List<String> getDishes(){ return dishes; }
    public List<String> getExcludedDishes(){ return excludedDishes; }

    public void writeOrder(String dishName) {   dishes.add(dishName); }

    // chef thêm món không thể nấu được vào danh sách loại trừ
    public void addExcludedDish(String dishName) { excludedDishes.add(dishName); }

    // chef update lại order để thêm món không thể nấu được
    public void updateOrder() {
        for (String excludedDish : excludedDishes) {
            int length = dishes.size();
            for (int i = 0; i < length; i++) {
                if (excludedDish.equals(dishes.get(i))) {
                    dishes.remove(i);
                    length--;
                }
            }
        }
    }

    public OrderState getState() {
        if (dishes.size() == 0) {
            return OrderState.NEW;
        } else if (numOfUnsatisfiedRequest > 0) {
            return OrderState.UNFINISHED;
        } else if (dishes.size() > 0 && numOfUnsatisfiedRequest == 0) {
            return OrderState.COMPLETED;
        }
        return OrderState.COMPLETED; //hỏi chấm?
    }

    //Random ra là chắc chắn món đó nằm trong menu của món thì tại sao lại có 2 vòng lặp for để chi vậy???

    public double calculateAmount() {
        double total = 0.0;
        DishManager dm = DishManager.getManager();     
        for (String dishName : this.dishes) {
            if (dishName == null) continue;
            for (Dish dish : dm.getDishList()) {
                if (dish.getName().equalsIgnoreCase(dishName)) {
                    total += dish.getPrice();
                    break;
                }
            }
    }

    // store the calculated total into the order so getAmount() reflects it
    this.amount = total;
    return total;
    }

 
}