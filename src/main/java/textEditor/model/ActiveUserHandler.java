package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ActiveUserHandler extends Remote{
    void addUserToProject(int projectId, String userName) throws RemoteException;
    void removeUserToProject(int projectId, String userName) throws RemoteException;
    ArrayList<String> getActiveUserInProject(int projectId) throws RemoteException;
}
