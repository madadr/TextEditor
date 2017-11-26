package textEditor.controller;

import textEditor.RMIClient;

public interface ClientInjectionTarget {
    void injectClient(RMIClient client);
}
