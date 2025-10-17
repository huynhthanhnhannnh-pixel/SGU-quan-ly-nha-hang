package models;

import java.util.*;
import enums.*;

public class Order {
    private Table table;
    private double amount;
    List<String> dishes = new ArrayList<String>();
    List<String> excludedDishes = new ArrayList<String>();
    private int numOfUnsatisfiedRequest = 0;

    public Order(Table table) {
        this.table = table;
    }

    public Table getTable() { return table; }
    public  int getNumOfUnsatisfiedRequest() { return numOfUnsatisfiedRequest; }
    public double getAmount() { return amount; }    

    public void writeOrder(String dishName) {
        dishes.add(dishName);
    }

    public void addExcludedDish(String dishName) {
        excludedDishes.add(dishName);
    }

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
        return OrderState.COMPLETED;
    }


}
