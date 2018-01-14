package textEditor.model.interfaces;

import textEditor.model.StylesHolder;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModelData extends Remote {
    String getText() throws RemoteException;

    void setText(String text) throws RemoteException;

    StylesHolder getStylesHolder() throws RemoteException;

    void setStylesHolder(StylesHolder stylesHolder) throws RemoteException;
}
