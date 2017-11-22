package textEditor.model;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;

public class EditorModel extends Observable implements EditorModelService, ObserverService {
    private String textAreaString = "";

    @Override
    public void setTextAreaString(String value) throws RemoteException {
        textAreaString = value;
        System.out.print(textAreaString.charAt(textAreaString.length() - 1));
        notifyObservers();
    }

    public static void main(String[] args) {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(4321);

            EditorModel editorModel = new EditorModel();
            EditorModelService editorModelService = (EditorModelService) UnicastRemoteObject.exportObject(editorModel, 12345);
            ObserverService observerService = (ObserverService) editorModelService;

            registry.rebind("EditorModelService", editorModelService);
            registry.rebind("ObserverService", observerService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addObserver(EditorModelService editorModelService) throws RemoteException {
        WrappedObserver wrappedObserver = new WrappedObserver(editorModelService);
        addObserver(wrappedObserver);
        System.out.println("Obserwator został dodany milordzie! :)");
    }

    private class WrappedObserver implements Observer, Serializable {
        public WrappedObserver(EditorModelService editorModelService) {
            this.editorModelService = editorModelService;
        }

        private EditorModelService editorModelService;

        @Override
        public void update(Observable o, Object arg) {
            try {
                editorModelService.setTextAreaString(o.toString() + " " + arg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}