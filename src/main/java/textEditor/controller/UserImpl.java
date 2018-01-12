package textEditor.controller;

import java.io.Serializable;
import java.rmi.RemoteException;

public class UserImpl implements User, Serializable {
    private int id;
    private String username;

    public UserImpl() throws RemoteException {
        this.username = "";
    }

    public UserImpl(int id, String username) throws RemoteException {
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
        return this.username;
    }
}
