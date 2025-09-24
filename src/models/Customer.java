package models;

import base.*;

/*
 * Không cần sửa đổi gì thêm ở đây,
 * Thứ chúng ta cần quan tâm là 1 bàn có bao nhiêu món được đặt, ko cần quan tâm ai đặt món
 * Chỉ gần đưa được món ăn tới bàn là nhiệm vụ đã hoàn thành, còn ai ăn thì ăn
 * Ví dụ là bàn 1 có 2 người gọi 5 món, chúng ta chỉ còn nấu 5 món rồi đưa đến bàn là xong,
 * ko cần quan tâm gì thêm
 */

public class Customer extends Human {

    public Customer(String name, int age, String gender) { super(name, age, gender); }

    @Override
    public void display() {
        System.out.println("Name: "+name+"\nAge: "+age+"\nGender: "+gender);
    }
}
