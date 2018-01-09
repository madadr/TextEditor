package textEditor.model;

import textEditor.controller.Project;
import textEditor.controller.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DatabaseModel extends Remote {
    void init() throws RemoteException;

    boolean userExist(String username) throws RemoteException;

    boolean checkPassword(String userName, String password) throws RemoteException;

    boolean registerUser(String login, String s, String email, String zipCode, String address, String region, String userName, String password) throws RemoteException;

    List<Project> getProjects(User user);

    int getUserId(String login);
}
