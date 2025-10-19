package controllers;

import java.util.HashMap;

import contracts.ManagerHandler;
import models.Table;
import utils.*;

public class TableManager implements ManagerHandler {
    private static TableManager self;
    private Displayer displayer = Displayer.getDisplayer();
    //private UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    private HashMap<Integer, Table> tableList = new HashMap<Integer, Table>(); // Danh sách bàn, <Mã bàn, bàn> ví dụ: tìm bàn số 5 => <5, bàn>
    private int numOfTable = 10; // Số lượng bàn ăn

    @Override
    public void showGeneralInfo() {
        String[] message = {
            "Day la trinh quan ly ban an sieu nhanh, con khach an co nhanh ko thi deo biet",
            "Trinh quan ly gom cac tinh nang nhu:",
            "1. Kiem tra xem ban nao trong, ban nao da duoc dat",
            "2. add/remove nhom khach hang vao tung ban",
            "Va dat biet la Menu cua chung ta duoc lam boi sieu dau bep ratatouille va And Cong Nhan(ACN)"
        };
        displayer.displayMessage(message);
    }
    
    @Override
    public void createReport() {
        System.out.println("Ban 1, 2, 3 con trong, Ban 4 thi bi con nho chubby an me roi");
    }
    @Override
    public void add(Object obj){

    }
    @Override
    public Object remove(int objID){
        return null;
    }
    @Override
    public Object search(Object objID){
        return null;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Initialization

    // khởi tạo bàn với số lương bàn đã cho
    private void loadTables(int numberOfTable) {
        // Sử dụng vòng lăp để tạo bàn rồi lưu vào dang sách bàn
        for ( int i = 1; i <= numberOfTable; i++){
            Table table = new Table(8);
            tableList.put(i,table);
        }
    }
    
    // Private constructor to enforce singleton
    private TableManager() {
        loadTables(numOfTable);
    }

    // Public method to get the single self
    public static TableManager getManager() {
        if (self == null) {
            self = new TableManager();
        }
        return self;
    }

    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    //===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+===+
    // Your codes go here

    // Chuẩn bị bàn ăn cho khách
    // public void prepareTable(int tableID) {
        // dựa vào tableID lấy ra object Table từ tableList(danh sách bàn)

        // thêm khách vào bàn

        

    // }

    // 2.1 random khách hàng (chua lam gi) 
    // public Customer customerRGN() {
    //     return null;
    // }
}
