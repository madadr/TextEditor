package textEditor.model;

import java.rmi.RemoteException;

public interface RemoteObservable {
    void addObserver(RemoteObserver observer) throws RemoteException;

    void deleteObserver(RemoteObserver observer) throws RemoteException;

    // TODO: add overloaded deleteObservers with List<RemoteObserver> argument
    void deleteObservers() throws RemoteException;

    // TODO: add to notifyObservers argument with enum to determine if text was updated or text style
    void notifyObservers() throws RemoteException;
}
