package textEditor.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EditorModel implements EditorModelService
{
    private String textAreaString = "";
    @Override
    public void setTextAreaString(String value) throws RemoteException
    {
        textAreaString+=value;
        System.out.println(textAreaString);
    }

    public static void main(String[] args)
    {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(4321);

            EditorModel editorModel = new EditorModel();
            EditorModelService editorModelService = (EditorModelService) UnicastRemoteObject.exportObject(editorModel,0);

            registry.rebind("EditorModelService", editorModelService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}