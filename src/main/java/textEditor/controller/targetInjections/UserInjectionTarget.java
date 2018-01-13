package textEditor.controller.targetInjections;

import textEditor.model.interfaces.User;

public interface UserInjectionTarget {
    void injectUser(User user);
}
