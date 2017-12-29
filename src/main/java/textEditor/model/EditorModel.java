package textEditor.model;

import org.fxmisc.richtext.model.StyleSpans;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote, RemoteObservable {
    void setTextString(String value) throws RemoteException;

    String getTextString() throws RemoteException;

    void setTextStyle(int from, StyleSpans<String> styleSpans) throws RemoteException;

    StyleSpans<String> getTextStyle() throws RemoteException;
}
