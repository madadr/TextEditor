package textEditor.model.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ActiveUserHandler extends Remote {
    void addUserToProject(int projectId, String userName) throws RemoteException;

    void removeUserFromProject(int projectId, String userName) throws RemoteException;

    ArrayList<String> getActiveUserInProject(int projectId) throws RemoteException;
}
