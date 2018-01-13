package textEditor.controller.inject;

import textEditor.utils.RMIClient;

public interface ClientInjectionTarget {
    void injectClient(RMIClient client);
}
