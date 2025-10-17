package controllers;

import contracts.ManagerHandler;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.*;
import utils.*;

public class SupplyManager implements ManagerHandler {
    private static SupplyManager self;
    private Displayer displayer = Displayer.getDisplayer();

    // Giờ kho là HashMap<Integer, Ingredient>
    private HashMap<Integer, Ingredient> ingredients = new HashMap<>();
    private List<Dish> dishList = new ArrayList<>();

    @Override
    public void showGeneralInfo() {
        displayer.clearScreen();
        String[] message = {
            "Day la trinh quan ly nguon cung thuc pham 100% sieu sach",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. Xem cac nguyen lieu trong kho",
            "2. Them nguyen lieu vao kho ",
            "3. Xoa nguyen lieu trong kho ",
            "4. Tim kiem nguyen lieu trong kho ",
            "5. Xuat cac nguyen lieu khong du so luong lam 1 mon trong kho ",
            "Va dat biet la cac mon an cua chung ta duoc lam boi sieu dau bep ratatouille"
        };
        displayer.displayMessage(message);
    }
    
    @Override
    public void createReport() {
        System.out.println("Danh sach nguyen lieu trong kho:");
        for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getValue();
            System.out.println(
                "ID: " + entry.getKey() + 
                ", Name: " + ing.getName() + 
                ", Quantity: " + ing.getQuantity() +
                ", HSD: " + ing.getDate() +
                ", Ngay Nhap hang: " + ing.getNgayNhapHang()
            );
        }
    }

    // === Load dữ liệu món ăn ===
    private void loadDishesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\resources\\Dishes.txt"))) {
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
                    dishList.add(dish);
                }
            }
            System.out.println("Loading dishes successful");
        } catch (IOException e) {
            System.err.println("Error loading dishes from file: " + e.getMessage());
        }
    }

    // === Load nguyên liệu ===
    private void loadIngredientsFromFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\controlable\\Ingredients.txt"))) {
            String line;
            int id = 1; // ID tự tăng
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] ing = line.split("\\|");
                    if (ing.length < 5) {
                        System.out.println("Dong du lieu k hop le");
                        continue;
                    }
                    Ingredient nguyenlieu = new Ingredient(ing[0].trim());
                    nguyenlieu.increaseQuantity(Integer.parseInt(ing[1].trim()));
                    nguyenlieu.setCost(Double.parseDouble(ing[2].trim()));
                    nguyenlieu.setHSD(LocalDate.parse(ing[3].trim(), formatter));
                    nguyenlieu.setNgayNhap(LocalDate.parse(ing[4].trim(), formatter));
                    ingredients.put(id++, nguyenlieu);
                }
            }
            System.out.println("Loading ingredients successful");
        } catch (IOException e) {
            System.err.println("Error loading ingredients: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Loi khi doc du lieu: " + e.getMessage());
        }
    }

    // Private constructor to enforce singleton
    private SupplyManager() {
        loadIngredientsFromFile();
        loadDishesFromFile();
    }
    // Public method to get the single self
    public static SupplyManager getManager() {
        if (self == null) self = new SupplyManager();
        return self;
    }

    // Trả về kho
    public HashMap<Integer, Ingredient> getKho() {
        return ingredients;
    }
    
    //Trả về danh sách món ăn 
    public List<Dish> getDishList() {
        return dishList;
    }

    // Kiểm tra đủ nguyên liệu chưa
    public Boolean checkIngredients(String name, int amount) {
        int total = 0;
        for (Ingredient ing : ingredients.values()) {
            if (name.equalsIgnoreCase(ing.getName())) {
                total+= ing.getQuantity();
            }
        }
        if (total >= amount) {
            System.out.println("Nguyen lieu " + name + " co du ");
            return true;
        } else {
            System.out.println("Nguyen lieu khong co trong kho: " + name);
            return false;
        }
    }
    
    //hàm xóa 
    @Override
    public void remove(int id) {
        if (!ingredients.containsKey(id)) {
            System.out.println("Khong the xoa: id " + id + " khong ton tai.");
            return;
        }
        Ingredient removed = ingredients.remove(id);
        System.out.println("Da xoa nguyen lieu: " + removed.getName() + " (so luong: " + removed.getQuantity() + ")");
    }

    //hàm thêm 
    @Override
    public void add(Object obj) {
        if (obj == null || !(obj instanceof Ingredient)) {
            System.out.println("Khong the them: obj null hoac khong phai Ingredient");
            return;
        }

        Ingredient ing = (Ingredient) obj;
        String name = ing.getName();
        int qtyToAdd = ing.getQuantity();

        // Tìm nguyên liệu trùng tên
        for (Ingredient existing : ingredients.values()) {
            if (existing.getName().equalsIgnoreCase(name)) {
                existing.increaseQuantity(qtyToAdd);
                System.out.println("Da cong " + qtyToAdd + " vao nguyen lieu ton tai: " + existing.getName());
                return;
            }
        }

        // Nếu chưa có thì thêm mới
        int newId = ingredients.isEmpty() ? 1 : Collections.max(ingredients.keySet()) + 1;
        ingredients.put(newId, ing);
        System.out.println("Da them nguyen lieu moi (ID=" + newId + "): " + ing.getName());
        
    }

    // Hàm tìm kiếm nguyên lí 
    @Override
    public Ingredient search(int objID){
        return ingredients.get(objID);
    }

    // Xóa nguyên liệu hết hạn và số lượng = 0 và trả về giá hàng bị hủy 
    public double deleteExpiredandLowQuantityIngredients() {
        LocalDate today = LocalDate.now();
        double total = 0;
        Iterator<Map.Entry<Integer, Ingredient>> iterator = ingredients.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Ingredient> entry = iterator.next();
            Ingredient ing = entry.getValue();
            if (ing.getDate().isBefore(today) || ing.getQuantity() == 0 ) {
                total += ing.getCost();
                iterator.remove();
                System.out.println("Da xoa nguyen lieu het han: " + ing.getName());
            }
        }
        return total;
    }

    
    // Lấy nguyên liệu ra khỏi kho
    public Ingredient getIngredient(String name, int amount) {
        for (Ingredient ing : ingredients.values()) {
            if (ing.getName().equalsIgnoreCase(name)) {
                int retrievedAmount = ing.decreaseQuantity(amount);
                if (retrievedAmount > 0) {
                    Ingredient result = new Ingredient(name);
                    result.increaseQuantity(retrievedAmount);
                    return result;
                }
                break;
            }
        }
        return null;
    }

    // Tìm nguyên liệu có hạn sử dụng nhỏ nhất
    public Ingredient TimNguyenLieuNhoNhat(String name) {
        Ingredient min = null;
        for (Ingredient ing : ingredients.values()) {
            if (ing.getName().equalsIgnoreCase(name)) {
                if (min == null || ing.getDate().isBefore(min.getDate())) {
                    min = ing;
                }
            }
        }
        return min;
    }

    // Hàm kiểm tra xem các nguyên liệu có đủ làm 1 món nào k 
    public void checkWarehouse() {
    // B1. Tạo map phụ để tra cứu nguyên liệu theo tên (O(n))
        Map<String, Ingredient> ingredientByName = new HashMap<>();
        for (Ingredient ing : ingredients.values()) {
            ingredientByName.put(ing.getName().toLowerCase(), ing);
        }

        // B2. Duyệt qua từng món ăn (O(m))
        boolean allEnough = true;

        for (Dish dish : dishList) {
            for (Map.Entry<String, Integer> need : dish.readIngredients().entrySet()) {
                String ingName = need.getKey().toLowerCase();
                int requiredAmount = need.getValue();

                Ingredient inStock = ingredientByName.get(ingName);

                // B3. Kiểm tra tồn kho
                if (inStock == null) {
                    System.out.println("Thieu nguyen lieu " + need.getKey() +
                                    " de lam mon " + dish.getName());
                    allEnough = false;
                } else if (inStock.getQuantity() < requiredAmount) {
                    System.out.println("Khong du nguyen lieu " + need.getKey() +
                                    " de lam mon " + dish.getName() +
                                    " (can " + requiredAmount + ", co " +
                                    inStock.getQuantity() + ")");
                    allEnough = false;
                }
            }
        }

        // B4. Nếu đủ tất cả
        if (allEnough) {
            System.out.println("✅ Du nguyen lieu de lam tat ca mon !");
        }
}

    
}

