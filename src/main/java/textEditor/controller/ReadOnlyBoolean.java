package textEditor.controller;


import java.util.concurrent.atomic.AtomicBoolean;

public class ReadOnlyBoolean {
    private AtomicBoolean atomicBoolean;

    public ReadOnlyBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
    }

    public boolean getValue() {
        return atomicBoolean.get();
    }
}