package textEditor.utils;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadOnlyBoolean implements Serializable {
    private AtomicBoolean atomicBoolean;

    public ReadOnlyBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
    }

    public boolean getValue() {
        return atomicBoolean.get();
    }
}