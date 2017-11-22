package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverService extends Remote
{
    void addObserver(EditorModelService editorModelService) throws RemoteException;
}
