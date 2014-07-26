package field.core.ui.text.util;

import field.core.dispatch.iVisualElement;
import field.core.execution.PythonInterface;
import field.core.plugins.python.PythonPlugin.CapturedEnvironment;
import field.core.plugins.python.PythonPluginEditor;
import field.launch.iUpdateable;
import field.namespace.generic.tuple.Pair;
import org.python.core.PyFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * helper for custom toolbar inside editor itself
 */
public class CustomToolbar {

	public static
    Object add(String name, Object callableSomehow, iVisualElement to) {
		iUpdateable c = callable(callableSomehow);
		if (c != null)
			PythonPluginEditor.python_customToolbar.addToList(ArrayList.class, to, new Pair<String, iUpdateable>(name, c));
		return c;
	}

	public static
    void remove(Object o, iVisualElement from) {
		List<Pair<String, iUpdateable>> ll = PythonPluginEditor.python_customToolbar.get(from);
		if (ll != null) {
			for (Pair<String, iUpdateable> p : ll) {
				if (p.right == o) {
					ll.remove(p);
					return;
				}
			}
		}
	}

	private static iUpdateable callable(final Object callableSomehow) {
		final CapturedEnvironment env = (CapturedEnvironment) PythonInterface.getPythonInterface().getVariable("_environment");
		if (callableSomehow instanceof PyFunction) {
			return new iUpdateable() {
				public void update() {
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
		if (callableSomehow instanceof iUpdateable) {
			return new iUpdateable() {

				public void update() {
					((iUpdateable) callableSomehow).update();
				}
			};
		}
		return null;
	}
}
