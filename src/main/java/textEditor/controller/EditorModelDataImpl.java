package textEditor.controller;

import textEditor.model.StyleSpansWrapper;

import java.io.Serializable;
import java.rmi.RemoteException;

public class EditorModelDataImpl implements Serializable, EditorModelData {
    private String text = "";
    private StyleSpansWrapper styleSpans;

    public EditorModelDataImpl(String text, StyleSpansWrapper styleSpans) {
        this.text = text;
        this.styleSpans = styleSpans;
    }

    @Override
    public String getText() throws RemoteException {
        return text;
    }

    @Override
    public void setText(String text) throws RemoteException {
        this.text = text;
    }

    @Override
    public StyleSpansWrapper getStyleSpans() throws RemoteException {
        return styleSpans;
    }

    @Override
    public void setStyleSpans(StyleSpansWrapper styleSpans) throws RemoteException {
        this.styleSpans = styleSpans;
    }

    @Override
    public String toString() {
        return "EditorModelDataImpl{" +
                "text='" + text + '\'' +
                ", styleSpans=" + styleSpans +
                '}';
    }
}
