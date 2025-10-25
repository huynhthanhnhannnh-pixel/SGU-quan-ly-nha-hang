package contracts; 

import utils.UserInputHandler;

public interface ManagerHandler<T> {
    int GO_BACK_OPTION = 0;
    UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    public void showGeneralInfo();
    public void createReport();
    public void add(T obj);
    public T remove(T objID);
    public T search(T objID);

    public void loadFromFile(Runnable func);
    public void saveToFile(Runnable func);
    public T Input();
}


