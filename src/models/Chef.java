package models;

public class Chef extends base.Worker {
    
    public Chef(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
    }

    public void startWorking() {
        System.out.println("Start working");
    }

    public void stopWorking() {
        System.out.println("Stop working");
    }
}
