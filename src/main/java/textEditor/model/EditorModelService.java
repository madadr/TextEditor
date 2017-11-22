package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModelService extends Remote
{
    void setTextAreaString(String value) throws RemoteException;
}
