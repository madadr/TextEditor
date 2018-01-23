package textEditor.model.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObservable extends Remote {
    public enum UpdateTarget {
        ONLY_TEXT, ONLY_STYLE
    }

    void addObserver(RemoteObserver observer) throws RemoteException;

    void deleteObserver(RemoteObserver observer) throws RemoteException;

    void deleteObservers() throws RemoteException;

    void notifyObservers(UpdateTarget target) throws RemoteException;
}
