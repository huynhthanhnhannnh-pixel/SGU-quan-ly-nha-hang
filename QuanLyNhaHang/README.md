ý nghĩa tên các file:
base - chứa các base class để kế thừa, class này có thể là abstract hoặc không
constracts - chứa các interface
controller - các class được sử dụng để thực hiện các event
main - chứa chương trình chính
models - các class kế thừ từ base hoặc contracts
resources - các tài nguyên của chương trình, đóng vai trò như một database thay thế sql
utils - thư viện hộ trợ

-----------------------------------------------------------------------------------------------------------

chương trình hiện có 2 loại nhân viên, các nhân viên được kế thừa từ abstract class Worker

mỗi Worker có 2 hàm chính là:
public abstract void startWorking();
public abstract void stopWorking();

waiter và chef sẽ implement cả 2 hàm theo cách riêng 

-----------------------------------------------------------------------------------------------------------

các class manager trong controllers implement interface ManagerHandler.
SupplyManager - quản lý nguồn cung => kiểm tra số lượng, mua nguyên liệu,
TableManager - quản lý bàn cho khách => chuẩn bị bàn, lấy order, phục vụ order
WorkerManager - quản lý nhân viên => thuê, sa thải, quản lý thời gian làm việc. Ca làm việc là class Shift

-----------------------------------------------------------------------------------------------------------

Class Displayer trong utils dùng để hộ trợ in ra màng hình hoặc clearScreen