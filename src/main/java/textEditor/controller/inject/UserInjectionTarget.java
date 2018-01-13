package textEditor.controller.inject;

import textEditor.model.interfaces.User;

public interface UserInjectionTarget {
    void injectUser(User user);
}
