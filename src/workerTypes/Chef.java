package workerTypes;

import controllers.*;
import java.util.Map;
import java.util.Random;
import models.Dish;
import models.Order;
import utils.Displayer;

public class Chef extends base.Worker {
    private EventHandler eventHlr = EventHandler.getEventHandler();
    private Displayer displayer = Displayer.getDisplayer();
    private String[] customCompliment = {
        "Ok.. (-_-)", "Great!", "Wow! ('O')", "Amazing!!! ( @ v @ )", 
        "Outstanding! ^~^", "Tasty ( ^ v ^ )", "How.. Magic? (o_o)?", "Perfection (U w U)",
        "Hell!!! yeah (> v <)"
    };

    public Chef(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    @Override
    public void startWorking(Order order) {
        if (order == null) {
            System.out.println("Chef: khong co order nao, nghi!");
            return;
        }
        
        boolean allAvailable = true;

        for (String dish : order.getDishes()) {
            //System.out.printf("| %-10s |", dish);
            loop1: // khai bao loop bên ngoài để break
            for (Dish dishMENU : DishManager.getManager().getDishList()){
                if (dish.equalsIgnoreCase(dishMENU.getName())){
                    Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                
                    for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                        String ingredientName = entry.getKey();
                        int requiredAmount = entry.getValue();

                        if (!SupplyManager.getManager().checkIngredients(ingredientName, requiredAmount)) {
                            //System.out.println("\nChef: Khong du nguyen lieu cho mon:  " + dishMENU.getName());
                            
                            order.addExcludedDish(dishMENU.getName());
                            allAvailable = false;
                            break loop1; // Không cần kiểm tra tiếp nguyên liệu của món này
                        }
                    }
                }
            }
        }

        // ========================================================================
        // Hiện thị các món do waiter lấy
        int dashDelta = 6;
        int dashDelta2 = 2;
        int[] format2 = {dashDelta2, dashDelta2, dashDelta2, 3, dashDelta2, dashDelta2, dashDelta2};
        int[] format = {dashDelta, dashDelta, dashDelta, dashDelta};
        System.out.println("Nhan vien phuc vu da lay mon\n");

        displayer.printFormatLine(format2);
        displayer.printFormatLine(format);
        System.out.println("|/       |      "+ "Order" + "      |       \\|");
        displayer.printFormatLine(format);
        System.out.println("||       +        +        +       ||");
        for (String dishName : order.getDishes()) {
            String centered = displayer.centerString(dishName, 33);
            System.out.println("||" + centered + "||");
        }
        System.out.println("||       +        +        +       ||");
        displayer.printFormatLine(format);
        System.out.printf("|\\ Bill: %-25s /|\n", "................");
        displayer.printFormatLine(format);
        System.out.println();

        // ========================================================================

        if (allAvailable) {
            Random rand = new Random();

            System.out.println("\nChef: du nguyen lieu - bat dau nau!");
            System.out.println("=".repeat(10) + "   COOKING....   " + "=".repeat(10));
            for (String dish : order.getDishes()) {
                for (Dish dishMENU : DishManager.getManager().getDishList()){
                    if (dish.equalsIgnoreCase(dishMENU.getName())){
                        Map<String, Integer> requiredIngredients = dishMENU.readIngredients(); // nguyên liệu cần cho món
                    
                        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                            String ingredientName = entry.getKey();
                            int requiredAmount = entry.getValue();
                            SupplyManager.getManager().getIngredient(ingredientName, requiredAmount);
                        }

                        System.out.println("Chef: Da nau xong mon:  " + dish);
                        

                        int ratingIdx = rand.nextInt(5) + 1;
                        String rating = displayer.wrapWord(dish, ratingIdx, "*");
                        String ratingCentered = displayer.centerString(rating, 33);
                        int complimentIdx = rand.nextInt(customCompliment.length);
                        String compliment = customCompliment[complimentIdx];
                        
                        System.out.printf(" %-33s %-20s\n", ratingCentered, compliment);
                    }
                }
            }

            System.out.println("\nChef: Hoan thanh tat ca mon do");
            System.out.println("=".repeat(50));
            // eventHlr.notifyWaiters(order);
            // for (String dishName : order.getDishes()) {
            //     String centered = displayer.centerString(dishName, 33);
            //     System.out.println("||" + centered + "||");
            // }
            // for (DailyRevenue dr : RevenueManager.getManager().getRevenueRecords().values()) {
            //     System.out.println(dr.getTransactions().size());
            // }
            eventHlr.notifyWaiters(order);
            double bill = order.calculateAmount();
            
            

            // ========================================================================

            displayer.printFormatLine(format2);
            displayer.printFormatLine(format);
            System.out.println("|/       |      "+ "Order" + "      |       \\|");
            displayer.printFormatLine(format);
            System.out.println("||       +        +        +       ||");
            for (String dishName : order.getDishes()) {
                String centered = displayer.centerString(dishName, 33);
                System.out.println("||" + centered + "||");
            }
            System.out.println("||       +        +        +       ||");
            displayer.printFormatLine(format);
            System.out.printf("|\\ Bill: %-25.2f /|\n", bill);
            displayer.printFormatLine(format);
            System.out.println();

            // ========================================================================
            
            //System.out.println("Ban da thanh toan: " + bill);
            
            RevenueManager.getManager().addTransaction(TableManager.getManager().getDate(), order);
            // EventHandler.getEventHandler().notifyTableManager();

            // eventHlr.notifyWaiters(order);

        } else {
            // ========================================================================
            System.out.println("Chef: Thieu nguyen lieu - yeu cau lay lai order\n");
            
            displayer.printFormatLine(format2);
            displayer.printFormatLine(format);
            System.out.println("|/       |      "+ "Order" + "      |       \\|");
            displayer.printFormatLine(format);
            System.out.println("||       +        +        +       ||");
            for (String dishName : order.getDishes()) {
                if (order.isExclusiveDish(dishName)) {
                    String styledWord = displayer.centerSplit(dishName, 33);
                    String centered = displayer.centerString(styledWord, 33);
                    System.out.println("||" + centered + "||");
                } else {
                    String centered = displayer.centerString(dishName, 33);
                    System.out.println("||" + centered + "||");
                }
                
            }
            System.out.println("||       +        +        +       ||");
            displayer.printFormatLine(format);
            System.out.printf("|\\ Bill: %-25s /|\n", "................");
            displayer.printFormatLine(format);

            // ========================================================================

            order.updateOrder(); // loại món thiếu ra khỏi dishes
            eventHlr.addOrder(order); // đưa bàn vào hàng chờ
            eventHlr.notifyWaiters(order); // kêu waiter tới lấy lại order
        }
    }
        

    @Override
    public void stopWorking() {
        
    }
}
