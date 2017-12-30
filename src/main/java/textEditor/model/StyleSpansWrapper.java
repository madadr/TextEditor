package textEditor.model;


import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class StyleSpansWrapper implements Serializable {
    // TODO: replace StyleSpans<Collection<String>> class with another Serializable object
//    private StyleSpans<Collection<String>> styleSpans;
    private ArrayList<Pair<Integer, ArrayList<String>>> pairsOfLengthAndStyle;

    public StyleSpansWrapper(StyleSpans<Collection<String>> styleSpans) {
//        this.styleSpans = styleSpans;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());

        for (StyleSpan<Collection<String>> span : styleSpans) {
            Pair<Integer, ArrayList<String>> lengthStylePair
                    = new Pair<>(span.getLength(), new ArrayList<>(span.getStyle()));

            this.pairsOfLengthAndStyle.add(lengthStylePair);
        }

        System.out.println(this.pairsOfLengthAndStyle);
    }

    public StyleSpans<Collection<String>> getStyleSpans() {
        // TODO: reconstruct StyleSpans
        return null;
    }

    public void setStyleSpans(StyleSpans<Collection<String>> styleSpans) {
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());

        for (StyleSpan<Collection<String>> span : styleSpans) {
            Pair<Integer, ArrayList<String>> lengthStylePair
                    = new Pair<>(span.getLength(), new ArrayList<>(span.getStyle()));

            this.pairsOfLengthAndStyle.add(lengthStylePair);
        }
    }
}