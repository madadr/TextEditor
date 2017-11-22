package textEditor.model;

import java.util.Observer;

public class EditorModel extends java.util.Observable {
    private boolean stopped = false;

    private String text = "";

    private int caretPosition;
    public EditorModel() {
        // simulation of editing text by another user?
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;

//        for (Consumer<String> observer : textObservers) {
//            observer.accept(text);
//        }

        System.out.println("new text: " + text);
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }
}
