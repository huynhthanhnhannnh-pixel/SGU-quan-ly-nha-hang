package test;

import controllers.SupplyManager;
import models.Ingredient;

public class TestSupply {
    public static void main(String[] args) {
        SupplyManager manager = SupplyManager.getManager();
        System.out.println("Before:");
        manager.search("Trung");

        Ingredient ing = new Ingredient("Trung");
        ing.setQuantity(10);
        // set cost and dates to match file's Trung entry
        ing.setCost(500);
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            ing.setHSD(java.time.LocalDate.parse("28/09/2026", fmt));
            ing.setNgayNhap(java.time.LocalDate.parse("12/12/2025", fmt));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Adding 10 Trung...");
        manager.add(ing);

        System.out.println("After:");
        manager.search("Trung");
    }
}
