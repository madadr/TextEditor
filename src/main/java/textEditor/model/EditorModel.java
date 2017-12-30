package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote, RemoteObservable {
    void setTextString(String value) throws RemoteException;

    void setTextString(String value, RemoteObserver source) throws RemoteException;

    String getTextString() throws RemoteException;

    void setTextStyle(StyleSpansWrapper styleSpans) throws RemoteException;

    void setTextStyle(StyleSpansWrapper styleSpans, RemoteObserver source) throws RemoteException;

    StyleSpansWrapper getTextStyle() throws RemoteException;
}
