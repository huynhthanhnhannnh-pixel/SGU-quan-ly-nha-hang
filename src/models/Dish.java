package models;

import java.util.*;
import controllers.*;

public class Dish {
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
        HashMap<Integer, Ingredient>  kho = SupplyManager.getManager().getKho();
        for (Ingredient ingredient : kho.values()){
            String ten = ingredient.getName();
            String key = ten != null ? ten.toLowerCase() : "";
            if(nguyenLieu.containsKey(key)){
                Integer soluongcanObj = nguyenLieu.get(key);
                int soluongcan = soluongcanObj != null ? soluongcanObj.intValue() : 0;
                double giaNguyenlieu = ingredient.getCost();
                total += soluongcan*giaNguyenlieu;
            }   
        }
        return total*3;
    }
}

