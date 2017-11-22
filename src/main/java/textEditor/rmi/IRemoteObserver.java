package textEditor.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteObserver extends Remote {
    // observable = EditorModel
    // text = EditorModel text
    void update(Object observable, Object text) throws RemoteException;
    void update2(Object text) throws RemoteException;
}
