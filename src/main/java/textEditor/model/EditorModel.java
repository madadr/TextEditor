package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote
{
    void setTextAreaString(String value) throws RemoteException;
}
