package controllers;

import contracts.ManagerHandler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import models.Dish;
import utils.Displayer;
import utils.UserInputHandler;

public class dishManager implements ManagerHandler {
    public static dishManager self;
    private Displayer displayer = Displayer.getDisplayer();
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    private LinkedHashMap<Integer, Dish> dishList = new LinkedHashMap<>();
    private int nextId = 1; // incremental ID for dishes
    int GO_BACK_OPTION = 0;
    
    public static dishManager getManager() {
        if (self == null) {
            self = new dishManager();
        } 
        return self;
    }
    //Trả về danh sách món ăn (view)
    public List<Dish> getDishList() {
        return new ArrayList<>(dishList.values());
    }
    // === Load dữ liệu món ăn === 
    private void loadDishesFromFile() {
        BufferedReader reader = null;
        try {
            InputStream is = SupplyManager.class.getClassLoader().getResourceAsStream("resources/Dishes.txt");
            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                Path p1 = Paths.get("resources", "Dishes.txt");
                Path p2 = Paths.get("src", "resources", "Dishes.txt");
                if (Files.exists(p1)) reader = Files.newBufferedReader(p1, StandardCharsets.UTF_8);
                else if (Files.exists(p2)) reader = Files.newBufferedReader(p2, StandardCharsets.UTF_8);
                else throw new FileNotFoundException("Dishes.txt not found in classpath or resources folders");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] ing = line.split(" ");
                    Dish dish = new Dish(ing[0]);
                    for (int i = 1; i < ing.length; i++) {
                        String[] parts = ing[i].split("\\|");
                        if (parts.length == 2) {
                            dish.addIngredient(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                        } else {
                            System.err.println("Sai dinh dang dong trong file Dishes.txt");
                            return;
                        }
                    }
                    // assign an incremental id and store
                    dishList.put(nextId++, dish);
                }
            }
            System.out.println("Loading dishes successful");
        } catch (IOException e) {
            System.err.println("Error loading dishes: " + e.getMessage());
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
    }
    // Private constructor to enforce singleton
    private dishManager() {
        loadDishesFromFile();
    }
    

    // -------- ManagerHandler implementations for Dish objects --------
    @Override
    public void showGeneralInfo() {
        // Replace showGeneralInfo with an interactive menu that manages dishes
        String[] header = {"Nhap 0 de quay lai", "Quan ly mon an"};
        String[] options = {
            "1. Xem tat ca mon va nguyen lieu",
            "2. Them mon moi",
            "3. Xoa mon (theo ID)",
            "4. Tim mon (theo ten, gia hoac khoang gia)",
            "5. Tim mon va them nguyen lieu",
            "6. Tim mon va xoa nguyen lieu (theo ten nguyen lieu)",
            "7. Thay doi so luong nguyen lieu trong mon (theo ID)",
            "0. Quay lai"
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) { inputHandler.resetOption(); break; }

            switch (choice) {
                case 1:
                    createReport();
                    break;
                // === LỖI LOGIC KHÔNG HỢP LÝ CÁCH SẮP XẾP===    
                case 2: {
                    // Build Dish via helper then add via ManagerHandler.add(Object)
                    Dish d = DishInput();
                    if (d != null) add(d);
                    break;
                }
                case 3: {
                    System.out.print("Nhap ID mon can xoa: ");
                    try {
                        int id = Integer.parseInt(inputHandler.getScanner().nextLine().trim());
                        remove(id);
                    } catch (Exception e) { System.out.println("ID khong hop le"); }
                    break;
                }
                case 4: {
                    System.out.print("Nhap ten mon, mot gia, hoac khoang gia (min-max). Nhap 0 de huy: ");
                    String q = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    if (q.equals("0")) break;
                    // If contains '-', treat as range; else try parse number; otherwise treat as name
                    if (q.contains("-")) {
                        search(q);
                    } else {
                        try {
                            double price = Double.parseDouble(q);
                            search(price);
                        } catch (Exception ex) {
                            search(q);
                        }
                    }
                    break;
                }
                case 5: {
                    // Find dish, then add ingredient
                    System.out.print("Nhap ID hoac ten mon can tim de them nguyen lieu (nhap 0 de huy): ");
                    String q = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    if (q.equals("0")) break;
                    String dishName = resolveDishName(q);
                    if (dishName == null) break;
                    System.out.print("Nhap nguyen lieu va so luong (Ten|SoLuong): ");
                    String line = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    String[] parts = line.split("\\|");
                    if (parts.length != 2) { System.out.println("Dinh dang khong hop le"); break; }
                    String ing = parts[0].trim(); 
                    int amt = 0; 
                    try { amt = Integer.parseInt(parts[1].trim()); }
                     catch (Exception e) { 
                        System.out.println("So luong khong hop le"); 
                        break; 
                     }
                    add(dishName, ing, amt);
                    break;
                }
                case 6: {
                    // Find dish, then remove ingredient
                    System.out.print("Nhap ID hoac ten mon can tim de xoa nguyen lieu (nhap 0 de huy): ");
                    String q2 = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    if (q2.equals("0")) break;
                    String dishName2 = resolveDishName(q2);
                    if (dishName2 == null) break;
                    System.out.print("Nhap ten nguyen lieu can xoa: ");
                    String ingRem = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    if (ingRem.isEmpty()) { System.out.println("Ten nguyen lieu khong hop le"); break; }
                    remove(dishName2, ingRem);
                    break;
                }
                case 7: {
                    // Find dish, then change ingredient amount
                    System.out.print("Nhap ID hoac ten mon can tim de thay doi so luong (nhap 0 de huy): ");
                    String q3 = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    if (q3.equals("0")) break;
                    String dishName3 = resolveDishName(q3);
                    if (dishName3 == null) break;
                    System.out.print("Nhap ten nguyen lieu va so luong moi (Ten|SoLuong): ");
                    String line2 = inputHandler.getScanner().nextLine().trim().replaceAll("\\s+", "");
                    String[] parts2 = line2.split("\\|");
                    if (parts2.length != 2) { System.out.println("Dinh dang khong hop le"); break; }
                    String ingName = parts2[0].trim(); int newAmt = 0; try { newAmt = Integer.parseInt(parts2[1].trim()); } catch (Exception e) { System.out.println("So luong khong hop le"); break; }
                    changeIngredientAmountInDish(dishName3, ingName, newAmt);
                    break;
                }
                default:
                    System.out.println("Lua chon khong hop le");
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
        }
    }

    // Helper to read a Dish from console (name + ingredient list). Returns null if cancelled.
    public static Dish DishInput() {
        UserInputHandler input = UserInputHandler.getUserInputHandler();
        System.out.print("Nhap ten mon moi (nhap 0 de huy): ");
        String name = input.getScanner().nextLine().trim();
        if (name.equals("0") || name.isEmpty()) return null;
        Dish dish = new Dish(name);
        System.out.println("Nhap nguyen lieu cho mon (ten|soLuong). Nhap dong rong hoac 0 de ket thuc.");
        while (true) {
            System.out.print("Nhap nguyen lieu va so luong: ");
            String line = input.getScanner().nextLine().trim();
            if (line.equals("0") || line.isEmpty()) break;
            String[] parts = line.split("\\|");
            if (parts.length != 2) { System.out.println("Dinh dang phai la: Ten|SoLuong"); continue; }
            String ing = parts[0].trim();
            int amt = 0;
            try { amt = Integer.parseInt(parts[1].trim()); } catch (Exception e) { System.out.println("So luong khong hop le"); continue; }
            dish.addIngredient(ing, amt);
        }
        return dish;
    }

    @Override
    public void createReport() {
        System.out.println("--- Bao cao danh sach mon an ---");
        if (dishList.isEmpty()) {
            System.out.println("Khong co mon an nao.");
            return;
        }
        for (Map.Entry<Integer, Dish> entry : dishList.entrySet()) {
            Dish d = entry.getValue();
            displayer.singleSeperate();
            System.out.println("ID: " + entry.getKey());
            System.out.println("Mon: " + d.getName());
            System.out.println("Gia: " + String.format("%.2f", d.getPrice()));
            System.out.println("Nguyen lieu:");
            for (Map.Entry<String, Integer> e : d.readIngredients().entrySet()) {
                System.out.println(" - " + e.getKey() + ": " + e.getValue());
            }
        }
    }

    @Override
    public void add(Object obj) {
        if (obj == null) {
            System.out.println("Khong the them: doi tuong null");
            return;
        }
        if (obj instanceof Dish) {
            Dish d = (Dish) obj;
            // prevent duplicate names (case-insensitive)
            for (Dish ex : dishList.values()) {
                if (ex.getName().equalsIgnoreCase(d.getName())) {
                    System.out.println("Da ton tai mon co ten: " + d.getName());
                    return;
                }
            }
            dishList.put(nextId++, d);
            System.out.println("Da them mon: " + d.getName());
            return;
        }
        System.out.println("Khong the them: doi tuong khong phai Dish");
    }

    // Overload: add ingredient into an existing dish (does not touch stock)
    public boolean add(String dishName, String ingName, int amount) {
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList.values()) {
            if (dish.getName().equalsIgnoreCase(dishName)) {
                HashMap<String, Integer> ingOfDish = dish.readIngredients();
                boolean found = false;
                for (String key : ingOfDish.keySet()) {
                    if (key.equalsIgnoreCase(ingName)) {
                        int newAmount = ingOfDish.get(key) + amount;
                        dish.getIngredients().put(key, newAmount);
                        found = true;
                        
                        break;  
                    }
                }
                if (!found) {
                    dish.addIngredient(ingName, amount);
                }
                System.out.println("Da them " + ingName + " vao mon " + dish.getName() + " (so luong: " + amount + ")");
                return true;     
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
    }

    @Override
    public Object remove(int objID) {
        // Interpret objID as dish ID key
        if (!dishList.containsKey(objID)) {
            System.out.println("Khong the xoa: ID khong ton tai: " + objID);
            return null;
        }
        Dish removed = dishList.remove(objID);
        System.out.println("Da xoa mon: " + removed.getName());
        return removed;
    }

    // Overload: remove ingredient from a dish
    public boolean remove(String dishName, String ingName) {
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList.values()) {
            if (dish.getName().equalsIgnoreCase(dishName)) {
                dish.removeIngredient(ingName.toLowerCase());
                System.out.println("Da xoa " + ingName + " khoi mon " + dish.getName());
                return true;
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
    }

    @Override
    public Object search(Object obj) {
        if (obj == null) return null;
        // If searching by name (String) or by price-range string like "min-max"
        if (obj instanceof String) {
            String s = ((String) obj).trim();
            // range format
            if (s.contains("-")) {
                String[] parts = s.split("-");
                try {
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    if (min > max) { double t=min; min=max; max=t; }
                    List<Dish> matches = new ArrayList<>();
                    for (Dish dish : dishList.values()) {
                        double p = dish.getPrice();
                        if (p >= min && p <= max) {
                            matches.add(dish);
                            System.out.println("Mon: " + dish.getName() + " - Gia: " + String.format("%.2f", p));
                            System.out.println("Nguyen lieu:");
                            for (Map.Entry<String, Integer> e : dish.readIngredients().entrySet()) {
                                System.out.println(" - " + e.getKey() + ": " + e.getValue());
                            }
                            displayer.singleSeperate();
                        }
                    }
                    if (matches.isEmpty()) System.out.println("Khong co mon nao trong khoang gia: " + s);
                    return matches;
                } catch (Exception ex) {
                    System.out.println("Dinh dang khoang gia khong hop le. Su dung: min-max");
                    return null;
                }
            }

            // otherwise treat as exact name search
            String name = s;
            for (Dish dish : dishList.values()) {
                if (dish.getName().equalsIgnoreCase(name)) {
                    System.out.println("Mon: " + dish.getName());
                    System.out.println("Gia: " + String.format("%.2f", dish.getPrice()));
                    System.out.println("Nguyen lieu:");
                    for (Map.Entry<String, Integer> e : dish.readIngredients().entrySet()) {
                        System.out.println(" - " + e.getKey() + ": " + e.getValue());
                    }
                    return dish;
                }
            }
            System.out.println("Khong tim thay mon: " + name);
            return null;
        }

        // If searching by price (Number), return list of dishes with (approx) same price
        if (obj instanceof Number) {
            // interpret as a single price -> search around tolerance
            double target = ((Number) obj).doubleValue();
            double eps = 0.01; // small tolerance
            List<Dish> matches = new ArrayList<>();
            for (Dish dish : dishList.values()) {
                if (Math.abs(dish.getPrice() - target) <= eps) {
                    matches.add(dish);
                    System.out.println("Mon: " + dish.getName() + " - Gia: " + String.format("%.2f", dish.getPrice()));
                    System.out.println("Nguyen lieu:");
                    for (Map.Entry<String, Integer> e : dish.readIngredients().entrySet()) {
                        System.out.println(" - " + e.getKey() + ": " + e.getValue());
                    }
                    displayer.singleSeperate();
                }
            }
            if (matches.isEmpty()) System.out.println("Khong co mon nao co gia xung voi: " + target);
            return matches;
        }

        // (range handled above in String branch)

        System.out.println("Kieu tim kiem khong ho tro.");
        return null;
    }

    // Overload: change ingredient amount in a dish
    public boolean changeIngredientAmountInDish(String dishName, String ingName, int amount) {
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList.values()) {
            if (dish.getName().equalsIgnoreCase(dishName)) {
                dish.changeIngredientAmount(ingName.toLowerCase(), amount);
                System.out.println("Da cap nhat so luong " + ingName + " trong mon " + dish.getName() + " = " + amount);
                return true;
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
    }

    // Helper: resolve an input which can be an ID string or a name to the canonical dish name
    private String resolveDishName(String idOrName) {
        if (idOrName == null) return null;
        String s = idOrName.trim();
        // try parse as ID
        try {
            int id = Integer.parseInt(s);
            Dish d = dishList.get(id);
            if (d == null) { System.out.println("Khong tim thay ID: " + id); return null; }
            return d.getName();
        } catch (NumberFormatException nfe) {
            // treat as name
            for (Dish dish : dishList.values()) {
                if (dish.getName().equalsIgnoreCase(s)) return dish.getName();
            }
            System.out.println("Khong tim thay mon: " + s);
            return null;
        }
    }
}