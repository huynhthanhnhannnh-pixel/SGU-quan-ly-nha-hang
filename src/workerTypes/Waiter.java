package workerTypes;

import models.Order;

public class Waiter extends base.Worker {
    
    public Waiter(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void interact() {
        
    }

    @Override
    public void startWorking() {
        // Nếu NEW thì dùng getOrder

        // Nếu UNFINISHED thì dùng retakeOrder
    }

    @Override
    public void stopWorking() {
        
    }

    // Lấy order của bàn
    private void getOrder(Order order) {

        // Đếm số lượng khách hang rồi
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món


    }

    // Lấy lại order nấu mấy món trước không đủ đồ để nấu
    private void retakeOrder(Order order) {

        // Sử dụng Order.getNumOfUnsatisfiedRequest để chạy vòng lăp for
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món
        // Loại trừ các món có trong excludedOrders

    }
}
