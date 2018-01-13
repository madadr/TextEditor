package textEditor.model.interfaces;

import textEditor.model.StylesHolder;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote, RemoteObservable {
    void setTextString(String value) throws RemoteException;

    void setTextString(String value, RemoteObserver source) throws RemoteException;

    String getTextString() throws RemoteException;

    void setTextStyle(StylesHolder styleSpans) throws RemoteException;

    void setTextStyle(StylesHolder styleSpans, RemoteObserver source) throws RemoteException;

    StylesHolder getTextStyle() throws RemoteException;
}
