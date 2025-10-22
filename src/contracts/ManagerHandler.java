package contracts; 

public interface ManagerHandler {
    public void showGeneralInfo();
    public void createReport();
    public void add(Object obj);
    public Object remove(int objID);
    public Object search(Object objID);
}


