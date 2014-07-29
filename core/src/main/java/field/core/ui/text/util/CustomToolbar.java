package field.core.ui.text.util;

import field.core.dispatch.IVisualElement;
import field.core.execution.PythonInterface;
import field.core.plugins.python.PythonPlugin.CapturedEnvironment;
import field.core.plugins.python.PythonPluginEditor;
import field.launch.IUpdateable;
import field.util.collect.tuple.Pair;
import org.python.core.PyFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * helper for custom toolbar inside editor itself
 */
public
class CustomToolbar {

    public static
    Object add(String name, Object callableSomehow, IVisualElement to) {
        IUpdateable c = callable(callableSomehow);
        if (c != null) PythonPluginEditor.python_customToolbar.addToList(ArrayList.class,
                                                                         to,
                                                                         new Pair<String, IUpdateable>(name, c));
        return c;
    }

    public static
    void remove(Object o, IVisualElement from) {
        List<Pair<String, IUpdateable>> ll = PythonPluginEditor.python_customToolbar.get(from);
        if (ll != null) {
            for (Pair<String, IUpdateable> p : ll) {
                if (p.right == o) {
                    ll.remove(p);
                    return;
                }
            }
        }
    }

    private static
    IUpdateable callable(final Object callableSomehow) {
        final CapturedEnvironment env =
                (CapturedEnvironment) PythonInterface.getPythonInterface().getVariable("_environment");
        if (callableSomehow instanceof PyFunction) {
            return new IUpdateable() {
                public
                void update() {
                    if (env != null) {
                        env.enter();
                    }
                    try {
                        ((PyFunction) callableSomehow).__call__();
                    } finally {
                        if (env != null) {
                            env.exit();
                        }
                    }
                }
            };
        }
        if (callableSomehow instanceof IUpdateable) {
            return new IUpdateable() {

                public
                void update() {
                    ((IUpdateable) callableSomehow).update();
                }
            };
        }
        return null;
    }
}
