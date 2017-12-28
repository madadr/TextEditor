package textEditor.model;

import java.rmi.RemoteException;
import java.util.Observer;

public interface ObservableModel {
    void addObserver(Observer observer) throws RemoteException;
    void deleteObserver(Observer observer) throws RemoteException;
    void deleteObservers() throws RemoteException;
    void notifyObservers() throws RemoteException;
}
