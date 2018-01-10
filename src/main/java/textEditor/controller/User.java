package textEditor.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface User extends Remote {
    public String getUsername() throws RemoteException;

    public void setUsername(String username) throws RemoteException;

    public int getId() throws RemoteException;

    public void setId(int id) throws RemoteException;
}
