package test;

import controllers.SupplyManager;
import controllers.WorkerManager;

public class TestSuite {
    public static void main(String[] args) {
        java.io.PrintWriter out = null;
        try {
            out = new java.io.PrintWriter(new java.io.FileWriter("test-output.txt", false));
            out.println("=== TEST SUITE START ===");
            out.println("1) Test load Workers.txt (non-interactive check)");
            WorkerManager wm = WorkerManager.getManager();
            out.println(" - WorkerManager instance created: " + (wm != null));

            out.println("\n2) Test load Dishes.txt and print first 10 dishes summary:");
            SupplyManager sm = SupplyManager.getManager();
            out.println(" - Dish count: " + sm.getDishList().size());
            int i = 0;
            for (var d : sm.getDishList()) {
                if (i++ >= 10) break;
                out.println("   - " + d.getName() + " (ingredients: " + d.readIngredients().keySet().size() + ")");
            }

            out.println("\n3) Test ingredient lots (first 10):");
            out.println(" - Lot count: " + sm.getKho().size());
            int j = 0;
            for (var e : sm.getKho().entrySet()) {
                if (j++ >= 10) break;
                out.println("   - LotID=" + e.getKey() + " name=" + e.getValue().getName() + " qty=" + e.getValue().getQuantity());
            }

            out.println("\n4) Test checkWarehouse (non-interactive)");
            // capture checkWarehouse output by temporarily redirecting System.out? We'll just call it (it prints to console)
            sm.checkWarehouse();

            out.println("=== TEST SUITE END ===");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) out.close();
        }
        System.out.println("Test harness finished — output written to test-output.txt");
    }
}
