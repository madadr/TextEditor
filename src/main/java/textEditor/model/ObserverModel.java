package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverModel extends Remote
{
    void addObserver(EditorModel editorModel) throws RemoteException;
}
