package workerTypes;

import base.Worker;
import contracts.ManagerHandler;
import controllers.*;
import enums.WorkerType;
import utils.*;

public class Manager extends Worker {
    ManagerHandler managerHandler = null;
    Displayer displayer = Displayer.getDisplayer();
    UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();

    public Manager(int id, String name, int age, String gender, String position, double salaries, String description) {
        super(id, name, age, gender,position, salaries, description);
        switch (WorkerType.fromPosition(position)) {
            case SUPPLY_MANAGER:
                managerHandler = SupplyManager.getManager();
                break;
            case WORKER_MANAGER:
                managerHandler = WorkerManager.getManager();
                break;
            case TABLE_MANAGER:;
                managerHandler = TableManager.getManager();
                break;
            default:
                managerHandler = TableManager.getManager();
                break;
        }
    }

    @Override 
    public void interact() {
                int GO_BACK_OPTION = 0;  
        String[] message = {
            "To go back enter 0",
            "Xin chao admin, toi co the giup gi cho ban"
        };
        String[] options = { 
            "Tao cho toi 1 bao cao", 
            "Hay tong quat cho toi ve cong viec cua ban",
            "Ban cam thay the nao ve cong viec nay"
        };
        while (inputHandler.getCurrentOption() != GO_BACK_OPTION) {
            displayer.clearScreen();

            displayer.displayMessage(message);
            displayer.displayOptions(options);

            inputHandler.getUserOption();

            switch (inputHandler.getCurrentOption()) {
                case 0: 
                    return;

                case 1:
                    managerHandler.createReport();
                    break;

                case 2:
                    System.out.println("Cong viec cua toi la quan ly: ");
                    managerHandler.showGeneralInfo();
                    break;

                case 3:
                    switch (WorkerType.fromPosition(this.getPosition())) {
                        case SUPPLY_MANAGER:
                            System.out.println("Dung noi cho ai biet la chef cua chung ta la ratatouille");
                            break;
                    
                        case WORKER_MANAGER:
                            System.out.println("Nhan vien toan an voi ngu, deo chiu lam viec 24h moi ngay");
                            break;

                        case TABLE_MANAGER:
                            System.out.println("Con nho nhin hoi chubby ay, lan truoc no an ca cai ban cua quan minh");
                            break;

                        default:
                            break;
                    }
                    break;
            
                default:
                    inputHandler.raiseWarning();
                    break;
            }
            displayer.singleSeperate();
            inputHandler.enter2Continue();
        }
    }

    @Override
    public void startWorking() {
        switch (WorkerType.fromPosition(this.getPosition())) {
            case SUPPLY_MANAGER:
                // Tạo báo cáo về nguồn cung hiện tại

                break;
            case TABLE_MANAGER:;
                // Bỏ các bàn trống vào danh sách cần phục vụ


                break;
            default:
                
                break;
        }
    }

    @Override
    public void stopWorking() {
        
    }
    

}
