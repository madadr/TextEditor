package textEditor.model.interfaces;

import textEditor.model.StylesHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

public interface EditorModel extends RemoteObservable {
    void setTextString(String value) throws RemoteException;

    void setTextString(String value, RemoteObserver source) throws RemoteException;

    String getTextString() throws RemoteException;

    void setTextStyle(StylesHolder styleSpans) throws RemoteException;

    void setTextStyle(StylesHolder styleSpans, RemoteObserver source) throws RemoteException;

    StylesHolder getTextStyle() throws RemoteException;

    EditorModelData getData() throws RemoteException;

//    void initObserverList() throws RemoteException;

    // TODO: find better place for that method
    static EditorModelData getEditorModelData(File modelFile) {
        ObjectInputStream ois = null;
        try {
            FileInputStream inputStream = new FileInputStream(modelFile);
            ois = new ObjectInputStream(inputStream);
            return (EditorModelData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed");
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
