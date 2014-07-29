package field.core.execution;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.plugins.pseudo.PseudoPropertiesPlugin;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.launch.SystemProperties;

import java.util.List;

/**
 * used to parse a command line for executing loaded sheets automatically
 */
public
class AutoEngage {

    private final IVisualElement root;

    int delay = SystemProperties.getIntProperty("autoDelay", 0);

    public
    AutoEngage(IVisualElement root) {
        this.root = root;
    }

    public
    void start(final String spec) {
        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            int t = 0;

            public
            void update() {

                if (t == delay) {

                    String[] parts = spec.split(";");

                    for (String s : parts) {
                        //System.out.println(" looking up <" + s + ">");
                        startElement(s);
                    }
                    Launcher.getLauncher().deregisterUpdateable(this);
                }

                t++;

            }
        });

//		new Thread() {
//			long m = System.currentTimeMillis();
//
//			public void run() {
//
//				while (System.currentTimeMillis() - m < (1000 * 60 * 30L)) {
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					
//				}
//				System.err.println(" forcing exit ");
//				Runtime.getRuntime().halt(0);
//
//			};
//		}.start();

    }

    protected static
    void startElement(IVisualElement v) {

        //System.out.println(" :::::::::::::: starting <" + v + ">");
        PseudoPropertiesPlugin.begin.get(v).call(new Object[]{});

        // PythonScriptingSystem pss =
        // PythonScriptingSystem.pythonScriptingSystem.get(v);
        // iExecutesPromise executesPromise =
        // iExecutesPromise.promiseExecution.get(v);
        //
        // Promise promise = pss.promiseForKey(v);
        //
        // executesPromise.addActive(new iFloatProvider() {
        // public float evaluate() {
        // return 0;
        // }
        // }, promise);
    }

    protected
    void startElement(String s) {
        List<IVisualElement> found = StandardFluidSheet.findVisualElementWithNameExpression(root, s);
        for (IVisualElement v : found) {
            startElement(v);
        }
    }

}
