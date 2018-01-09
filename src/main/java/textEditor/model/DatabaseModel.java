package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DatabaseModel extends Remote {
    void init() throws RemoteException;

    boolean userExist(String username) throws RemoteException;

    boolean checkPassword(String userName, String password) throws RemoteException;

    boolean registerUser(String login, String s, String email, String zipCode, String address, String region, String userName, String password) throws RemoteException;
}