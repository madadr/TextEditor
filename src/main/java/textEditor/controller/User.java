package textEditor.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface User extends Remote {
    String getUsername() throws RemoteException;

    void setUsername(String username) throws RemoteException;

    int getId() throws RemoteException;

    void setId(int id) throws RemoteException;
}
