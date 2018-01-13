package textEditor.model.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProjectManager extends Remote {
    EditorModel getEditorModel(Project project) throws RemoteException;
    ActiveUserHandler getActiveUserHandler(ActiveUserHandler activeUserHandler) throws RemoteException;
}
