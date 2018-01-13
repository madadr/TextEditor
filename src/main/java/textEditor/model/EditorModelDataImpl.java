package textEditor.model;

import textEditor.model.interfaces.EditorModelData;

import java.io.Serializable;
import java.rmi.RemoteException;

public class EditorModelDataImpl implements Serializable, EditorModelData {
    private String text = "";
    private StylesHolder stylesHolder;

    public EditorModelDataImpl() {
        this.text = "";
        this.stylesHolder = new StylesHolder();
    }

    public EditorModelDataImpl(String text, StylesHolder stylesHolder) {
        this.text = text;
        this.stylesHolder = stylesHolder;
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
    public StylesHolder getStylesHolder() {
        return stylesHolder;
    }

    @Override
    public void setStylesHolder(StylesHolder stylesHolder) {
        this.stylesHolder = stylesHolder;
    }
}
