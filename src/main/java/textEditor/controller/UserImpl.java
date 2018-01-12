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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserImpl user = (UserImpl) o;

        if (id != user.id) return false;
        return username != null ? username.equals(user.username) : user.username == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
