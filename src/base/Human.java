package base;

public abstract class Human {
    protected String name;
    protected int age;
    protected String gender;

    public Human(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters 
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }

    // Setters
    public void setName(String newName) { name = newName; }
    public void setGender(String newGender) { gender = newGender; }
    public void setAge(int newAge) { age = newAge; }

    public abstract void display();
}
