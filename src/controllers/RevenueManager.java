package controllers;
import contracts.ManagerHandler;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.*;
import utils.Displayer;
public class RevenueManager implements ManagerHandler {
    private static RevenueManager self;
    private Displayer displayer = Displayer.getDisplayer();
    private HashMap<LocalDate, DailyRevenue> revenueRecords; //danh sách các DailyRevenue, đc lưu theo từng ngày 
    private HashMap<LocalDate, Double> profitLoss = new LinkedHashMap<>(); // danh sách các khoản phí
    // private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    // private int GO_BACK_OPTION = 0;


    @Override
    public void showGeneralInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            displayer.clearScreen();
            LocalDate date = null;
            while (date == null) {
                System.out.print("Nhap ngay muon kiem tra doanh thu va loi nhuan (dd/MM/yyyy) (nhap 0 de huy): ");
                String dateToSearch = inputHandler.getScanner().nextLine().trim();
                if (dateToSearch.equals("0")) break; 
                try {
                    date = LocalDate.parse(dateToSearch, formatter);
                } catch (Exception e) {
                    System.out.println("Dinh dang ngay nhap khong hop le. Vui long nhap lai theo dd/MM/yyyy.");
                }

                search(date);
            }
            inputHandler.enter2Continue();
            break;
        }
    }

    @Override
    public void createReport() {

    }
    // thêm doanh thu của 1 ngày vào revenueRecords
    @Override
    public void add(Object obj){
        HashMap<LocalDate, DailyRevenue> revenues = getRevenueRecords();
        revenues.put(LocalDate.now(), (DailyRevenue)obj);
    }
    @Override
    public Object remove(Object objID){
        LocalDate keyToRemove = null;
        for (Map.Entry<LocalDate, DailyRevenue> entry : revenueRecords.entrySet()) {
            DailyRevenue dr = entry.getValue();
            if (dr.getDate() == (LocalDate)objID) {   // Giả sử DailyRevenue có phương thức getId()
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove != null) {
            return revenueRecords.remove(keyToRemove); // Xóa phần tử khỏi map
        } else {
            System.out.println("Không tìm thấy bản ghi có ID: " + objID);
            return null;
        }
    }
    @Override
    public Object search(Object objID){
        if (!(objID instanceof LocalDate)) {
            System.out.println("Gia tri nhap vao khong phai LocalDate");
            return null;
        }

        LocalDate date = (LocalDate) objID;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DailyRevenue revenue = revenueRecords.get(date);

        if (revenue != null) {
            System.out.println("Doanh thu cua ngay " + date.format(formatter) + ": " + revenue.getTotalAmount());
            System.out.println("==================================================");
            System.out.println("Loi nhuan cua ngay " + date.format(formatter) + ": " + revenue.getTotalProfit());
            System.out.println("Chi phi hoat dong cua ngay + " + date.format(formatter) + ": " + profitLoss.get(date));
            System.out.println("Tien von nguyen lieu cua ngay + " + date.format(formatter) + ": " + revenue.getTotalAmount()/3*1);
        } else {    
            System.out.println("Khong tin thay doanh thu va loi nhuan cua ngay " + date.format(formatter));
        }

        return revenue;
    }

    public void saveToFile(){   
        BufferedWriter bw = null;
        try {
            Path p1 = Paths.get("cache", "Revenue.txt");
            Path p2 = Paths.get("src", "cache", "Revenue.txt");

            if (Files.exists(p1)){
                bw = Files.newBufferedWriter(p1, StandardCharsets.UTF_8);
            }
            else if ( Files.exists(p2)){
                bw = Files.newBufferedWriter(p2, StandardCharsets.UTF_8);
            }
            else {
                throw new FileNotFoundException("Schedule.txt not found in classpath or cache folders");
            }

            for (Map.Entry<LocalDate, DailyRevenue> entry : revenueRecords.entrySet()){
                LocalDate date = entry.getKey();
                DailyRevenue dr = entry.getValue();
                bw.write(String.valueOf(date) + "|" + String.valueOf(dr.getTotalAmount()));
                bw.newLine();
            }

        } 
         catch (IOException e) {
            e.printStackTrace();
         }
          finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException ignored) {}
            }
          }
    }
    @Override
    public Object Input(){
        return  null;
    }

    public HashMap<LocalDate, DailyRevenue> getRevenueRecords(){ return revenueRecords; }

    //Lấy doanh thu của ngày nào đó 
    public double getRevenueOfDate(LocalDate date){ return revenueRecords.get(date).getTotalAmount(); }

    public double getProfitOfDate(LocalDate date){ return revenueRecords.get(date).getTotalProfit(); }

    // Private constructor to enforce singleton
    private RevenueManager() {
        revenueRecords = new HashMap<>();
    }

    // Public method to get the single self
    public static RevenueManager getManager() {
        if (self == null) {
            self = new RevenueManager();
        }
        return self;
    }    

    public HashMap<LocalDate, Double> getProfitLoss() { return profitLoss; }


    public void addTransaction(java.time.LocalDate date, models.Order order) {
        DailyRevenue dr = revenueRecords.get(date);
        if (dr == null) {
            dr = new DailyRevenue(date);
            revenueRecords.put(date, dr);
        }
        dr.getTransactions().add(order);
        
    }

}
