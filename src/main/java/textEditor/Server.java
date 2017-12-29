package textEditor;

import textEditor.model.DatabaseModel;
import textEditor.model.DatabaseModelImpl;
import textEditor.model.EditorModel;
import textEditor.model.EditorModelImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(4321);

            //Creating models implementation of our classes
            EditorModelImpl editorModelImpl = new EditorModelImpl();
            DatabaseModelImpl databaseModelImpl = new DatabaseModelImpl();

            //Exporting models interface to client
            EditorModel editorModel = (EditorModel) UnicastRemoteObject.exportObject(editorModelImpl, 0);
            DatabaseModel databaseModel = (DatabaseModel) UnicastRemoteObject.exportObject(databaseModelImpl, 0);

            //Binding names and models interfaces
            registry.rebind("EditorModel", editorModel);
            registry.rebind("DatabaseModel", databaseModel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
