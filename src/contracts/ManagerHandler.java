package contracts; 

import utils.UserInputHandler;

public interface ManagerHandler {
    int GO_BACK_OPTION = 0;
    UserInputHandler inputHandler = UserInputHandler.getUserInputHandler();
    public void showGeneralInfo();
    public void createReport();
    public void add(Object obj);
    public Object remove(Object objID);
    public Object search(Object objID);
}


