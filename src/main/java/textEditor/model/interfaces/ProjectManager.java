package textEditor.model.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProjectManager extends Remote {
    String getEditorModelId(Project project) throws RemoteException;

    String getActiveUserHandlerId(Project project) throws RemoteException;
}
