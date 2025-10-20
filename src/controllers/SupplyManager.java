package controllers;

import contracts.ManagerHandler;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
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

    // normalize keys: trim, toLowerCase, remove spaces to match variants like "Tra Bong" and "TraBong"
    private String normalizeKey(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("\\s+", "");
    }

    @Override
    public void showGeneralInfo() {
        String[] intro = {
            "Day la trinh quan ly nguon cung thuc pham 100% sieu sach",
            "Trinh quan ly gom cac tinh nang nhu:"
        };

        String[] options = {
            "Xem cac nguyen lieu trong kho",
            "Them nguyen lieu vao kho",
            "Xoa nguyen lieu trong kho",
            "Tim kiem nguyen lieu trong kho",
            "Xuat cac nguyen lieu khong du so luong lam 1 mon",
            "Tao bao cao ve cac nguyen lieu hien tai dang co",
            "Xoa nguyen lieu het han khoi kho",
            "Xem danh sach mon an hien co (va cac thao tac mon an)",
            "Them nguyen lieu trong mon an",
            "Xoa nguyen lieu trong mon an",
            "Sua so luong nguyen lieu trong mon an",
            "Quay lai"
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(intro);
            displayer.displayOptions(options);

            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) {
                inputHandler.resetOption();
                break;
            }

            switch (choice) {
                case 1:
                    showStorage();
                    break;
                case 2:
                    showAddRemoveIngredients();
                    break;
                case 3: {
                    System.out.print("Nhap ID can xoa: ");
                    try {
                        int id = Integer.parseInt(inputHandler.getScanner().nextLine().trim());
                        remove(id);
                    } catch (Exception e) { System.out.println("ID khong hop le"); }
                    break;
                }
                case 4: {
                    System.out.print("Nhap ten nguyen lieu can tim: ");
                    String name = inputHandler.getScanner().nextLine().trim();
                    if (!name.equals("0")) search(name);
                    break;
                }
                case 5:
                    checkWarehouse();
                    break;
                case 6:
                    createReport();
                    break;
                case 7:
                    double lost = deleteExpiredandLowQuantityIngredients();
                    System.out.println("Gia tri hang lo bi xoa: " + lost);
                    break;
                case 8:
                    showDishesAndPrintMenu();
                    break;
                case 9:
                    showDishesAndPrintMenu();
                    break;
                case 10:
                    showDishesAndPrintMenu();
                    break;
                case 11:
                    showDishesAndPrintMenu();
                    break;
                default:
                    System.out.println("Lua chon khong hop le.");
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
        }
    }

    // Interactive submenu for options related to dishes and printing
    public void showDishesAndPrintMenu() {
        String[] header = {"Nhap 0 de quay lai", "Quan ly mon an va hien thi"};
        String[] options = {
            "Xem tat ca nguyen lieu (aggregate)",
            "Xem tat ca mon an va gia",
            "Them nguyen lieu vao mon an",
            "Xoa nguyen lieu khoi mon an",
            "Sua so luong nguyen lieu trong mon an"
        };

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);

            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) {
                inputHandler.resetOption();
                break;
            }

            switch (choice) {
                case 1:
                    Xuat("nguyenlieu");
                    break;
                case 2:
                    Xuat("monan");
                    break;
                case 3: {
                    // Add ingredient to a dish
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    System.out.print("So luong: ");
                    int amt = 0;
                    try { amt = Integer.parseInt(inputHandler.getScanner().nextLine().trim()); } catch (Exception e){ System.out.println("So luong khong hop le"); break; }
                    addIngredientToDish(dishName, ingName, amt);
                    break;
                }
                case 4: {
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu can xoa: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    removeIngredientFromDish(dishName, ingName);
                    break;
                }
                case 5: {
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu can sua: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    System.out.print("So luong moi: ");
                    int amt = 0;
                    try { amt = Integer.parseInt(inputHandler.getScanner().nextLine().trim()); } catch (Exception e){ System.out.println("So luong khong hop le"); break; }
                    changeIngredientAmountInDish(dishName, ingName, amt);
                    break;
                }
                default:
                    System.out.println("Lua chon khong hop le.");
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
        }
    }

    // New: top-level interactive flow for when user logs in as Supply Manager
    public void runSupplyManagerConsole() {
        // Auto-clean expired items at start of day
        deleteExpiredandLowQuantityIngredients();

        String[] header = {"Ban dang dang nhap voi vai tro: Supply Manager", "Chon mot chuc nang:"};
        String[] options = {"1. Ban muon quan li mon an?", "2. Ban muon quan li nguyen lieu?", "0. Quay lai"};

        while (true) {
            displayer.clearScreen();
            displayer.displayMessage(header);
            displayer.displayOptions(options);
            inputHandler.getUserOption();
            int choice = inputHandler.getCurrentOption();
            if (choice == GO_BACK_OPTION) { inputHandler.resetOption(); break; }

            if (choice == 1) {
                dishManagementMenu();
            } else if (choice == 2) {
                ingredientManagementMenu();
            } else {
                System.out.println("Lua chon khong hop le");
            }

            displayer.singleSeperate();
            inputHandler.enter2Continue();
            inputHandler.resetOption();
        }
    }

    // Dish management submenu per spec
    private void dishManagementMenu() {
        String[] header = {"Quan ly mon an"};
        String[] options = {
            "Xuat menu (ten, gia, nguyen lieu - khong can so luong)",
            "Them mon vao menu (nhap ten va danh sach nguyen lieu + so luong)",
            "Ham them nguyen lieu vao mon (them nguyen lieu vao mon co san)",
            "Ham sua nguyen lieu cua mon (cap nhat so luong)",
            "Ham xoa nguyen lieu cua mon",
            "Chon 0 de quay lai"
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
                    printMenuForGuests();
                    break;
                case 2:
                    addDishInteractive();
                    break;
                case 3: {
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    System.out.print("So luong: ");
                    int amt = 0; try { amt = Integer.parseInt(inputHandler.getScanner().nextLine().trim()); } catch(Exception e){ System.out.println("So luong khong hop le"); break; }
                    addIngredientToDish(dishName, ingName, amt);
                    break;
                }
                case 4: {
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu can sua: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    System.out.print("So luong moi: ");
                    int amt = 0; try { amt = Integer.parseInt(inputHandler.getScanner().nextLine().trim()); } catch(Exception e){ System.out.println("So luong khong hop le"); break; }
                    changeIngredientAmountInDish(dishName, ingName, amt);
                    break;
                }
                case 5: {
                    System.out.print("Ten mon (exact): ");
                    String dishName = inputHandler.getScanner().nextLine().trim();
                    if (dishName.equals("0")) break;
                    System.out.print("Ten nguyen lieu can xoa: ");
                    String ingName = inputHandler.getScanner().nextLine().trim();
                    if (ingName.equals("0")) break;
                    removeIngredientFromDish(dishName, ingName);
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

    // Ingredient management submenu per spec
    private void ingredientManagementMenu() {
        String[] header = {"Quan ly nguyen lieu"};
        String[] options = {
            "Xem nguyen lieu trong kho (aggregate)",
            "Xem cac lo nguyen lieu hien dang co (chi tiet lo)",
            "Xem cac nguyen lieu khong du de lam mot mon",
            "Them nguyen lieu",
            "Xoa nguyen lieu (theo ID)",
            "Tim kiem nguyen lieu",
            "Quay lai"
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
                    Xuat("nguyenlieu");
                    break;
                case 2:
                    createReport();
                    break;
                case 3:
                    checkWarehouse();
                    break;
                case 4: {
                    Ingredient newIng = IngredientInput();
                    if (newIng != null) add(newIng);
                    break;
                }
                case 5: {
                    System.out.print("Nhap ID can xoa: ");
                    try { int id = Integer.parseInt(inputHandler.getScanner().nextLine().trim()); remove(id); } catch(Exception e){ System.out.println("ID khong hop le"); }
                    break;
                }
                case 6: {
                    System.out.print("Nhap ten nguyen lieu can tim: ");
                    String name = inputHandler.getScanner().nextLine().trim(); if (!name.equals("0")) search(name);
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

    // Print a guest-friendly menu: name, price, ingredient names only
    public void printMenuForGuests() {
        if (dishList.isEmpty()) {
            System.out.println("Khong co mon an trong menu.");
            return;
        }
        for (Dish dish : dishList) {
            displayer.singleSeperate();
            System.out.println("Ten: " + dish.getName());
            System.out.println("Gia: " + String.format("%.2f", dish.getPrice()));
            System.out.println("Nguyen lieu: ");
            for (String ing : dish.readIngredients().keySet()) {
                System.out.println(" - " + ing);
            }
        }
    }

    // Interactive add-dish routine (name + list of ingredient|amount pairs)
    public void addDishInteractive() {
        System.out.print("Nhap ten mon moi (nhap 0 de huy): ");
        String name = inputHandler.getScanner().nextLine().trim();
        if (name.equals("0") || name.isEmpty()) { System.out.println("Huy them mon."); return; }
        Dish dish = new Dish(name);
        System.out.println("Nhap nguyen lieu cho mon (ten|soLuong). Nhap dong rong hoac 0 de ket thuc.");
        while (true) {
            System.out.print("Nhap nguyen lieu va so luong: ");
            String line = inputHandler.getScanner().nextLine().trim();
            if (line.equals("0") || line.isEmpty()) break;
            String[] parts = line.split("\\|");
            if (parts.length != 2) { System.out.println("Dinh dang phai la: Ten|SoLuong"); continue; }
            String ing = parts[0].trim();
            int amt = 0;
            try { amt = Integer.parseInt(parts[1].trim()); } catch (Exception e) { System.out.println("So luong khong hop le"); continue; }
            dish.addIngredient(ing, amt);
        }
        dishList.add(dish);
        System.out.println("Da them mon: " + dish.getName());
    }
    
    @Override
    public void createReport() {
        System.out.println("Danh sach nguyen lieu trong kho:");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getValue();
            displayer.singleSeperate();
            System.out.println(
                "ID: " + entry.getKey() + 
                "\nName: " + ing.getName() + 
                "\nHSD: " + (ing.getDate() != null ? ing.getDate().format(outFmt) : "N/A") +
                "\nNgay Nhap hang: " + (ing.getNgayNhapHang() != null ? ing.getNgayNhapHang().format(outFmt) : "N/A") +
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

    // Overloaded Xuat methods
    // Xuat() with no args: print both aggregates and dishes
    public void Xuat(){
        System.out.println("--- Nguyen lieu (aggregate) ---");
        Xuat("nguyenlieu");
        System.out.println();
        System.out.println("--- Danh sach mon an ---");
        Xuat("monan");
    }

    // Xuat(kind): kind = "nguyenlieu" or "monan"
    public void Xuat(String kind){
        if (kind == null) return;
        if (kind.equalsIgnoreCase("nguyenlieu")){
            if (ingredientsData.isEmpty()){
                System.out.println("Khong co nguyen lieu nao trong aggregate.");
                return;
            }
            for (Map.Entry<String, Ingredient> entry : ingredientsData.entrySet()){
                Ingredient ing = entry.getValue();
                displayer.singleSeperate();
                System.out.println("Name: " + ing.getName() +
                                    "\nQuantity: " + ing.getQuantity() +
                                    "\nCost: " + ing.getCost() );
            }
            return;
        }

        if (kind.equalsIgnoreCase("monan")){
            if (dishList.isEmpty()){
                System.out.println("Khong co mon an nao.");
                return;
            }
            for (Dish dish : dishList){
                displayer.singleSeperate();
                System.out.println("Mon: " + dish.getName());
                System.out.println("Gia: " + String.format("%.2f", dish.getPrice()));
                System.out.println("Nguyen lieu:");
                HashMap<String,Integer> ingMap = dish.readIngredients();
                for (Map.Entry<String,Integer> e : ingMap.entrySet()){
                    System.out.println(" - " + e.getKey() + ": " + e.getValue());
                }
            }
            return;
        }

        System.out.println("Loai Xuat khong ho tro: " + kind);
    }

    // Add ingredient to a dish's recipe (does not touch stock), returns true if dish exists
    public boolean addIngredientToDish(String dishName, String ingName, int amount){
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList){
            if (dish.getName().equalsIgnoreCase(dishName)){
                dish.addIngredient(ingName, amount);
                System.out.println("Da them " + ingName + " vao mon " + dish.getName() + " (so luong: " + amount + ")");
                return true;
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
    }

    // Remove ingredient from a dish's recipe
    public boolean removeIngredientFromDish(String dishName, String ingName){
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList){
            if (dish.getName().equalsIgnoreCase(dishName)){
                dish.removeIngredient(ingName.toLowerCase());
                System.out.println("Da xoa " + ingName + " khoi mon " + dish.getName());
                return true;
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
    }

    // Change ingredient amount in a dish's recipe
    public boolean changeIngredientAmountInDish(String dishName, String ingName, int amount){
        if (dishName == null || ingName == null) return false;
        for (Dish dish : dishList){
            if (dish.getName().equalsIgnoreCase(dishName)){
                dish.changeIngredientAmount(ingName.toLowerCase(), amount);
                System.out.println("Da cap nhat so luong " + ingName + " trong mon " + dish.getName() + " = " + amount);
                return true;
            }
        }
        System.out.println("Khong tim thay mon: " + dishName);
        return false;
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
                    dishList.add(dish);
                }
            }
            System.out.println("Loading dishes successful");
        } catch (IOException e) {
            System.err.println("Error loading dishes: " + e.getMessage());
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
    }

    // === Load nguyên liệu ===
    private void loadIngredientsFromFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        BufferedReader reader = null;
        try {
            // 1) Try classpath resource (works when resources are on classpath or inside JAR)
            InputStream is = SupplyManager.class.getClassLoader().getResourceAsStream("resources/Ingredients.txt");
            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                // 2) Try common relative paths. These depend on the current working directory.
                Path p1 = Paths.get("resources", "Ingredients.txt");
                Path p2 = Paths.get("src", "resources", "Ingredients.txt");
                if (Files.exists(p1)) {
                    reader = Files.newBufferedReader(p1, StandardCharsets.UTF_8);
                } else if (Files.exists(p2)) {
                    reader = Files.newBufferedReader(p2, StandardCharsets.UTF_8);
                } else {
                    throw new FileNotFoundException("Ingredients.txt not found in classpath or resources folders");
                }
            }

            String line;
            int id = 1; // ID tự tăng
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] ing = line.split("\\|");
                    if (ing.length < 5) {
                        System.out.println("Dong du lieu k hop le: " + line);
                        continue;
                    }
                    String name = ing[0].trim();
                    int qty = Integer.parseInt(ing[1].trim());
                    double cost = Double.parseDouble(ing[2].trim());
                    LocalDate hsd = LocalDate.parse(ing[3].trim(), formatter);
                    LocalDate ngayNhap = LocalDate.parse(ing[4].trim(), formatter);

                    // Update aggregate data (normalize key)
                    String key = normalizeKey(name);
                    Ingredient dataNguyenlieu = ingredientsData.get(key);
                    if (dataNguyenlieu == null) {
                        dataNguyenlieu = new Ingredient(name);
                        dataNguyenlieu.setCost(cost);
                        dataNguyenlieu.increaseQuantity(qty);
                        ingredientsData.put(key, dataNguyenlieu);
                    } else {
                        dataNguyenlieu.increaseQuantity(qty);
                    }

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
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
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
            if (normalizeKey(name).equals(entry.getKey())) {
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
    String key = removed.getName() != null ? removed.getName().trim().toLowerCase() : "";
        Ingredient agg = ingredientsData.get(key);
        if (agg != null){
            agg.decreaseQuantity(removed.getQuantity());
            if (agg.getQuantity() <= 0 ) {
                // remove aggregate record when total reaches zero
                agg.setQuantity(0);
            }
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
        // normalize name for aggregate key, but keep lot's name as provided (trimmed)
        String lotName = ing.getName() != null ? ing.getName().trim() : "";
        ing = new Ingredient(lotName); // ensure lot's name is trimmed and a fresh object is used for lot storage
        // Note: we expect caller set HSD, ngayNhap, cost, quantity on the original obj; but to avoid aliasing
        // we will copy those fields from the provided object (the obj passed in may already be a separate instance)
        // However in this codebase Ingredient has no copy constructor; so we'll assume original obj had been set and
        // the caller passed it; to preserve fields we will cast original and reuse values when possible.
        // For simplicity, if obj was an Ingredient we should have used the passed object; but to ensure trimmed name
        // we construct a new Ingredient and then set fields from the original reference.
        Ingredient original = (Ingredient) obj;
        ing.setHSD(original.getDate());
        ing.setNgayNhap(original.getNgayNhapHang());
        ing.setCost(original.getCost());
        ing.setQuantity(original.getQuantity());

    String key = normalizeKey(lotName);
        int qtyToAdd = ing.getQuantity();

        // Update aggregate (create if needed)
        Ingredient agg = ingredientsData.get(key);
        if (agg == null) {
            Ingredient newAgg = new Ingredient(lotName);
            newAgg.setCost(ing.getCost());
            newAgg.increaseQuantity(qtyToAdd);
            ingredientsData.put(key, newAgg);
            System.out.println("Da them nguyen lieu moi vao aggregate: " + lotName);
        } else {
            agg.increaseQuantity(qtyToAdd);
            System.out.println("Da cong " + qtyToAdd + " vao nguyen lieu ton tai: " + agg.getName());
        }

        // Always create a new lot in `ingredients`
        int newId = ingredients.isEmpty() ? 1 : Collections.max(ingredients.keySet()) + 1;
        ingredients.put(newId, ing);
        System.out.println("Da them 1 lo hang moi vao kho (ID=" + newId + "): " + ing.getName());
        
    }

    // Hàm tìm kiếm theo id (tức tìm theo lô hàng)
    @Override
        public Ingredient search(Object obj){
            if (obj == null || !(obj instanceof String)){
                System.out.println("Khong the tim obj la null hay obj khong la string ");
                return null;
            }

            String name = normalizeKey(obj.toString());
            boolean found = false;
            System.out.println("Ket qua tim kiem cho: \"" + name + "\"" );
            displayer.singleSeperate();
            DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
                if (normalizeKey(ing.getName()).equals(normalizeKey(name))) {
                    found = true;
                    System.out.println(
                        "ID: " + entry.getKey() + 
                        "\nName: " + ing.getName() +
                        "\nHSD: " + (ing.getDate() != null ? ing.getDate().format(outFmt) : "N/A") +
                        "\nNgay Nhap: " + (ing.getNgayNhapHang() != null ? ing.getNgayNhapHang().format(outFmt) : "N/A") +
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

                String key = normalizeKey(ing.getName());
                Ingredient agg = ingredientsData.get(key);
                if (agg != null){
                    agg.decreaseQuantity(ing.getQuantity());
                    if (agg.getQuantity() <= 0 ) {
                        agg.setQuantity(0);
                    }
                }
                System.out.println("Da xoa nguyen lieu het han: " + ing.getName());
            }
        }
        return total;
    }

    
    // Lấy nguyên liệu ra khỏi kho
    public Ingredient getIngredient(String name, int amount) {
        for (Ingredient ing : ingredients.values()) {
            if (normalizeKey(ing.getName()).equals(normalizeKey(name))) {
                int retrievedAmount = ing.decreaseQuantity(amount);
                if (retrievedAmount > 0) {
                    // cập nhật tổng
                    String key = normalizeKey(name);
                    Ingredient agg = ingredientsData.get(key);
                    if (agg != null) {
                        agg.decreaseQuantity(retrievedAmount);
                        if (agg.getQuantity() <= 0) {
                            agg.setQuantity(0);
                        }
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
            if (normalizeKey(ing.getName()).equals(normalizeKey(name))) {
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
                String ingName = normalizeKey(need.getKey());
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            // Name
            System.out.print("Nhap ten nguyen lieu (nhap 0 de huy): ");
            String name = input.getScanner().nextLine().trim();
            if (name.equals("0")) return null;
            if (name.isEmpty()) {
                System.out.println("Ten nguyen lieu khong duoc de trong. Vui long nhap lai.");
                continue;
            }

            // Quantity
            Integer quantity = null;
            while (quantity == null) {
                System.out.print("Nhap so luong (so nguyen duong, nhap 0 de huy): ");
                String qline = input.getScanner().nextLine().trim();
                if (qline.equals("0")) return null;
                try {
                    quantity = Integer.parseInt(qline);
                    if (quantity <= 0) {
                        System.out.println("So luong phai la so nguyen duong. Nhap lai.");
                        quantity = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Dinh dang so luong khong hop le. Vui long nhap lai.");
                }
            }

            // Cost
            Double cost = null;
            while (cost == null) {
                System.out.print("Nhap gia nhap (so, nhap 0 de huy): ");
                String cline = input.getScanner().nextLine().trim();
                if (cline.equals("0")) return null;
                try {
                    cost = Double.parseDouble(cline);
                    if (cost < 0) {
                        System.out.println("Gia phai la so khong am. Nhap lai.");
                        cost = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Dinh dang gia khong hop le. Vui long nhap lai.");
                }
            }

            // Ngay nhap
            LocalDate ngayNhap = null;
            while (ngayNhap == null) {
                System.out.print("Nhap ngay nhap (dd/MM/yyyy) (nhap 0 de huy): ");
                String ngayNhap1 = input.getScanner().nextLine().trim();
                if (ngayNhap1.equals("0")) return null;
                try {
                    ngayNhap = LocalDate.parse(ngayNhap1, fmt);
                } catch (Exception e) {
                    System.out.println("Dinh dang ngay nhap khong hop le. Vui long nhap lai theo dd/MM/yyyy.");
                }
            }

            // HSD
            LocalDate hsd = null;
            while (hsd == null) {
                System.out.print("Nhap HSD (dd/MM/yyyy) (nhap 0 de huy): ");
                String hsd1 = input.getScanner().nextLine().trim();
                if (hsd1.equals("0")) return null;
                try {
                    hsd = LocalDate.parse(hsd1, fmt);
                    if (hsd.isBefore(ngayNhap)) {
                        System.out.println("HSD khong duoc nho hon ngay nhap. Vui long nhap lai.");
                        hsd = null;
                    }
                } catch (Exception e) {
                    System.out.println("Dinh dang HSD khong hop le. Vui long nhap lai theo dd/MM/yyyy.");
                }
            }

            Ingredient moi = new Ingredient(name);
            moi.setQuantity(quantity);
            moi.setCost(cost);
            moi.setHSD(hsd);
            moi.setNgayNhap(ngayNhap);
            return moi;
        }
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
    