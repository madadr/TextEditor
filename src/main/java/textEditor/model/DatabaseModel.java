package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DatabaseModel extends Remote {
    void update() throws RemoteException;
}
