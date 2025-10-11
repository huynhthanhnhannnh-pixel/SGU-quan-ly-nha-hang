package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import contracts.ManagerHandler;
import java.util.*;

import javax.sound.midi.SysexMessage;

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
        // Dish monAn = new Dish("Trung")
        // monAn.AddIngredient("Trung", 3)
        // monAn.AddIngredient("Dau", 1)
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\resources\\Dishes.txt"))){
            String line; 
            while((line = reader.readLine()) != null){
                line = line.trim();
                if(!line.isEmpty()) {
                    String[] ing = line.split(" ");
                    Dish dish = new Dish(ing[0]);
                    for (int i = 1 ; i < ing.length; i ++){
                        String[] parts = ing[i].split("\\|");
                        dish.addIngredient(parts[0],Integer.parseInt(parts[1]));
                    }
                    dishList.add(dish);
                }
            }
            System.out.println("Loading successful");
        }
        catch (IOException e) {
            System.err.println("Error loading dishes from file: " + e.getMessage());
        }
    }

    // Load các object nguyên liệu(Ingredient)
    private void loadIngredientsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\controlable\\Ingredients(copy).txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] ing = line.split("\\|");
                    Ingredient nguyenlieu = new Ingredient(ing[0]);
                    nguyenlieu.increaseQuantity(Integer.parseInt(ing[1]));
                    nguyenlieu.setCost(Double.parseDouble(ing[2]));
                    nguyenlieu.setDate(ing[3]);
                    nguyenlieu.setDateofEntry(ing[4]);
                    nguyenlieu.setStandard(true);
                    ingredients.add(nguyenlieu);
                }
            }
            System.out.println("Loading successful");
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

    public List<Ingredient> getKho() {
        return ingredients;
    }


    // kiểm tra xem có đủ 1 nguyên liệu không
    public Boolean checkIngredients(String name , int amount) {
        // Giả sử bạn đã có danh sách các nguyên liệu trong kho
    // List<Ingredient> ingredients;

    for (Ingredient ing : ingredients) {
        if (name.equalsIgnoreCase(ing.getName())) { // tìm đúng nguyên liệu
            if (amount > ing.getQuantity()) {
                System.out.println("Khong du nguyen lieu " + name);
                return false;
            } else {
                System.out.println("Nguyen lieu du"+ name );
                return true; // có đủ nguyên liệu
            }
        }
    }
    // Nếu duyệt hết mà không tìm thấy nguyên liệu
    System.out.println("Nguyen lieu khong co trong kho " + name);
    return false;
    }
    
    //Ham kiem tra nguyen lieu trong kho con hsd khong, neu khong con thi se danh dau thuoc tinh nguyen lieu theo boolean là false
    // public boolean checkdateIngredients(Ingredient ing) {
    // LocalDate today = LocalDate.now();
    // System.out.println("Ngay hom nay la " + today);
    // if (today.isAfter(ing.getDate())){
    //     System.out.println(ing.getName() + " da het han su dung tu ngay " + ing.getDate());
    //     return false;
    // }
    // System.out.println(ing.getName() + " con han su dung ");
    // return true;
    // }

    //Ham kiem tra kho co nguyen lieu nao het han su dung khong neu co thi delete
    public void deleteIngredientsfromWarehouse(){
       LocalDate today = LocalDate.now();
       System.out.println("Ngay hom nay la " + today);
       ingredients.removeIf(ing -> ing.getDate().isBefore(today));
       saveToFile("src\\controlable\\Ingredients(copy).txt",ingredients);
    }
    // ghi lai cai list vao file txt 
    public static void saveToFile(String fileName, List<Ingredient> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Ingredient ing : list) {
                bw.write(ing.toString());
                bw.newLine();
            }
            System.out.println("Da cap nhat file" + fileName);
        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }
    // // Bỏ nguyên liệu vào kho
    // public void addIngredient(String name, int amount) {
    //     // lấy object ingredient trong kho

    //     // tăng số lượng
    // } lỡ viết loadingIngredients đọc xong lưu vào mảng Ingredients (tức kho nguyên liệu) nên viết thêm cái này bị thừa

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
    //public String[] getMenu() {
    //     // Sử dụng vòng lặp để lấy hết tên tất cả các món trong menu rồi lưu vào biến temp
    //     // String[] temp = {"Mon A", "Món B"};
    //     // return temp;
        
    // }
    // //get menu giờ sửa thành randomMenu
    // public List<String> randomMenuToday(int amount){
    //     Random rand = new Random();
    //     List <String> menu = new ArrayList<>();
    //     List <Dish> temp = new ArrayList<>(dishList);
    //     Collections.shuffle(temp, rand);
    //     for (int i =0;i< amount;i++){
    //         menu.add(temp.get(i).getName());
    //     }
    //     return menu;
    // }
}
