package textEditor.model;

import textEditor.controller.Project;
import textEditor.controller.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface DatabaseModel extends Remote {
    boolean userExist(String username) throws RemoteException;

    boolean checkPassword(String userName, String password) throws RemoteException;

    boolean registerUser(ArrayList<String> entryForm) throws RemoteException;

    List<Project> getProjects(User user) throws RemoteException;

    int getUserId(String login) throws RemoteException;

    String getUserLogin(int id) throws RemoteException;

    void removeProject(Project projectToDelete) throws RemoteException;

    void addProject(Project project) throws RemoteException;

    List<User> getFriends(User user) throws RemoteException;

    void addFriend(User user, User friend) throws RemoteException;

    void removeFriend(User user, User friend) throws RemoteException;
}
