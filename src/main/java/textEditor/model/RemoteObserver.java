package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObserver extends Remote {
    void update(RemoteObservable observable) throws RemoteException;
    void update(RemoteObservable observable, RemoteObservable.UpdateTarget target) throws RemoteException;
}