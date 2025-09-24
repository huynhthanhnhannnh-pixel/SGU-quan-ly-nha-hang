package models;

public class Ingredient {
    private final String name;
    private int quantity = 0; // Default quantity is 0

    // Constructor
    public Ingredient(String name) {
        this.name = name;
    }

    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    // Settets

    // Method to increase quantity
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }

    // Method to decrease quantity
    public int decreaseQuantity(int amount) {
        if (amount <= 0) {
            return 0; // Invalid request
        }

        if (quantity >= amount) {
            quantity -= amount;
            return amount;
        } else {
            int available = quantity;
            quantity = 0;
            return available; // Return what was available
        }
    }
}
