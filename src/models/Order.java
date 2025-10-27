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

    public void setNumOfUnsatisfiedRequest(int num) { this.numOfUnsatisfiedRequest = num; } 

    public void writeOrder(String dishName) {   dishes.add(dishName); }

    // chef thêm món không thể nấu được vào danh sách loại trừ
    public void addExcludedDish(String dishName) { excludedDishes.add(dishName); }

    // chef update lại order để thêm món không thể nấu được
    // public void updateOrder() {
    //     for (String excludedDish : excludedDishes) {
    //         int length = dishes.size();
    //         for (int i = 0; i < length; i++) {
    //             if (excludedDish.equals(dishes.get(i))) {
    //                 dishes.remove(i);
    //                 numOfUnsatisfiedRequest++;
    //                 length--;
    //             }
    //         }
    //     }
    // }

    public void updateOrder() {
        int countRemoved = 0;
        Iterator<String> it = dishes.iterator();
        while (it.hasNext()) {
            String dish = it.next();
            if (excludedDishes.contains(dish)) {
                it.remove();
                countRemoved++;
            }
        }
        numOfUnsatisfiedRequest += countRemoved;
        //System.out.println("Dishes after update: " + dishes);
        //System.out.println("Excluded: " + excludedDishes);
        //System.out.println("numOfUnsatisfiedRequest: " + numOfUnsatisfiedRequest);

    }

    public OrderState getState() {
        if (dishes.size() == 0) {
            return OrderState.NEW;
        } else if (numOfUnsatisfiedRequest > 0) {
            return OrderState.UNFINISHED;
        } else if (dishes.size() > 0 && numOfUnsatisfiedRequest == 0) {
            return OrderState.COMPLETED;
        }
        return OrderState.COMPLETED;
    }

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

    public boolean isExclusiveDish(String name) {
        for (String dishName : excludedDishes) {
            if (dishName.equals(name)) {
                return true;
            }
        }
        return false;
    } 
 
}