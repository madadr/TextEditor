package textEditor.controller;

import javafx.util.Callback;
import textEditor.RMIClient;
import textEditor.view.WindowSwitcher;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private RMIClient rmiClient;
    private WindowSwitcher switcher;
    private UserImpl user;

    public ControllerFactory(RMIClient rmiClient, WindowSwitcher switcher, UserImpl user) {
        this.rmiClient = rmiClient;
        this.switcher = switcher;
        this.user = user;
    }

    @Override
    public Object call(Class<?> p) {
        Object controller = null;
        try {
            controller = p.newInstance();

            if (controller instanceof ClientInjectionTarget) {
                ((ClientInjectionTarget) controller).injectClient(rmiClient);
            }

            if (controller instanceof WindowSwitcherInjectionTarget) {
                ((WindowSwitcherInjectionTarget) controller).injectWindowSwitcher(switcher);
            }

            if (controller instanceof UserInjectionTarget) {
                ((UserInjectionTarget) controller).injectUser(user);
            }

            return controller;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
