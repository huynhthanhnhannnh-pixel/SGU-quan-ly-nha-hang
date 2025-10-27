package controllers;

import contracts.ManagerHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import models.Dish;
import models.Ingredient;
import utils.*;

public class DishManager implements ManagerHandler {
    public static DishManager self;
    private Displayer displayer = Displayer.getDisplayer();
    // private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    private LinkedHashMap<Integer, Dish> dishList = new LinkedHashMap<>();
    private int nextId = 1; // incremental ID for dishes
    // int GO_BACK_OPTION = 0;
    
    public void saveDishesToFile() {
    BufferedWriter bw = null;
    try {
        Path p1 = Paths.get("src","cache", "Dishes(copy).txt");

    if (Files.exists(p1)) {
        // overwrite existing file instead of appending
        bw = Files.newBufferedWriter(p1, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
    else {
            throw new FileNotFoundException("Dishes(copy).txt not found in cache folders");
        }

        for (Dish ex : dishList.values()) {
            bw.write(ex.getName());
            bw.write(" ");
            for (Map.Entry<String, Integer> ing : ex.readIngredients().entrySet()) {
                bw.write(ing.getKey() + "|" + ing.getValue() + " ");
            }
            bw.newLine();
        }
        System.out.println("Đã lưu thành công vào file!");
    } catch (IOException e) {
        System.err.println("Lỗi khi ghi file: " + e.getMessage());
    } finally {
        try {
            if (bw != null) bw.close();
        } catch (IOException ignored) {}
    }
}

    
    public static DishManager getManager() {
        if (self == null) {
            self = new DishManager();
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

    public void copyFile(){
        // write the copy to the runtime cache folder (same folder used by saveDishesToFile)
        Path destination = Paths.get("src","cache", "Dishes(copy).txt");
        try {
            // Try copying from classpath resource first (works when running from jar/IDE)
            InputStream is = DishManager.class.getClassLoader().getResourceAsStream("resources/Dishes.txt");
            Files.createDirectories(destination.getParent());
            if (is != null) {
                try (InputStream in = is) {
                    Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Đã copy file từ classpath thành công!");
                    // reload in-memory cache from the copied file so runtime state matches the cache
                    
                }
            } else {
                // Fallback to filesystem path relative to working directory
                Path source = Paths.get("src", "resources", "Dishes.txt");
                if (Files.exists(source)) {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Đã copy file từ src/resources thành công!");
                    // reload in-memory cache from the copied file so runtime state matches the cache
                    
                } else {
                    System.err.println("Nguon Dishes.txt khong tim thay (checked classpath and src/resources)");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    // Private constructor to enforce singleton
    private DishManager() {
        loadDishesFromFile();
        copyFile();
    }

    
    

    // -------- ManagerHandler implementations for Dish objects --------
    @Override
    public void showGeneralInfo() {
        // Replace showGeneralInfo with an interactive menu that manages dishes
        String[] header = {"Nhap 0 de quay lai", "Quan ly mon an"};
        String[] options = {
            "Xem tat ca mon va nguyen lieu",
            "Them mon moi",
            "Xoa mon (theo ID)",
            "Tim mon (theo ten, gia hoac khoang gia)",
            "Tim mon va them nguyen lieu",
            "Tim mon va xoa nguyen lieu (theo ten nguyen lieu)",
            "Thay doi so luong nguyen lieu trong mon (theo ID)",
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
                    Dish d = Input();
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
                    addIngToDish(dishName, ing, amt);
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
    @Override
    public Dish Input() {
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
        int[] headlerLine = {50};
        System.out.println();
        displayer.printFormatLine(headlerLine);
        System.out.println("|" + displayer.centerString(" *** BAO CAO DANH SACH MON AN *** ", 52) + "|");
        displayer.printFormatLine(headlerLine);
        if (dishList.isEmpty()) {
            System.out.println("Khong co mon an nao.");
            return;
        }
        int[] dishTitleLine = {3, 25, 16};
        for (Map.Entry<Integer, Dish> entry : dishList.entrySet()) {
            Dish d = entry.getValue();
            displayer.printFormatLine(dishTitleLine);
            System.out.printf("| %-3d | %-25s | Gia: %-11.2f |\n", entry.getKey(), d.getName(), d.getPrice());
            displayer.printFormatLine(dishTitleLine);
            System.out.println("| Nguyen lieu:" + " ".repeat(39) + "|");
            for (Map.Entry<String, Integer> e : d.readIngredients().entrySet()) {
                String dishInfo = e.getKey() + ": " + e.getValue();
                System.out.printf("| %-50s |\n", dishInfo);
            }    
        }
        displayer.printFormatLine(headlerLine);
        System.out.println();
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
            saveDishesToFile();
            return;
        }
        System.out.println("Khong the them: doi tuong khong phai Dish");
    }

    // Overload: add ingredient into an existing dish (does not touch stock)
    public boolean addIngToDish(String dishName, String ingName, int amount) {
        if (dishName == null || ingName == null) return false; 

        Dish targetDish = null; // món ăn cần tìm
        for (Dish dish : dishList.values()) { // Lặp qua danh sách các món ăn để tìm món ăn cần tìm
            if (!dish.getName().equalsIgnoreCase(dishName)) continue; // Nếu không phải món cần tìm thì bỏ qua
            targetDish = dish;  
        }
        if (targetDish == null) {
            System.out.println("Khong tim thay mon: " + dishName);
            return false;
        }; // Nếu không tìm thấy thì kết thúc hàm


        HashMap<String, Integer> ingOfDish = targetDish.readIngredients(); // Lấy nguyên liệu từ món ăn
        boolean ingredientIsNotFound = true;

        // Tìm nguyên liệu trong món, nếu có thì cộng dồn nhưng không được vực quá số lượng trong kho 
        for (String ingredientName : ingOfDish.keySet()) {
            if (!ingredientName.equalsIgnoreCase(ingName)) continue;

            boolean isAmountSatisfied = SupplyManager.getManager().checkIngredients(ingName, amount);
            if (!isAmountSatisfied) {
                System.out.println("Khong the them nguyen lieu: " + ingName + " vi kho khong du so luong");
                return false;
            }

            int newAmount = ingOfDish.get(ingredientName) + amount;
            targetDish.getIngredients().put(ingredientName, newAmount);
            ingredientIsNotFound = false; // Nguyên liệu đã được tìm thấy và đã cập nhật
            saveDishesToFile();
            break;
        }

        // Nếu nguyên liệu không có trong món thì tìm trong kho 
        if (ingredientIsNotFound) {  
            for (Ingredient ingredient : SupplyManager.getManager().getKho().values()) {
                if (!ingredient.getName().equalsIgnoreCase(ingName)) { continue; }

                targetDish.addIngredient(ingName, amount);
                ingredientIsNotFound = false;
                saveDishesToFile();
                break;
            }
            //dish.addIngredient(ingName, amount);
        }

        // Nếu không tìm thấy nguyên liệu trong kho thì báo là không có trong kho 
        if (ingredientIsNotFound) {
            System.out.println("Khong the them nguyen lieu: " + ingName + " vi nguyen lieu nay khong co trong kho");
            return ingredientIsNotFound;
        }
        
        System.out.println("Da them " + ingName + " vao mon " + targetDish.getName() + " (so luong: " + amount + ")");
        return ingredientIsNotFound; // return true
    }

    @Override
    public Object remove(Object objID) {
        // Interpret objID as dish ID key
        if (!dishList.containsKey(objID)) {
            System.out.println("Khong the xoa: ID khong ton tai: " + objID);
            return null;
        }
        Dish removed = dishList.remove(objID);
        System.out.println("Da xoa mon: " + removed.getName());
        saveDishesToFile();
        return removed;
    }

    // Overload: remove ingredient from a dish
    public boolean remove(String dishName, String ingName) {
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList.values()) {
            if (dish.getName().equalsIgnoreCase(dishName)) {
                dish.removeIngredient(ingName.toLowerCase());
                System.out.println("Da xoa " + ingName + " khoi mon " + dish.getName());
                saveDishesToFile();
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
                saveDishesToFile();
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