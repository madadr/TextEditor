package textEditor.model.interfaces;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProjectManager extends Remote {
    String getEditorModelId(Project project) throws RemoteException;

    String getActiveUserHandlerId(Project project) throws RemoteException;

    void saveProject(Project project) throws RemoteException;

    void saveProject(Project project, EditorModelData data) throws RemoteException;

    EditorModelData getEditorModelData(File modelFile) throws RemoteException;
}
