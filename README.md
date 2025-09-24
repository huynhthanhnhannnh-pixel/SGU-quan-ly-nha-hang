ý nghĩa tên các file:
base - chứa các base class để kế thừa, class này có thể là abstract hoặc không
constracts - chứa các interface
controller - các class được sử dụng để thực hiện các event
main - chứa chương trình chính
models - các class chứ các object phụ
enums - Thư viện các hằng số
workerTypes - folder chứa các class nhân viên của nhà hàng
resources - các tài nguyên của chương trình, đóng vai trò như một database thay thế sql
utils - thư viện hộ trợ

-----------------------------------------------------------------------------------------------------------

NOTE:

-----------------------------------------------
Kiến thức cần nhớ:

1. Enum 
Enum là thư viện các hằng số
giả sử có 2 file A và B, ta cần so sánh 1 chuỗi "SoSanh", thông thường ta sẽ tạo 1 biến String CONSTANT = "SoSanh".
Nhưng nếu giả sử chúng ta có 100 files thì sao?. nếu chúng ta ghi sai chuỗi thành "sosanh" thì sao?
Enum giải quyết vấn đề này bằng cách tạo ra một nhóm các hằng số để dùng chung cho các file, đảm bảo tính thống nhất
và toàn vẹn của chương trình

Tạo src/enums/WorkerType.java để quản lý các chức vụ của nhân viên trong nhà hàng

2. Đóng gói
Ví dụ 1:

````
Animal {
    makeSound(); -- kêu
}    

Dog extends Animal {
    makeSound(); -- kêu
    catchBall(); -- bắt bóng
}

Cat  extends Animal {
    makeSound(); -- kêu
    playWithToy(); -- chơi đồ chơi
}

void main() {
    Animal dog = new Dog();
    Animal cat = new Cat();

// Vì cat và dog là kiểu dữ liệu Animal, nên chúng ta chỉ biết là Animal có hàm makeSound()
// Đây gọi là đóng gói, mặc dù dog được tạo ra từ Dog() nhưng chúng ta chỉ biết nó là Animal 
// ko phải là Dog vì chúng ta đã định nghĩa nó là kiểu Animal

dog.makeSound(); -- đúng
cat.makeSound(); -- đúng

dog.catchBall(); -- báo lỗi
cat.playWithToy(); -- báo lỗi

// Nếu muốn sử dụng hàm playWithToy hay catchBall thì phải gán trực tiếp kiểu dữ liệu Dog và Cat

Dog dog = new Dog();
Cat cat = new Cat();

dog.catchBall(); -- đúng
cat.playWithToy(); -- đúng
````
Giả sử chúng ta cần in ra màn hình tiếng kêu của các con vật

````
// Giải pháp 1
List<Animal> list1 = new List<Aniaml>;

for(Animal animal : list1) {
    animal.makeSound();
}
````
````
// Giải pháp 2
List<Dog> dogList = new List<Dog>;
List<Cat> catList = new List<Cat>;

for(Dog dog : list1) {
    dog.makeSound();
}

for(Cat cat : list1) {
    cat.makeSound();
}

// Giờ hãy tưởng tượng chúng ta có 100 con vật, giải pháp 2 sẽ là một đống hỗi độn và không tối ưu
````
!!! Giải thích về class src/base/Worker.java và src/workerTypes.*

Giả sử ta có
````
Chef.Nau();
Waiter.PhucVu();
Manager.QuanLy();

List<Chef> chefList = new List<Chef>;
List<Waiter> waiterList = new List<Waiter>;
List<Manager> managerList = new List<Manager>;

// Bắt đầu làm việc

for(Chef chef : chefList) {
    chef.Nau(); -- nấu món ABC, XYZ
}

for(Waiter waiter : waiterList) {
    waiter.PhucVu(); -- lấy order bàn 1, 2, 3
}

for(Manager manager : managerList) {
    manager.QuanLy(); -- Sa thải nhân viên A, thuê a B, viết báo cáo về chị C
}
````
Giải pháp
Hãy Để cho Chef, Waiter, Manager kế thừa từ class Worker
````
for(Worker worker : workerList) {
    worker.StartWorking(); -- hoạt động tương tự nhưng ví dụ trên nhưng tối ưu và hiệu quả hơn
}
````
!!! Vấn đề mới

Nếu chúng ta muốn truy cập trực tiếp đối tượng manager thì sao, nói cách khác là chúng ta muốn
Manager manager = new Manager(); Thay vì Worker worker = new Manager();

vấn đề này giống như việc chúng ta muốn try cập hàm catchBall() và playWithToy() ở ví dụ trước

Giải pháp: Theo tính đóng gói chúng ta chỉ biết là 1 worker có các hàm:
interact(), startWorking(), stopWorking(), // (Đọc file src/base/Worker.java).
Chúng ta biết worker có hàm interact nhưng chúng ta có thể định nghĩa cách mà hàm interact hoạt động

giả sử ta muốn thêm vào class manager 2 hàm mới 
DiNhau() -- đi nhậu
ChoiPickerBall() -- vờn em pickerball

Nhưng chúng ta không muốn tạo thêm 1 List mới để chứa manager, List<Manager>
Vậy hãy "gọi"(gọi hàm ko phải khởi tạo hàm) 2 hàm trên trong interact()
````
public void DiNhau() {
    print("1, 2, 3, ZO");
}
public void ChoiPickerBall() {
    print("PICKERBALL");
}

public void interact() {
    int option = getUserOption() -- lấy lựa chọn từ người dùng

    switch (option) {
        case 1:
            DiNhau(); -- "1, 2, 3, ZO"
            break;
        case 2:
            ChoiPickerBall(); -- "PICKERBALL"
            break;
        default:
            break;
    }
}
````
Vậy chúng ta chỉ cần gọi worker.interact() là xong

Hết.
----------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------

!!! đối với SupplyManager, TableManager, WorkerManager, các class này mặc định là singleton,
tuyệt đối không được đụng vào Constructor() và hàm getManager() của nó, nếu thấy dòng 
// Private constructor to enforce singleton
// Public method to get the single self 
thì đừng đụng vào nó

---------------------
Yêu cầu chương trình hiện tại

Tip: bấm cntrl + k, 2 để collapse, xem các hàm cho nhanh, "bấm cntrl rồi bấm k, tiếp tục giữ cntrl rồi bấm 2"
 
!!!đối với nhiệm vụ chưa xong thì   
--------------------- X ---------------------          
còn nhiệm vụ đã xong thì                
--------------------- V ---------------------             
         
-------------------------------------------------- Start here           
Bắt đầu làm 
 
--------------------- X ---------------------                  
src/models/Dish.java                         
src/resources/Dishes.txt                         

Nhiệm vụ 

1. Cập nhật class Dish.java Dish(món ăn) được hiểu là 1 danh sách các nguyên liệu và số lượng nguyên liệu,
tạo biến private name(tên món ăn) cho nó, tạo hàm getter, setter và constructor
tạo 1 hashMap để chứa tên nguyên liệu và số lượng của chúng HashMap<String, int>

2. Tạo 2 hàm addIngredient(Ingredient ingredient) và removeIngredient(Ingredient ingredient)

3. Tạo 1 hàm public double getPrice() => trả về tổng tiền của tất cả các nguyên liệu rồi x3 lên

4. Tạo 1 hàm public HashMap<String, int> readIngredients() để trả về thông tin của các nguyên liệu dùng trong món ăn

5. Trong file Dishes.txt thiết lập format(định dạng) {DishName ingredient6 ingredient29 ...}
hiểu là {"Tên món ăn" "nguyên liệu A | số lượng" "nguyên liệu B | số lượng" ...} nhập định dạng này trên mỗi hàng

--------------------- X ---------------------                        
src/models/Ingredient.java                                                      
src/resources/Ingredients.txt                                                            

Nhiệm vụ

1. Bổ sung thêm thuộc tính cost(giá tiền) vào class Ingredient, cost sẽ là private, tạo 
getter và setter cho nó, sửa đổi hoặc bổ sung constructor cho Ingredient(String name, double cost);

2. Sử đổi file Ingredients.txt thành format(định dạng) sau {Name Cost} nhập định dạng này trên mỗi hàng
định dạng hiện tại của file là {Name}

--------------------- X ---------------------                                                                 
src/models/Order.java                                             

Nhiệm vụ

1. Order có 1 danh sách gồm tên các món đã gọi, tạo 1 danh sách để chứa tên các món ăn, đặt tên là "dishes",
tên của món ăn có thể đc lặp lại, nhưng không được trùng với bất kỳ tên nào trong "excludedDishes"

2. Order có 1 tham chiếu đến bàn mà order này được lấy, "Table table" để chứa bàn, tạo getter/setter cho nó

3. Order gồm 1 danh sách gồm các món không được gọi, hiểu là sau khi đầu bếp(chef) kiểm tra và thấy không 
thể nấu món A, B, C thì tên các món đó phải được ghi vào danh sách này, đặt tên là "excludedDishes"

4. Tạo 1 biến tên là private numOfUnsatisfiedRequest(số lượng order cần phải lấy lại),
tạo getter/setter cho nó

5. Tạo 1 hàm tên là writeOrder(String dishName) để ghi lại tên món ăn của khách hàng

6. Tạo 1 hàm tên là addExcludedDish(String dishName) để thêm vào tên món ăn không được order

7. Tạo 1 hàm tên là updateOrder(), sau khi đã cập nhật các món không đc order bằng hàm addExcludedDish(String dishName)
thì gọi hàm updateOrder() để xóa các món ăn khỏi danh sách "dishes", với mỗi món được xóa thì +1 vào numOfUnsatisfiedRequest

8. Tạo 3 trạng thái mới cho Order là NEW, UNFINISHED, và COMPLETED, tạo 1 file enum mới trong mục src/enums

9. Tạo hàm getState để kiểm tra trạng thái của Order

--------------------- X ---------------------                    
src/models/Table.java                          

Nhiệm vụ

!!! đọc file src/models/Customer.java trước khi tiếp tục

1. Viết hàm String[] getOrder(), 
hàm get order này sẽ trả về danh sách tên các món ăn mà bàn này đã gọi

2. Viết String[] getOrder(int numOfOrder)
nếu chef ko nấu được món do hết nguyên liệu thì sẽ kêu waiter sử dụng hàm này để lấy order mới,
numOfOrder là số order không nấu được

3. Viết hàm display(), sau khi khách đã đặt bàn thì có thể sử dụng hàm này để xem thông tin các
khách hàng, món ăn trong bàn này, và khích thước bàn

--------------------- X ---------------------                        
src/controllers/TableManager.java                            

Nhiệm vụ

1. Viết hàm loadTables(int numberOfTable) => khởi tạo n bàn cho nhà hàng

2. Viết hàm prepareTable(int tableID), hàm này sẽ reset bàn ăn Table.reset() sau đó thì bắt đầu xếp 
khách vào bàn Table.addCustomer(Customer customer), hiện tại khách hàng vẫn chưa có data để khởi tạo
nên coi hàm này là 1 demo

2.1 (Tùy chọn) nếu thích thì tạo 1 file Customers.txt trong resources rồi đọc file đó, sau đó tạo 1 hàm
customerRGN để tạo khách hàng ngẫu nhiên

3. Tạo hàm getTableWithID(int tableID) => lấy bàn bằng id của bàn

4. Tạo hàm getEmptyTables() => trả về tất cả các bàn đang trống

--------------------- X ---------------------                      
src/controllers/SupplyManager.java                            

Nhiệm vụ

1. Sửa lại hàm loadIngredientsFromFile trong SupplyManager.java để nó đọc thêm giá tiền của nguyên liệu

2. Ở hàm loadDishesFromFile() trong SupplyManager hay viết 1 đoạn code để đọc các món ăn trong file Dishes.txt,
với mỗi hàng trong file, tạo 1 object Dish(món ăn), với mỗi nguyên liệu trong
món ăn thì gọi Dish.addIngredient() để bỏ nguyên liệu vào món ăn, rồi lưu nó vào mảng "List<Dish> menu"

3. Viết hàm getMenu(), hàm này sẽ trả về một mảng String gồm tên của tất cả các món trong menu, 
hàm này sẽ được sử dụng trong class src/models/Table.java Hiểu là mỗi bàn sẽ có 1 tờ menu 
cho khách xem rồi chọn món

4. Viết hàm addIngredient

--------------------- X ---------------------                          
src/models/Waiter.java                            

Nhiệm vụ

Ở hàm startWorking(), 
waiter sẽ gọi EventHandler.getTable(), nếu null thì nghỉ, còn không thì gọi Table.getOrder()
Nhân viên cần kiểm tra xem order là mới hay là order cần lấy lại 
dùng hàm getgetState để kiểm tra trạng thái,

nếu là NEW thì dự vào số khách hàng của bàn Order.getTable().getNumOfCustomer() 
nếu là UNFINISHED thì kiểm tra Order.getNumOfUnsatisfiedRequest()
nếu là FINISHED thì thanh toán hóa đơn rồi gọi notifyTableManager() để thông báo cho quản lý là bàn
đã phục vụ xong
(tạm thời cứ ghi là print("da thanh toan"), nếu muốn tạo danh thu thì nhắn zalo cho tui sau)

Đối với NEW và UNFINISHED thì phải dùng Order.writeOrder(String dishName) trong for loop để ghi Order
sau đó EventHandler.addOrder() để add order trở lại danh sách rồi gọi EventHandler.notifyChef() để kêu các 
chef bắt đầu làm việc

Note: dựa vào logic bên dưới thì Nhân viên sẽ được tự động thông báo từ các chef(đây là ứng dụng của EventHandler)

--------------------- X ---------------------                          
src/models/Chef.java                                            

Nhiệm vụ

Ở hàm startWorking()

1. chef sẽ lấy order bằng cách gọi EventHandler.getOrder(), nếu order là null(ko có order) thì nghỉ,
nếu có thì bắt đầu nấu

2. Sử dụng for loop để đọc từng món ăn có trong order Với mỗi món ăn thì
Sử dụng SupplyManager.checkIngredients(String dishName) để xem có đủ nguyên liệu không,
sau khi kiểm tra xong toàn bộ thì

- nếu đủ thì dùng vòng lặp để lấy từng nguyên liệu ra khỏi kho, sử dụng hàm 
SupplyManager.getIngredient(String name, int amount) sau đó tạo 1 object Dish rồi kêu waiter gửi về bàn

- nếu không thì gọi Order.addExcludedDish(String dishName), sau khi add xong hết rồi thì gọi
Order.updateOrder(), sau đó thì EventHandler.addUnfinishedTable(Order.getTable()) rồi cuối cùng
thì kêu nhân viên ra lấy lại order EventHandler.notifyWaiter()

Note: dựa vào logic bên trên thì Chef sẽ được tự động thông báo từ các waiter(đây là ứng dụng của EventHandler)

--------------------- X ---------------------                                   
1. Đối với nhân viên quản lý là TableManager thì phải
Kiểm tra các bàn đang trống sau đó bỏ vào danh sách bàn cần được phục vụ
sử dụng hàm TableManager.getEmptyTables() để lấy danh sách các bàn đang trống, sau đó gọi
EventHandler.addTable(Table table) để bỏ các bàn vào danh sách cần được phục vụ

2. Đối với nhân viên quản lý là SupplyManager thì phải thông báo còn bao nhiêu nguyên liệu

trong kho sau mỗi ca làm

