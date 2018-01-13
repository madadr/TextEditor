package textEditor.controller.targetInjections;

import textEditor.RMIClient;

public interface ClientInjectionTarget {
    void injectClient(RMIClient client);
}
