package textEditor.model;


import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class StyleSpansWrapper implements Serializable {
    private ArrayList<Pair<Integer, ArrayList<String>>> pairsOfLengthAndStyle;
    private int stylesStart;

    public StyleSpansWrapper(int from, StyleSpans<Collection<String>> styleSpans) {
        this.stylesStart = from;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());

        for (StyleSpan<Collection<String>> span : styleSpans) {
            Pair<Integer, ArrayList<String>> lengthStylePair
                    = new Pair<>(span.getLength(), new ArrayList<>(span.getStyle()));

            this.pairsOfLengthAndStyle.add(lengthStylePair);
        }

        System.out.println(this.pairsOfLengthAndStyle);
    }

    public StyleSpans<Collection<String>> getStyleSpans() {
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>(pairsOfLengthAndStyle.size());

        for(Pair<Integer, ArrayList<String>> pair : pairsOfLengthAndStyle) {
            builder.add(pair.getValue(), pair.getKey());
        }

        return builder.create();
    }

    public int getStylesStart() {
        return this.stylesStart;
    }

    public void setStyleSpans(int from, StyleSpans<Collection<String>> styleSpans) {
        this.stylesStart = from;
        this.pairsOfLengthAndStyle = new ArrayList<>(styleSpans.length());

        for (StyleSpan<Collection<String>> span : styleSpans) {
            Pair<Integer, ArrayList<String>> lengthStylePair
                    = new Pair<>(span.getLength(), new ArrayList<>(span.getStyle()));

            this.pairsOfLengthAndStyle.add(lengthStylePair);
        }
    }
}