package models;
import controllers.RevenueManager;
import java.time.LocalDate;
import java.util.*;

public class DailyRevenue {
    private LocalDate date;
    // private int revenueID;
    private List<Order> transactions; //danh sách các order trong ngày

    public DailyRevenue(LocalDate date){
        this.date = date;
        // this.revenueID = revenueID;
        this.transactions = new ArrayList<>();
    }

    public LocalDate getDate(){ return date; }
    // public int getID(){ return revenueID; }
    public List<Order> getTransactions(){ return transactions; }

    //hàm lấy tổng doanh thu
    public double getTotalAmount() {
        double totalAmount = 0 ;
        for ( Order order : transactions){
            totalAmount += order.getAmount();
        }
        return totalAmount;
    }

    public double getTotalProfit(){
        return getTotalAmount()/3*2 - RevenueManager.getManager().getProfitLoss().get(date);
    }
}
