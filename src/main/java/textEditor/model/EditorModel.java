package textEditor.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EditorModel implements ChangeListener {
    private boolean stopped = false;
    private String text = "";
    private final String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer ornare, ante ac convallis cursus, arcu erat sagittis neque, at bibendum nisl nulla eu eros. Aenean viverra, orci sed rutrum ullamcorper, nisi lorem finibus augue, non sollicitudin arcu sapien eget sapien. Nullam sapien ipsum, interdum ac neque vitae, lobortis volutpat dolor. Cras sollicitudin risus tincidunt elit cursus, at eleifend felis cursus. Etiam in varius risus. Donec convallis tortor at auctor sollicitudin. Phasellus nec venenatis lorem, in pharetra enim. Sed sagittis velit et feugiat vestibulum. Proin tortor elit, tristique venenatis elementum eget, iaculis non neque. Duis sed porttitor nibh, in cursus orci. In ac molestie sem. Curabitur auctor commodo congue. Morbi mollis aliquam lacus sed semper. Nam dapibus lobortis justo et tincidunt. In lectus massa, suscipit vel venenatis non, aliquam in lacus.";

    List<Consumer<String>> textObservers = new ArrayList<>();

    private int caretPosition;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public EditorModel() {
        // simulation of editing text by another user?
        executor.submit(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            while (!stopped) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                setText(text + getLoremIpsumChar());
            }
        });
    }

    private int loremIndex = 0;

    private String getLoremIpsumChar() {
        if(loremIndex < loremIpsum.length()) {
            return String.valueOf(loremIpsum.charAt(loremIndex++));
        } else {
            loremIndex = 0;
            return getLoremIpsumChar();
        }
    }

    public void shutdown() {
        stopped = true;
        executor.shutdownNow();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;

        for (Consumer<String> observer : textObservers) {
            observer.accept(text);
        }

        System.out.println("new text: " + text);
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

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        System.out.println((String) oldValue);
        System.out.println((String) newValue);
        setText((String) newValue);
    }
}
