package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import contracts.ManagerHandler;
import java.util.*;
import models.*;
import utils.*;


public class SupplyManager implements ManagerHandler {
    private static SupplyManager self;
    private Displayer displayer = Displayer.getDisplayer();
    //private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    private List<Ingredient> ingredients; // Kho nguyên liệu
    private List<Dish> dishList; // Menu của chúng ta, không hẳng là menu vì nó đang chứa cả data của món ăn, menu chính sẽ nằm trong object Table

    @Override
    public void showGeneralInfo() {
        String[] message = {
            "Day la trinh quan ly nguon cung thuc pham 100% sieu sach",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. Kiem tra nguon cung thuc pham, co so luong nhung ko co chat luong ;)",
            "2. add/remove them nguyen lieu nhanh gon va sieu muot",
            "Va dat biet la cac mon an cua chung ta duoc lam boi sieu dau bep ratatouille"
        };
        displayer.displayMessage(message);
    }
    @Override
    public void createReport() {
        System.out.println("Danh sach nguyen lieu trong kho:");
        for (Ingredient ing : ingredients) {
            System.out.println("Name: " + ing.getName() + ", Quantity: " + ing.getQuantity());
        }
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // Load các món ăn(Dishes) NOTE: load nguyên liệu trước rồi load món ăn
    private void loadDishesFromFile() {

    }

    // Load các object nguyên liệu(Ingredient)
    private void loadIngredientsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\resources\\Ingredients.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    ingredients.add(new Ingredient(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading ingredients from file: " + e.getMessage());
        }
    }

    // Private constructor to enforce singleton
    private SupplyManager() {
        ingredients = new ArrayList<>();
        loadIngredientsFromFile(); // Load nguyên liệu
        loadDishesFromFile(); // Load món ăn
    }

    // Public method to get the single self
    public static SupplyManager getManager() {
        if (self == null) {
            self = new SupplyManager();
        }
        return self;
    }
    
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Your codes go here

    // kiểm tra xem có đủ nguyên liệu không
    public Boolean checkIngredients(String dishName) {
        // lấy object Dish từ danh sách menu 

        // Dùng Dish.readIngredient để đọc các nguyên liệu cần lấy

        // Dùng vòng lặp để kiểm tra có đủ nguyên liệu để nấu không

        return false;
    }


    // Bỏ nguyên liệu vào kho
    public void addIngredient(String name, int amount) {
        // lấy object ingredient trong kho

        // tăng số lượng
    }

    // Lấy nguyên liệu ra khỏi kho
    public Ingredient getIngredient(String name, int amount) {
        for (Ingredient ing : ingredients) {
            if (ing.getName().equalsIgnoreCase(name)) {
                int retrievedAmount = ing.decreaseQuantity(amount);
                if (retrievedAmount > 0) {
                    Ingredient result = new Ingredient(name);
                    result.increaseQuantity(retrievedAmount);
                    return result;
                }
                break; // Found but not enough quantity
            }
        }
        return null; // Not found or insufficient quantity
    }

  
    // lấy menu dưới dạng danh sách tên các món ăn
    public String[] getMenu() {
        // Sử dụng vòng lặp để lấy hết tên tất cả các món trong menu rồi lưu vào biến temp
        String[] temp = {"Mon A", "Món B"};

        return temp;
    }
}
