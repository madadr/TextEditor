package textEditor.model;

import javafx.scene.control.TextField;
import textEditor.controller.Project;
import textEditor.controller.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface DatabaseModel extends Remote {
    void init() throws RemoteException;

    boolean userExist(String username) throws RemoteException;

    boolean checkPassword(String userName, String password) throws RemoteException;

    boolean registerUser(ArrayList<String> entryForm) throws RemoteException;

    List<Project> getProjects(User user) throws RemoteException;

    int getUserId(String login) throws RemoteException;

    void removeProject(Project projectToDelete) throws RemoteException;

    void addProject(Project project) throws RemoteException;
}
