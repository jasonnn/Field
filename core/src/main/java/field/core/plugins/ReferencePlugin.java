package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.VisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.dispatch.override.IVisualElementOverridesAdaptor;
import field.core.plugins.drawing.ConnectiveThickArc2;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.windowing.components.PlainComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.SelectionGroup.iSelectionChanged;
import field.core.windowing.components.iComponent;
import field.launch.IUpdateable;
import field.math.abstraction.IFloatProvider;
import field.math.abstraction.IProvider;
import field.math.graph.NodeImpl;
import field.math.graph.IMutableContainer;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.linalg.Vector4;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;

import java.util.*;
import java.util.Map.Entry;

@Deprecated
public
class ReferencePlugin implements iPlugin {

    public
    class LocalVisualElement extends NodeImpl<IVisualElement> implements IVisualElement {

        public
        <T> void deleteProperty(VisualElementProperty<T> p) {
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
        <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {

            // this doesn't work if we're a peer, not a child of the python plugin
            // if (prop.getName().equals(PythonPlugin.python_source.getName()))
            {
                if (debug) ;//System.out.println("REF: python source changed");

                // why was this in?
                // if (!seenBefore.containsKey(source))
                {
                    seenBefore.put(source, null);
                    updateReferencesFor(source);
                }

                if (to.get() instanceof String) verifyAllReferences(source);
            }
            if (prop.getName().startsWith("__minimalReference")) {
                updateReferencesFor(source);
            }
            return super.setProperty(source, prop, to);
        }
    }

    public static boolean debug = false;

    private IVisualElement root;

    private Overrides elementOverride;

    private LocalVisualElement lve;

    private SelectionGroup<iComponent> group;

    protected static final String pluginId = "//reference_plugin";

    Set<IVisualElement> currentSelection = new HashSet<IVisualElement>();

    WeakHashMap<IVisualElement, String> seenBefore = new WeakHashMap<IVisualElement, String>();

    Map<Object, Object> properties = new HashMap<Object, Object>();

    public
    void close() {
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, Object>("version_1", null);
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        if (id.equals(pluginId)) return lve;
        return null;
    }

    public
    void registeredWith(IVisualElement root) {
        this.root = root;
        lve = new LocalVisualElement();

        elementOverride = createElementOverrides();
        // root.addChild(lve);

        Ref<PythonPlugin> ref = new Ref<PythonPlugin>(null);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(root)
                                                       .getProperty(root, PythonPlugin.python_plugin, ref);

        // lve.addChild(ref.get().getWellKnownVisualElement(PythonPlugin.pluginId));
        (ref.get().getWellKnownVisualElement(PythonPlugin.pluginId)).addChild(lve);

        group = root.getProperty(IVisualElement.selectionGroup);
        group.registerNotification(new iSelectionChanged<iComponent>() {

            public
            void selectionChanged(Set<iComponent> selected) {
                currentSelection.clear();
                for (iComponent c : selected) {
                    IVisualElement cc = c.getVisualElement();
                    if (cc != null) currentSelection.add(cc);
                }
            }
        });

    }

    public
    void setPersistanceInformation(Object o) {

        // should iterate over everything and call updatereferences and verify all references

    }

    public
    void update() {
    }

    private
    Overrides createElementOverrides() {
        return new Overrides();
    }

    private
    void ensureConnection(String propertyName, final IVisualElement source, List<IVisualElement> connectedTo) {
        Triple<VisualElement, PlainComponent, SplineComputingOverride> created =
                VisualElement.createWithToken("reflected:" + propertyName,
                                              root,
                                              new Rect(0, 0, 0, 0),
                                              VisualElement.class,
                                              PlainComponent.class,
                                              SplineComputingOverride.class);

        if (debug) ;//System.out.println("REF: adding connection <" + source + "> <" + connectedTo + ">");
        // these must be cached once they are working

        List<IUpdateable> instructions = new ArrayList<IUpdateable>();
        for (final IVisualElement v : connectedTo) {

            if (debug) ;//System.out.println(" connecting to <" + v + "> from <" + source + ">");

            ConnectiveThickArc2 con = new ConnectiveThickArc2(created.left,
                                                              new IProvider.Constant<Vector4>(new Vector4(0.0f,
                                                                                                          0.0f,
                                                                                                          0.0f,
                                                                                                          0.15f)),
                                                              new IProvider.Constant<Vector4>(new Vector4(0,
                                                                                                          0,
                                                                                                          0,
                                                                                                          0.5f)),
                                                              source,
                                                              new IFloatProvider.Constant(10),
                                                              v,
                                                              new IFloatProvider.Constant(0));
            con.addGate(new IFloatProvider() {
                public
                float evaluate() {
                    return (currentSelection.contains(v) || currentSelection.contains(source)) ? 1 : 0;
                }
            });
        }

        created.left.setProperty(SplineComputingOverride.computed_drawingInstructions,
                                 new ArrayList<IUpdateable>(instructions));
        created.left.setProperty(IVisualElement.doNotSave, true);
    }

    private static
    int indexOf(List<String> text, String name) {
        for (String q : text) {
            if (text.contains(name)) return text.indexOf(name);
        }
        return -1;
    }

    private
    void removeConnection(String name, IVisualElement source) {
        if (debug) ;//System.out.println("REF: deleting reference marker");
        VisualElement.deleteWithToken("reflected:" + name, root);
    }

    protected
    void updateReferencesFor(IVisualElement source) {

        if (debug) ;//System.out.println("REF: update references for <" + source + ">");

        Map<Object, Object> allProperties = source.payload();
        for (Entry<Object, Object> o : new HashMap<Object, Object>(allProperties).entrySet()) {
            if (o.getKey() instanceof VisualElementProperty) {
                if (((VisualElementProperty) o.getKey()).getName().startsWith("__minimalReference")) {
                    if (debug) ;//System.out.println("REF: got property <" + o + ">");
                    if (o.getValue() instanceof List) {
                        List<IVisualElement> connectedTo = (List<IVisualElement>) o.getValue();
                        if (connectedTo != null) {
                            if (debug) ;//System.out.println("REF: ensuring connection");
                            ensureConnection(((VisualElementProperty) o.getKey()).getName(), source, connectedTo);
                        }
                    }
                }
            }
        }
    }

    protected
    void verifyAllReferences(IVisualElement source) {

        List<String> texts = new ArrayList<String>();

        for (VisualElementProperty p : PythonPluginEditor.knownPythonProperties.values()) {
            String pp = (String) source.getProperty(p);
            if (pp != null) texts.add(pp);
        }

        verifyReferencesFor(source, texts);
    }

    protected
    void verifyReferencesFor(IVisualElement source, List<String> text) {

        if (debug) ;//System.out.println("REF: verify references for <" + source + "> with text <" + text + ">");

        Map<Object, Object> allProperties = source.payload();
        for (Entry<Object, Object> o : new HashMap<Object, Object>(allProperties).entrySet()) {
            if (o.getKey() instanceof VisualElementProperty) {
                if (((VisualElementProperty) o.getKey()).getName().startsWith("__minimalReference")
                    && !((VisualElementProperty) o.getKey()).getName().endsWith("source")) {

                    if (debug) ;//System.out.println(" checking <" + o + "> for in name <" + text + ">");

                    if (indexOf(text, ((VisualElementProperty) o.getKey()).getName()) == -1) {
                        removeConnection(((VisualElementProperty) o.getKey()).getName(), source);
                        source.deleteProperty((VisualElementProperty) o.getKey());
                        source.deleteProperty(new VisualElementProperty(((VisualElementProperty) o.getKey()).getName()
                                                                        + "-source"));
                    }
                }
            }
        }

    }

}
