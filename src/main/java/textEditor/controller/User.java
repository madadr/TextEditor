package textEditor.controller;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class User extends UnicastRemoteObject implements Serializable, Remote {
    private int id;
    private String username;

    public User() throws RemoteException {
        this.username = "";
    }

    public User(int id, String username) throws RemoteException {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
