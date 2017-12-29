package textEditor.model;

import java.rmi.RemoteException;

public interface RemoteObservable {
    void addObserver(RemoteObserver observer) throws RemoteException;

    void deleteObserver(RemoteObserver observer) throws RemoteException;

    void deleteObservers() throws RemoteException;

    // TODO: add to notifyObservers argument with enum to determine if text was updated or text style
    void notifyObservers() throws RemoteException;
}
