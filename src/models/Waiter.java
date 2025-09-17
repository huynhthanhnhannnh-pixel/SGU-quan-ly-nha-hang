package models;

public class Waiter extends base.Worker {
    
    public Waiter(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    public void startWorking() {
        System.out.println("Start working");
    }

    public void stopWorking() {
        System.out.println("Stop working");
    }
}
