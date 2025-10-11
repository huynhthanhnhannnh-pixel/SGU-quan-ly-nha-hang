package models;

import java.util.*;
import controllers.*;
import models.*;

public class Dish {
    private List<Ingredient> kho = SupplyManager.getManager().getKho();
    private final String name;
    private HashMap<String, Integer> nguyenLieu = new HashMap<String, Integer>(); // ten va so kuong nguyen lieu can thiet cho mon an 
    public Dish(String name){
        this.name = name;
    }

    public String getName() { return name; }
    
    public HashMap<String, Integer> readIngredients(){
        return new HashMap<>(nguyenLieu);
    }
    
    public void addIngredient (String name , int amount){
        nguyenLieu.put(name.toLowerCase(),amount);
    }
    public void removeIngredient(String name) {
        nguyenLieu.remove(name);
    }
    public void changeIngredientAmount(String name, int amount){
        if (nguyenLieu.containsKey(name)) {
            nguyenLieu.put(name, amount); // cập nhật số lượng mới
        } else {
            System.out.println("Nguyên liệu '" + name + "' không tồn tại trong món ăn.");
        }
    }
    public double getPrice(){
        double total=0;
        for (Ingredient ingredient : kho){
            String ten = ingredient.getName();
            if(nguyenLieu.containsKey(ten.toLowerCase())){
                int soluongcan = nguyenLieu.get(ten);
                double giaNguyenlieu = ingredient.getCost();
                total += soluongcan*giaNguyenlieu;
            }   
        }
        return total*3;
    }
}

