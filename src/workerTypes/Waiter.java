package workerTypes;
import controllers.EventHandler;
import enums.OrderState;
import java.util.List;
import models.Order;
import models.Table;


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
            Order order = new Order(null);
            Table table = new Table(10);
            if (order == null) {
                System.out.println(" không còn order -> tạm nghỉ!");
                break;
            }

            OrderState state = order.getState();

            switch (state) {
                case NEW:
                    getOrder(table);
                    EventHandler.getEventHandler().addOrder(order);
                    EventHandler.getEventHandler().notifyChefs();
                    break;

                case UNFINISHED:
                    retakeOrder(table);
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

        }
        // Nếu UNFINISHED thì dùng retakeOrder
    }

    @Override
    public void stopWorking() {
        
    }
    // Lấy order của bàn
    private void getOrder(Table table) {
        Order order = new Order(table);
        for(String dish : table.orderRNG())
            order.writeOrder(dish);
            System.out.println("Waiter: Đã lấy món mới cho order của bàn " );
        }
    
    // Lấy lại order nấu mấy món trước không đủ đồ để nấu
    private void retakeOrder(Table table) {
    Order order = new Order(table);
        // Sử dụng Order.getNumOfUnsatisfiedRequest để chạy vòng lăp for
        // Dùng vòng lặp for để chọn món rồi lưu vào temp, dùng hàm orderRNG để lấy ngẫu nhiên món
        // Loại trừ các món có trong excludedOrders
        // Bước 1: Xoá các món không đủ nguyên liệu
    order.updateOrder();
    // Bước 2: Lấy số món cần chọn lại
    int retryCount = order.getNumOfUnsatisfiedRequest();
    // Bước 3: Truy xuất trực tiếp excludedDishes (không cần get)
    List<String> excluded = order.getExcludedDishes(); // nếu cùng class Order thì dùng được
    // Bước 4: Chọn lại các món mới, tránh món bị loại
    for (int i = 0; i < retryCount; i++) {
        String dish;
        do {
            List<String> randomList = table.orderRNG(); // trả về List<String>
        dish = randomList.get(0);
        } while (excluded.contains(dish)); // không chọn lại món lỗi
        order.writeOrder(dish);
    }
}
}