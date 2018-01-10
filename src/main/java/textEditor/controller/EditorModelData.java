package textEditor.controller;

import textEditor.model.StyleSpansWrapper;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EditorModelData extends Remote {
    String getText() throws RemoteException;

    void setText(String text) throws RemoteException;

    StyleSpansWrapper getStyleSpans() throws RemoteException;

    void setStyleSpans(StyleSpansWrapper styleSpans) throws RemoteException;
}
