package base;

import models.Order;

public abstract class Worker extends Human {
    private final int id;
    private String position;
    private double salaries;
    private String description;
    private boolean isEmploy;
    
    public Worker(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(name, age, gender);
        this.id = id;
        this.position = position;
        this.salaries = salaries;
        this.description = description;
    }

    // Getters 
    public int getId() { return id; }
    public String getPosition() { return position; }
    public double getSalary() { return salaries; }
    public String getDescription() { return description; }
    public boolean isEmployed() { return isEmploy; }

    // Setters
    public void setPosition(String newPosition) { this.position = newPosition; }
    public void setSalary(double newSalaries) { this.salaries = newSalaries; }
    public void setEmploymentState(boolean isEmploy) { this.isEmploy = isEmploy; }

    @Override
    public void display() {
        String _position = getPosition();
        switch (_position.toLowerCase()) {
            case "waiter":
                _position = "Phuc vu";
                break;
            case "chef":
                _position = "Dau bep";
                break;
            case "supplymanager":
                _position = "Quan ly thuc pham";
                break;
            case "workermanager":
                _position = "Quan ly nhan vien";
                break;
            case "tablemanager":
                _position = "Quan ly ban";   
                break;
            default:
                break;
        }
        System.out.println("ID: "+id+"\nTen: "+name+"\nTuoi: "+age+"\nGioi Tinh: "+gender+"\nChuc vu: "+_position+"\nLuong: "+salaries+" dong");
        System.out.println("Mo ta: "+description);
    }
    public void shortDisplay() {
        System.out.printf("| %-4d | %-20s |\n", id, name);
    }
    public void gridDisplay() {
        String _position = getPosition();
        switch (_position.toLowerCase()) {
            case "waiter":
                _position = "Phuc vu";
                break;
            case "chef":
                _position = "Dau bep";
                break;
            case "supplymanager":
                _position = "Quan ly thuc pham";
                break;
            case "workermanager":
                _position = "Quan ly nhan vien";
                break;
            case "tablemanager":
                _position = "Quan ly ban";   
                break;
            default:
                break;
        }
        System.out.printf("| %-4d | %-20s | %-5d | %-10s | %-25s | %-30.1f |\n", id, name, age, gender, _position, salaries);
    }

    public abstract void interact();
    public abstract void startWorking(Order order);
    public abstract void stopWorking();
}