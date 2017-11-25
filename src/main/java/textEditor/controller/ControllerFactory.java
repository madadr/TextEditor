package textEditor.controller;

import javafx.util.Callback;
import textEditor.RMIClient;

public class ControllerFactory implements Callback<Class<?>, Object> {
    private RMIClient rmiClient;
    private WindowSwitcher switcher;

    public ControllerFactory(RMIClient rmiClient, WindowSwitcher switcher) {
        this.rmiClient = rmiClient;
        this.switcher = switcher;
    }

    @Override
    public Object call(Class<?> p) {
        Object controller = null;
        try {
            controller = p.newInstance();

            if (controller instanceof ClientInjectionTarget) {
                ((ClientInjectionTarget) controller).injectClient(rmiClient);
            }

            if(controller instanceof WindowSwitcherInjectionTarget) {
                ((WindowSwitcherInjectionTarget) controller).injectWindowSwitcher(switcher);
            }

            return controller;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
