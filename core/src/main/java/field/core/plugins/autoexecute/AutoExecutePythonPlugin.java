package field.core.plugins.autoexecute;

import field.bytecode.protect.Woven;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.dispatch.override.IVisualElementOverridesAdaptor;
import field.core.plugins.SimpleConstraints;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.iPlugin;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.launch.SystemProperties;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Pair;

import java.util.*;
import java.util.regex.Pattern;

@Woven
public
class AutoExecutePythonPlugin implements iPlugin {

    public
    class LocalVisualElement extends NodeImpl<IVisualElement> implements IVisualElement {

        public
        <T> void deleteProperty(VisualElementProperty<T> p) {
            properties.remove(p);
        }

        public
        void dispose() {
        }

        public
        Rect getFrame(Rect out) {
            return null;
        }

        public
        <T> T getProperty(VisualElementProperty<T> p) {
            if (p == overrides) return (T) elementOverride;
            Object o = properties.get(p);
            return (T) o;
        }

        public
        String getUniqueID() {
            return pluginId;
        }

        public
        Map<Object, Object> payload() {
            return properties;
        }

        public
        void setFrame(Rect out) {
        }

        public
        IMutableContainer<Map<Object, Object>, IVisualElement> setPayload(Map<Object, Object> t) {
            properties = t;
            return this;
        }

        public
        <T> IVisualElement setProperty(VisualElementProperty<T> p, T to) {
            properties.put(p, to);
            return this;
        }

        public
        void setUniqueID(String uid) {
        }
    }

    public
    class Overrides extends IVisualElementOverridesAdaptor {

        @Override
        public
        TraversalHint added(IVisualElement newSource) {

            //System.out.println(" added <" + newSource + ">");

            check(newSource);
            return StandardTraversalHint.CONTINUE;
        }

    }

    public static final VisualElementProperty<String> python_autoExec =
            new VisualElementProperty<String>("python_autoExec_v");
    public static final VisualElementProperty<Integer> autoExecuteDelay =
            new VisualElementProperty<Integer>("autoExecuteDelay");
    public static final VisualElementProperty<Integer> autoExecuteDelayedFor =
            new VisualElementProperty<Integer>("autoExecuteDelayedFor_");

    public static final String pluginId = "//AudoExecutePython";

    private IVisualElement root;

    private SimpleConstraints simpleConstraintsPlugin;

    private IVisualElementOverrides elementOverride;

    private PythonPlugin pythonPlugin;

    protected LocalVisualElement lve;

    Map<Object, Object> properties = new HashMap<Object, Object>();

    Pattern c = Pattern.compile("#--\\{[^\\{]*?auto.*?\\}.*?$", Pattern.MULTILINE);

    public
    AutoExecutePythonPlugin() {
        lve = new LocalVisualElement();
    }

    public static
    void autoExecute(final IVisualElement newSource, final String string) {
        assert false;
    }

    TreeSet<IVisualElement> elements = new TreeSet<IVisualElement>(new Comparator<IVisualElement>() {

        public
        int compare(IVisualElement o1, IVisualElement o2) {
            Rect r1 = o1.getFrame(new Rect());
            Rect r2 = o2.getFrame(new Rect());
            int c = Double.compare(r1.x, r2.x);
            return (c == 0) ? Double.compare(System.identityHashCode(o1), System.identityHashCode(o2)) : c;
        }
    });

    public
    void check(IVisualElement newSource) {
        elements.add(newSource);
    }

    public
    void perform(IVisualElement newSource) {
        // pull auto execute information from the properties

        Ref<String> ref = new Ref<String>("");
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(newSource)
                                                       .getProperty(newSource,
                                                                    PythonPlugin.python_source_forExecution,
                                                                    ref);

        String[] s = c.split(ref.get());

        if (s.length > 1) {
            autoExecute(newSource, s[1]);
        }

        String q = python_autoExec.get(newSource);
        if (q != null) {
            //System.out.println(" about to auto exec for <" + newSource + ">");
            SplineComputingOverride.executePropertyOfElement(python_autoExec, newSource);
        }

    }

    public
    void close() {
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, Object>(pluginId + "version_0", null);
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        if (id.equals(pluginId)) return lve;
        return null;
    }

    public
    void registeredWith(IVisualElement root) {

        PythonPluginEditor.knownPythonProperties.put("Automatically Executed", python_autoExec);

        this.root = root;

        pythonPlugin = PythonPlugin.python_plugin.get(root);

        root.addChild(lve);
        elementOverride = createElementOverrides();
    }

    public
    void setPersistanceInformation(Object o) {
    }

    boolean noAuto = SystemProperties.getIntProperty("noAuto", 0) == 1;

    public
    void update() {

        if (noAuto) elements.clear();

        if (!elements.isEmpty()) {
            ArrayList<IVisualElement> a1 = new ArrayList<IVisualElement>(elements);
            ArrayList<IVisualElement> readd = new ArrayList<IVisualElement>();

            Collections.sort(a1, new Comparator<IVisualElement>() {

                public
                int compare(IVisualElement o1, IVisualElement o2) {
                    Rect f1 = o1.getFrame(null);
                    Rect f2 = o2.getFrame(null);

                    int c = Double.compare(f1.x, f2.x);
                    return (c == 0) ? Double.compare(f1.y, f2.y) : c;
                }
            });

            //System.out.println(" about to exec in this order :" + a1);

            for (IVisualElement e : a1) {
                Number m = e.getProperty(autoExecuteDelay);
                if ((m != null) && (m.intValue() > 0)) {
                    Integer soFar = e.getProperty(autoExecuteDelayedFor);
                    if (soFar == null) soFar = 0;

                    //System.out.println(" needs delay of <"+m+"> has delay of <"+soFar+">");

                    if (soFar >= m.intValue()) {
                        perform(e);
                    }
                    else {
                        e.setProperty(autoExecuteDelayedFor, ++soFar);
                        readd.add(e);
                        //System.out.println(" readding <"+e+">");
                    }
                }
                else perform(e);
            }

            // note that this is deliberately late \u2014 elements that
            // are added during somebody else's perform are not
            // autoexec'd
            elements.clear();
            elements.addAll(readd);
        }
    }

    protected
    IVisualElementOverrides createElementOverrides() {
        return new Overrides();
    }
}
