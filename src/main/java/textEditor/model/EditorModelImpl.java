package textEditor.model;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class EditorModelImpl extends Observable implements EditorModel, ObserverModel {
    private String textAreaString = "";

    @Override
    public void setTextAreaString(String value) throws RemoteException {
        textAreaString = value;
        System.out.print(textAreaString.charAt(textAreaString.length()));
        notifyObservers();
    }

    @Override
    public void addObserver(EditorModel editorModel) throws RemoteException {
        WrappedObserver wrappedObserver = new WrappedObserver(editorModel);
        addObserver(wrappedObserver);
        System.out.println("Obserwator został dodany milordzie! :)");
    }

    private class WrappedObserver implements Observer, Serializable {
        public WrappedObserver(EditorModel editorModel) {
            this.editorModel = editorModel;
        }

        private EditorModel editorModel;

        @Override
        public void update(Observable o, Object arg) {
            try {
                editorModel.setTextAreaString(o.toString() + " " + arg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}