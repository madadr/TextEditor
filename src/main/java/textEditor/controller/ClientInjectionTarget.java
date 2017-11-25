package textEditor.controller;

import textEditor.Client;

public interface ClientInjectionTarget {
    void injectClient(Client.RMIClient client);
}
