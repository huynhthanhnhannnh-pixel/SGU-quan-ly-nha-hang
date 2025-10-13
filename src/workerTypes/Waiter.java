package workerTypes;
import controllers.EventHandler;
import enums.OrderState;
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
        while (true) {
            // Lấy order từ EventHandler
            Order order = EventHandler.getEventHandler().getTable();

            if (order == null) {
                System.out.println(getName() + " không còn order -> tạm nghỉ!");
                break;
            }

            OrderState state = order.getState();

            switch (state) {
                case NEW:
                    getOrder(order);
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs();
                    break;

                case UNFINISHED:
                    retakeOrder(order);
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs();
                    break;

                case COMPLETED:
                    double bill = order.getAmount();
                    System.out.println("Bàn " + order.getTable() +
                                       " đã thanh toán: " + bill);
                    System.out.println("da thanh toan");
                    EventHandler.getEventHandler().notifyTableManager();
                    break;

                default:
                    System.out.println(getName() + " -> trạng thái không xác định: " + state);
                    break;
            }

            // Nghỉ mô phỏng
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        // Nếu UNFINISHED thì dùng retakeOrder
    }
    
    @Override
    public void stopWorking() {
        
    }

    // Lấy order của bàn
    private void getOrder(Order order) {

        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món


    }

    // Lấy lại order nấu mấy món trước không đủ đồ để nấu
    private void retakeOrder(Order order) {

        // Sử dụng Order.getNumOfUnsatisfiedRequest để chạy vòng lăp for
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món
        // Loại trừ các món có trong excludedOrders

    }
}