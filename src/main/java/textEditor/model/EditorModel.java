package textEditor.model;

import org.fxmisc.richtext.model.StyleSpans;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModel extends Remote, ObservableModel {
    void setTextString(String value) throws RemoteException;
    void setTextStyle(int from, StyleSpans<String> styleSpans) throws RemoteException;
}
