package controllers;

import contracts.ManagerHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.*;
import utils.*;

public class SupplyManager implements ManagerHandler {
    private static SupplyManager self;
    private Displayer displayer = Displayer.getDisplayer();
    // private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    // Giờ kho là HashMap<Integer, Ingredient>
    private HashMap<Integer, Ingredient> ingredients = new LinkedHashMap<>(); // kho
    // int GO_BACK_OPTION = 0;
    private HashMap <String, Ingredient> ingredientsData = new LinkedHashMap<>();


    // normalize keys: trim, toLowerCase, remove spaces to match variants like "Tra Bong" and "TraBong"
    private String normalizeKey(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("\\s+", "");
    }


    // Ingredient management submenu per spec
    @Override
    public void showGeneralInfo() {
        String[] header = {"Quan ly nguyen lieu",           
         "Chon 0 de quay lai"};
        String[] options = {
            "Xem nguyen lieu trong kho (aggregate)",
            "Xem cac lo nguyen lieu hien dang co (chi tiet lo)",
            "Xem cac nguyen lieu khong du de lam mot mon",
            "Them nguyen lieu",
            "Xoa nguyen lieu (theo ID)",
            "Tim kiem nguyen lieu",
            "TEST nguyen lieu date thap nhat"
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
                    XuatNguyenLieu();
                    break;
                case 2:
                    createReport();
                    break;
                case 3:
                    checkWarehouse();
                    break;
                case 4: {
                    Ingredient newIng = Input();
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
                // test hàm
                case 7: {

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

    //Ghi lại các lô vào file chỉ thiếu mỗi id cho giống Ingredient.txt
    public void saveIngredientsToFile() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Path destination = Paths.get("cache", "Ingredients(copy).txt");
        try {
            Files.createDirectories(destination.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(destination, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Map.Entry<Integer, Ingredient> entry : ingredients.entrySet()) {
                    Ingredient ing = entry.getValue();
                    String name = ing.getName() != null ? ing.getName().trim() : "";
                    int qty = ing.getQuantity();
                    double cost = ing.getCost();
                    String hsd = ing.getDate() != null ? ing.getDate().format(fmt) : "";
                    String ngayNhap = ing.getNgayNhapHang() != null ? ing.getNgayNhapHang().format(fmt) : "";
                    bw.write(String.format("%s|%d|%.2f|%s|%s", name, qty, cost, hsd, ngayNhap));
                    bw.newLine();
                }
            }
            System.out.println("Đã lưu thành công vào file: " + destination.toString());
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
            e.printStackTrace();
        }
}


    // Private constructor to enforce singleton
    private SupplyManager() {
        loadIngredientsFromFile();
        copyFile();
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
    public Ingredient remove(Object id) {
        if (!ingredients.containsKey(id)) {
            System.out.println("Khong the xoa: id " + id + " khong ton tai.");
            return null;
        }
        Ingredient removed = ingredients.remove(id);
        String key = normalizeKey(removed.getName());
            Ingredient agg = ingredientsData.get(key);
            if (agg != null){
                agg.decreaseQuantity(removed.getQuantity());
                if (agg.getQuantity() <= 0 ) {
                    // remove aggregate record when total reaches zero
                    agg.setQuantity(0);
                }
            }

            System.out.println("Da xoa nguyen lieu: " + removed.getName() + " (so luong: " + removed.getQuantity() + ")");
            // persist changes after removal
            saveIngredientsToFile();
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
        ing = new Ingredient(lotName);
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
        // persist changes to cache file
        saveIngredientsToFile();
        
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


    // Xóa nguyên liệu hết hạn và số lượng = 0 và trả về giá hàng bị hủy ( dùng để tính chi phí khấu hao của nguyên liệu )
    public double deleteExpiredandLowQuantityIngredients(LocalDate today) {
        double total = 0;
        Iterator<Map.Entry<Integer, Ingredient>> iterator = ingredients.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Ingredient> entry = iterator.next();
            Ingredient ing = entry.getValue();
            if ((ing.getDate() != null && ing.getDate().isBefore(today)) || ing.getQuantity() == 0 ) {
                total += ing.getCost() * ing.getQuantity();
                iterator.remove();
                saveIngredientsToFile();
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
        // thêm số hao hụt hàng hóa vào tổng cost 1 ngày
        RevenueManager.getManager().getProfitLoss().put(today, total);
        // persist updated inventory after deletions
        saveIngredientsToFile();
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
        // Improve: for each dish compute how many full portions can be made
        // and list missing/insufficient ingredients with quantities.
        controllers.DishManager dm = controllers.DishManager.getManager();
        List<Dish> dishList = dm.getDishList();
        if (dishList == null || dishList.isEmpty()) {
            System.out.println("Khong co mon an nao de kiem tra.");
            return;
        }

        boolean anyMakeable = false;
        System.out.println("Kiem tra kho de biet co the lam duoc may phan cua moi mon:");
        for (Dish dish : dishList) {
            Map<String, Integer> needs = dish.readIngredients();
            if (needs == null || needs.isEmpty()) {
                System.out.println("- " + dish.getName() + ": Khong co nguyen lieu yeu cau (bo qua).");
                continue;
            }

            // For each ingredient compute how many portions it allows
            int maxPortions = Integer.MAX_VALUE;
            List<String> shortages = new ArrayList<>();

            for (Map.Entry<String, Integer> need : needs.entrySet()) {
                String needRaw = need.getKey();
                String key = normalizeKey(needRaw);
                int required = need.getValue() != null ? need.getValue() : 0;

                Ingredient agg = ingredientsData.get(key);
                int available = (agg != null) ? agg.getQuantity() : 0;

                if (required <= 0) {
                    // ignore invalid requirement
                    continue;
                }

                if (available <= 0) {
                    shortages.add(needRaw + " (thieu: " + required + ")");
                    maxPortions = 0;
                } else if (available < required) {
                    shortages.add(needRaw + " (can: " + required + ", co: " + available + ")");
                    maxPortions = Math.min(maxPortions, available / required);
                } else {
                    maxPortions = Math.min(maxPortions, available / required);
                }
            }

            if (maxPortions > 0 && maxPortions != Integer.MAX_VALUE) {
                System.out.println("- " + dish.getName() + ": Co the lam duoc " + maxPortions + " phan.");
                anyMakeable = true;
            } else if (maxPortions == Integer.MAX_VALUE) {
                // rare: all requirements had required<=0
                System.out.println("- " + dish.getName() + ": Yeu cau nguyen lieu khong hop le.");
            } else {
                System.out.println("- " + dish.getName() + ": Khong the lam phan nao.");
                if (!shortages.isEmpty()) {
                    System.out.println("  Nguyen lieu thieu/khong du:");
                    for (String s : shortages) System.out.println("    - " + s);
                }
            }
        }

        if (!anyMakeable) {
            System.out.println("(Luu y) Kho hien khong du nguyen lieu de lam bat ky mon nao hoac so luong la 0.");
        }
    }

    @Override
    public  Ingredient Input (){
        UserInputHandler input = UserInputHandler.getUserInputHandler();
        Displayer displayer = Displayer.getDisplayer();
        displayer.doubleSeperate();
        System.out.println("=== THEM NGUYEN LIEU MOI ===");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            // Name
            System.out.print("Nhap ten nguyen lieu (nhap 0 de huy): ");
            String name = input.getScanner().nextLine().trim().replaceAll("\\s+", "");
            if (name.equals("0")) return null;
            if (name.isEmpty()) {
                System.out.println("Ten nguyen lieu khong duoc de trong. Vui long nhap lai.");
                continue;
            }

            // Quantity
            Integer quantity = null;
            while (quantity == null) {
                System.out.print("Nhap so luong (so nguyen duong, nhap 0 de huy): ");
                String qline = input.getScanner().nextLine().trim().replaceAll("\\s+", "");
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

    //copy file 
    public void copyFile(){
        // write the copy to the runtime cache folder (same folder used by saveDishesToFile)
        Path destination = Paths.get("cache", "Ingredients(copy).txt");
        try {
            // Try copying from classpath resource first (works when running from jar/IDE)
            InputStream is = DishManager.class.getClassLoader().getResourceAsStream("resources/Ingredients.txt");
            Files.createDirectories(destination.getParent());
            if (is != null) {
                try (InputStream in = is) {
                    Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Đã copy file từ classpath thành công!");
                }
            } else {
                // Fallback to filesystem path relative to working directory
                Path source = Paths.get("src", "resources", "Ingredients.txt");
                if (Files.exists(source)) {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Đã copy file từ src/resources thành công!");
                } else {
                    System.err.println("Nguon Dishes.txt khong tim thay (checked classpath and src/resources)");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}