package textEditor.model;


import org.fxmisc.richtext.model.StyleSpans;

import java.io.Serializable;
import java.util.Collection;

public class StyleSpansWrapper implements Serializable {
    // TODO: replace StyleSpans<Collection<String>> class with another Serializable object
    StyleSpans<Collection<String>> styleSpans;

    public StyleSpansWrapper(StyleSpans<Collection<String>> styleSpans) {
        this.styleSpans = styleSpans;
    }

    public StyleSpans<Collection<String>> getStyleSpans() {
        return styleSpans;
    }

    public void setStyleSpans(StyleSpans<Collection<String>> styleSpans) {
        this.styleSpans = styleSpans;
    }
}