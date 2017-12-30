package textEditor.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote, RemoteObservable {
    void setTextString(String value) throws RemoteException;

    String getTextString() throws RemoteException;

    void setTextStyle(StyleSpansWrapper styleSpans) throws RemoteException;

    StyleSpansWrapper getTextStyle() throws RemoteException;
}
