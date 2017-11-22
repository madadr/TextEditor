package textEditor.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiService extends Remote {
    void addObserver(IRemoteObserver o) throws RemoteException;
}
