package controllers;
import contracts.ManagerHandler;
import java.time.LocalDate;
import java.util.*;
import models.*;
import utils.Displayer;

public class RevenueManager implements ManagerHandler {
    private static RevenueManager self;
    private Displayer displayer = Displayer.getDisplayer();
    private HashMap<LocalDate, DailyRevenue> revenueRecords; //danh sách các DailyRevenue, đc lưu theo từng ngày 

    @Override
    public void showGeneralInfo() {
        String[] message = {
            "Day la trinh quan ly doanh thu cua nha hang",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. Kiem tra doanh thu cua tung ngay",
            "2. Tinh toan loi nhuan thuc te",
        };
        displayer.displayMessage(message);
    }

    @Override
    public void createReport() {

    }
    @Override
    public void add(Object obj){

    }
    @Override
    public void remove(int objID){

    }
    @Override
    public Object search(int objID){
        return null;
    }

    public HashMap<LocalDate, DailyRevenue> getRevenueRecords(){ return revenueRecords; }

    //Lấy doanh thu của ngày nào đó 
    public double getRevenueOfDate(LocalDate date){
        double totalAmount = 0;
        DailyRevenue RevenueOfDate = revenueRecords.get(date);
        for ( Order order : RevenueOfDate.getTransactions()){
            totalAmount += order.getAmount();
        }
        return totalAmount;
    }

    // lấy LỢI NHUẬN của 1 ngày 
    public double getProfitOfDate(LocalDate date){
        return getRevenueOfDate(date)/3*2;
    }

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

    

}
