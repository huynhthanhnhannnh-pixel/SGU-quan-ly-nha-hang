package base;

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
        System.out.println("ID: "+id+"\nName: "+name+"\nAge: "+age+"\nGender: "+gender+"\nPosition: "+position+"\nSalaries: "+salaries+"$");
        System.out.println("Description: "+description);
    }
    public void shortDisplay() {
        System.out.println(id+" / "+name);
    }

    public abstract void interact();
    public abstract void startWorking();
    public abstract void stopWorking();
}