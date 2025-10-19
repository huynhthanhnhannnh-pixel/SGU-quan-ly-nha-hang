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
    private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    // Giờ kho là HashMap<Integer, Ingredient>
    private HashMap<Integer, Ingredient> ingredients = new LinkedHashMap<>();
    private List<Dish> dishList = new ArrayList<>();
    int GO_BACK_OPTION = 0;
    private HashMap <String, Ingredient> ingredientsData = new LinkedHashMap<>();

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
            "6. Tao bao cao ve cac nguyen lieu hien tai dang co",
            "Va dat biet la cac mon an cua chung ta duoc lam boi sieu dau bep ratatouille"
        };
        displayer.displayMessage(message);
    }
    
    @Override
    public void createReport() {
        System.out.println("Danh sach nguyen lieu trong kho:");
        for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getValue();
            displayer.singleSeperate();
            System.out.println(
                "ID: " + entry.getKey() + 
                "\nName: " + ing.getName() + 
                "\nHSD: " + ing.getDate() +
                "\nNgay Nhap hang: " + ing.getNgayNhapHang() +
                "\nQuantity: " + ing.getQuantity() +
                "\nCost: " + ing.getCost()
            );
        }
    }

    public void XuatNguyenLieu(){
        for (Map.Entry<String, Ingredient> entry : ingredientsData.entrySet()){
            Ingredient ing = entry.getValue();
            displayer.singleSeperate();
            System.out.println("Name: " + ing.getName() +
                                "\nQuantity: " + ing.getQuantity() +
                                "\nCost: " + ing.getCost() 
                );
        }
        
    }

    // === Load dữ liệu món ăn ===
    private void loadDishesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources\\Dishes.txt"))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader("resources\\Ingredients.txt"))) {
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
                    String name = ing[0].trim();
                    int qty = Integer.parseInt(ing[1].trim());
                    double cost = Double.parseDouble(ing[2].trim());
                    LocalDate hsd = LocalDate.parse(ing[3].trim(), formatter);
                    LocalDate ngayNhap = LocalDate.parse(ing[4].trim(), formatter);

                    Ingredient dataNguyenlieu = new Ingredient(name);
                    dataNguyenlieu.increaseQuantity(qty);
                    dataNguyenlieu.setCost(cost);
                    ingredientsData.put(name.toLowerCase(), dataNguyenlieu);

                    Ingredient nguyenlieu = new Ingredient(name);
                    nguyenlieu.increaseQuantity(qty);
                    nguyenlieu.setCost(cost);
                    nguyenlieu.setHSD(hsd);
                    nguyenlieu.setNgayNhap(ngayNhap);
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
        for (Map.Entry<String,Ingredient> entry : ingredientsData.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                Ingredient ing = entry.getValue();
                if(ing.getQuantity()>= amount) return true;
            }
        }
        return false;
    }
    
    //hàm xóa 
    @Override
    public Ingredient remove(int id) {
        if (!ingredients.containsKey(id)) {
            System.out.println("Khong the xoa: id " + id + " khong ton tai.");
            return null;
        }
        Ingredient removed = ingredients.remove(id);
        Ingredient agg = ingredientsData.get(removed.getName().toLowerCase());
        if (agg != null){
            agg.decreaseQuantity(removed.getQuantity());
            if (agg.getQuantity() <= 0 ) agg.setQuantity(0);
        }

        System.out.println("Da xoa nguyen lieu: " + removed.getName() + " (so luong: " + removed.getQuantity() + ")");
        return removed;
    }

    //hàm thêm quantity vào nguyên liệu có sẵn
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
        for (Map.Entry<String, Ingredient> entry : ingredientsData.entrySet()) {
            Ingredient existing = entry.getValue();
            if (existing.getName().equalsIgnoreCase(name)) {
                existing.increaseQuantity(qtyToAdd);
                System.out.println("Da cong " + qtyToAdd + " vao nguyen lieu ton tai: " + existing.getName());
                int newId = ingredients.isEmpty() ? 1 : Collections.max(ingredients.keySet())+1;
                ingredients.put(newId, ing);
                System.out.println("Da them 1 lo hang vao kho hang");
                return;
            }
        }

        // Nếu chưa có thì thêm mới
        ingredientsData.put(name.toLowerCase(),ing);
        System.out.println("Da them nguyen lieu moi" + ing.getName());
        int newId = ingredients.isEmpty() ? 1 : Collections.max(ingredients.keySet()) + 1;
        ingredients.put(newId, ing);
        System.out.println("Da them nguyen lieu moi vao kho (ID=" + newId + "): " + ing.getName());
        
    }

    // Hàm tìm kiếm theo id (tức tìm theo lô hàng)
    @Override
        public Ingredient search(Object obj){
            if (obj == null || !(obj instanceof String)){
                System.out.println("Khong the tim obj la null hay obj khong la string ");
                return null;
            }

            String name = obj.toString().toLowerCase();
            boolean found = false;
            System.out.println("Ket qua tim kiem cho: \"" + name + "\"" );
            displayer.singleSeperate();

            Ingredient goc = ingredientsData.get(name);
            if (goc != null) {
                found = true;
                System.out.println("Tong so luong trong kho hien co:");
                System.out.println(
                    "Name: " + goc.getName() + 
                    "\nQuantity tong: " + goc.getQuantity()
                );
                displayer.singleSeperate();
            }

            for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
                Ingredient ing = entry.getValue();
                if (ing.getName().equalsIgnoreCase(name)) {
                    found = true;
                    System.out.println(
                        "ID: " + entry.getKey() + 
                        "\nName: " + ing.getName() +
                        "\nHSD: " + ing.getDate() +
                        "\nNgay Nhap: " + ing.getNgayNhapHang() +
                        "\nQuantity: " + ing.getQuantity() +
                        "\nCost: " + ing.getCost()
                    );
                    displayer.singleSeperate();
                }
            }

            if (!found) {
                System.out.println("Khong tim thay nguyen lieu co ten \"" + name + "\" trong kho.");
            }

            return goc;
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
                total += ing.getCost() * ing.getQuantity();
                iterator.remove();

                Ingredient agg = ingredientsData.get(ing.getName().toLowerCase());
                if (agg != null){
                    agg.decreaseQuantity(ing.getQuantity());
                    if (agg.getQuantity() <= 0 ) agg.setQuantity(0);
                }
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
                    // cập nhật tổng
                    Ingredient agg = ingredientsData.get(name.toLowerCase());
                    if (agg != null) {
                        agg.decreaseQuantity(retrievedAmount);
                        if (agg.getQuantity() <= 0) agg.setQuantity(0);
                    }

                    Ingredient result = new Ingredient(name);
                    result.setCost(ing.getCost());
                    result.increaseQuantity(retrievedAmount);
                    return result;
                }
                break;
            }
        }
        return null;
    }

    // Tìm nguyên liệu có hạn sử dụng nhỏ nhất
    public Ingredient findEarliestExpiry(String name) {
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
        boolean allEnough = true;
        for (Dish dish : dishList) {
            for (Map.Entry<String, Integer> need : dish.readIngredients().entrySet()) {
                String ingName = need.getKey().toLowerCase();
                int requiredAmount = need.getValue();

                Ingredient inStock = ingredientsData.get(ingName);

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
            System.out.println("Du nguyen lieu de lam tat ca mon !");
        }
    }

    public static Ingredient IngredientInput (){
        UserInputHandler input = UserInputHandler.getUserInputHandler();
        Displayer displayer = Displayer.getDisplayer();
        displayer.doubleSeperate();
        System.out.println("=== THEM NGUYEN LIEU MOI ===");

        System.out.print("Nhap ten nguyen lieu: ");
        String name = input.getScanner().nextLine();

        System.out.print("Nhap so luong: ");
        int quantity = input.getScanner().nextInt();
        input.getScanner().nextLine(); // clear newline

        System.out.print("Nhap gia nhap: ");
        double cost = input.getScanner().nextDouble();
        input.getScanner().nextLine(); // clear newline

        System.out.print("Nhap ngay nhap (dd/MM/yyyy): ");
        String ngayNhap1 = input.getScanner().nextLine();
        LocalDate ngayNhap = LocalDate.parse(ngayNhap1,  DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.print("Nhap HSD (dd/MM/yyyy): ");
        String hsd1 = input.getScanner().nextLine();
        LocalDate hsd = LocalDate.parse(hsd1,  DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Ingredient moi = new Ingredient(name);
        moi.setQuantity(quantity);
        moi.setCost(cost);
        moi.setHSD(hsd);
        moi.setNgayNhap(ngayNhap);
        return moi;
    }


    //case 5, show tất cả nguyên liệu ở kho
    public void showStorage(){
        String[] message = {
            "Tat ca nguyen lieu co trong kho: "
        };
        String[] exit = {
            "Chon 0 de quay lai..."
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();
            displayer.displayMessage(message);

            XuatNguyenLieu(); //đã sửa 
            displayer.displayMessage(exit);
            inputHandler.getUserOption();
        }
        inputHandler.resetOption();
    }
        public void showAddRemoveIngredients() {
        String[] messageMenu = {
            "Nhap 0 de quay lai",
            "Chon them hoac xoa nguyen lieu"
        };
        String[] option = {
            "1. Them nguyen lieu moi vao kho",
            "2. Xoa nguyen lieu khoi kho"
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(messageMenu);
            displayer.displayOptions(option);

            // Đọc lựa chọn của người dùng
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();

            if (choice == GO_BACK_OPTION) {
                System.out.println("Quay lai menu truoc...");
                inputHandler.enter2Continue();
                break;
            }

            switch (choice) {
                case 1: { // ====== THÊM NGUYÊN LIỆU ======
                    String[] messageAdd = {
                        "Nhap 0 de quay lai",
                        "Moi nhap vao thong tin nguyen lieu moi:"
                    };
                    displayer.clearScreen();
                    displayer.displayMessage(messageAdd);

                    // Gọi hàm nhập nguyên liệu
                    Ingredient newIng = IngredientInput();

                    if (newIng == null) {
                        System.out.println("Khong the them: du lieu nguyen lieu khong hop le!");
                        inputHandler.enter2Continue();
                        break;
                    }

                    // Thêm vào kho
                    System.out.println("Dang them nguyen lieu moi vao kho...");
                    add(newIng);

                    // Hiển thị lại kết quả tìm kiếm (để xác nhận)
                    System.out.println("\nKiem tra ket qua them vao:");
                    search(newIng.getName().toLowerCase());

                    System.out.println("\nThem nguyen lieu thanh cong!");
                    inputHandler.enter2Continue();
                    break;
                }

                case 2: { // ====== XÓA NGUYÊN LIỆU ======
                    String[] messageRemove = {
                        "Nhap 0 de quay lai",
                        "Moi nhap vao ID nguyen lieu can xoa:"
                    };
                    displayer.clearScreen();
                    displayer.displayMessage(messageRemove);

                    try {
                        int removeId = inputHandler.getScanner().nextInt();
                        inputHandler.getScanner().nextLine(); // clear newline

                        if (removeId == GO_BACK_OPTION) {
                            System.out.println("Huy thao tac xoa, quay lai menu truoc.");
                            inputHandler.enter2Continue();
                            break;
                        }

                        System.out.println("Dang xoa nguyen lieu co ID = " + removeId + "...");
                        Ingredient ing = remove(removeId);
                        search(ing.getName());
                    } catch (Exception e) {
                        System.out.println("Loi: Vui long nhap so hop le.");
                        inputHandler.getScanner().nextLine(); // clear buffer nếu lỗi nhập
                    }

                    inputHandler.enter2Continue();
                    break;
                }

                default: {
                    System.out.println("Lua chon khong hop le, vui long chon lai (1, 2 hoac 0).");
                    inputHandler.enter2Continue();
                    break;
                }
            }

            // Reset lại option để tránh lỗi vòng lặp
            inputHandler.resetOption();
        }
    }

}
    