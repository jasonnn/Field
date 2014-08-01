package field.core.plugins.connection;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.VisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.persistance.VisualElementReference;
import field.core.plugins.iPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.ui.PopupTextBox;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.PlainComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.core.windowing.overlay.OverlayAnimationManager;
import field.math.abstraction.IAcceptor;
import field.math.graph.NodeImpl;
import field.math.graph.IMutableContainer;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * plugin for connecting visual elements together
 */
public
class Connections implements iPlugin {

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
            if (p.equals(overrides)) return (T) elementOverride;
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
    class Overrides extends DefaultOverride {
        @Override
        public
        TraversalHint deleted(IVisualElement source) {
            if (connections.containsKey(source.getUniqueID())) {
                IVisualElement removed = connections.remove(source.getUniqueID());
                PythonPluginEditor.delete(removed, root);
            }

            return StandardTraversalHint.CONTINUE;
        }
    }

    public static final VisualElementProperty<Connections> connections_plugin =
            new VisualElementProperty<Connections>("connection_plugin");

    public static final String pluginId = "//plugin_connections";

    public static int uniq;

    public Map<String, IVisualElement> connections = new HashMap<String, IVisualElement>();

    private final IVisualElement rootElement;

    private IVisualElement root;


    private LocalVisualElement lve;

    private SelectionGroup<iComponent> group;

    private final StandardFluidSheet sheet;

    DefaultOverride elementOverride;

    Map<Object, Object> properties = new HashMap<Object, Object>();

    public
    Connections(StandardFluidSheet sheet, IVisualElement rootElement) {
        uniq++;
        this.sheet = sheet;
        this.rootElement = rootElement;

    }


    public
    void close() {
    }

//	public iVisualElement connect(iVisualElement from, iVisualElement to)
//	{
//		Triple<VisualElement, PlainComponent, ClosestEdge> c1 = VisualElement.create(new Rect(30,30,30,30), VisualElement.class, PlainComponent.class, LineDrawingOverride.ClosestEdge.class);
//
//		c1.left.addChild(rootElement);
//		new iVisualElementOverrides.MakeDispatchProxy().getBackwardsOverrideProxyFor(c1.left).added(c1.left);
//		new iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(c1.left).added(c1.left);
//
//		connections.put(c1.left.getUniqueID(), c1.left);
//
//		LineDrawingOverride.lineDrawing_to.set(c1.left, c1.left, new VisualElementReference(to));
//		LineDrawingOverride.lineDrawing_from.set(c1.left, c1.left, new VisualElementReference(from));
//
//		return c1.left;
//	}

    public
    <T extends DefaultOverride> IVisualElement connect(final IVisualElement from,
                                                                               final IVisualElement to,
                                                                               Class<T> connective,
                                                                               boolean askforname) {
        final Triple<VisualElement, PlainComponent, T> c1 =
                VisualElement.create(new Rect(30, 30, 30, 30), VisualElement.class, PlainComponent.class, connective);

        c1.left.addChild(rootElement);
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(c1.left).added(c1.left);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(c1.left).added(c1.left);

        LineDrawingOverride.lineDrawing_to.set(c1.left, c1.left, new VisualElementReference(to));
        LineDrawingOverride.lineDrawing_from.set(c1.left, c1.left, new VisualElementReference(from));

        final GLComponentWindow frame = IVisualElement.enclosingFrame.get(root);

        if (askforname) PopupTextBox.Modal.getString(PopupTextBox.Modal.elementAt(c1.left),
                                                     "name :",
                                                     '\''
                                                     + IVisualElement.name.get(from)
                                                     + "' to '"
                                                     + IVisualElement.name.get(to)
                                                     + '\'',
                                                     new IAcceptor<String>() {
                                                         public
                                                         IAcceptor<String> set(String x) {
                                                             IVisualElement.name.set(c1.left, c1.left, x);
                                                             IVisualElement.dirty.set(c1.left, c1.left, true);

                                                             if (frame != null) {
                                                                 OverlayAnimationManager.notifyAsText(from,
                                                                                                      "created connection '"
                                                                                                      + to
                                                                                                      + '\'',
                                                                                                      to.getFrame(null)
                                                                                                        .union(from.getFrame(null)));
                                                             }

                                                             return this;
                                                         }
                                                     });
        else {
            IVisualElement.name.set(c1.left,
                                    c1.left,
                                    '\''
                                    + from.getProperty(IVisualElement.name)
                                    + "'->'"
                                    + to.getProperty(IVisualElement.name)
                                    + '\'');
        }


        connections.put(c1.left.getUniqueID(), c1.left);
        return c1.left;
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, Collection<String>>(pluginId + "version_1", new ArrayList(connections.keySet()));
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
        lve.setProperty(connections_plugin, this);

        // add a next to root that adds some overrides
        root.addChild(lve);

        // register for selection updates? (no, do it in subclass)
        group = root.getProperty(IVisualElement.selectionGroup);

        elementOverride = createElementOverrides();
        elementOverride.setVisualElement(lve);
    }

    public
    void setPersistanceInformation(Object o) {
        if (o instanceof Pair) {
            Pair<String, Collection<String>> p = (Pair<String, Collection<String>>) o;
            if (p.left.equals(pluginId + "version_1")) {
                // need to go through and find those elements
                for (String s : p.right) {
                    IVisualElement element = StandardFluidSheet.findVisualElement(root, s);
                    if (element == null) {
                    }
                    else {
                        connections.put(s, element);

                        IVisualElementOverrides over = element.getProperty(IVisualElement.overrides);
                    }
                }
            }
        }
    }

    public
    void update() {
        if (connections == null) connections = new HashMap<String, IVisualElement>();
    }

    protected
    DefaultOverride createElementOverrides() {
        return new Overrides();
    }

}
