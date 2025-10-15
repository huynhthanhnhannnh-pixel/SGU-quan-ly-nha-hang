package models;
import java.time.LocalDate;
import java.util.*;

public class DailyRevenue {
    private LocalDate date;
    private List<Order> transactions; //danh sách các order trong ngày

    public DailyRevenue(LocalDate date){
        this.date = date;
        this.transactions = new ArrayList<>();
    }

    public LocalDate getDate(){ return date; }
    public List<Order> getTransactions(){ return transactions; }
}
