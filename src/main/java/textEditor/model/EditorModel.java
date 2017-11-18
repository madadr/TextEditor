package textEditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorModel {
    private String text;

    List<Consumer<String>> textObservers = new ArrayList<>();

    private int caretPosition;

    public String getText() {
        return text.toString();
    }

    public void setText(String text) {
        this.text = text;
        textObservers.forEach((st) -> {
            st.accept(text);
            System.out.println("new text: " + text);
        });
    }

    public void addTextObserver(Consumer<String> observer) {
        textObservers.add(observer);
    }

    public void removeTextObserver(Consumer<String> observer) {
        textObservers.remove(observer);
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
    }
}
