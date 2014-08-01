package field.core.dispatch;

import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.plugins.autoexecute.AutoExecutePythonPlugin;
import field.core.plugins.pseudo.PseudoPropertiesPlugin;
import field.core.ui.PopupTextBox;
import field.core.ui.text.PythonTextEditor;
import field.core.ui.text.protect.ClassDocumentationProtect.Comp;
import field.core.util.FieldPyObjectAdaptor2;
import field.core.windowing.components.*;
import field.core.windowing.overlay.OverlayAnimationManager;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.visitors.TopologyVisitor_breadthFirst;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Triple;

import java.rmi.server.UID;
import java.util.*;
import java.util.Map.Entry;

public
class VisualElement extends NodeImpl<IVisualElement> implements IVisualElement {
    static {
        FieldPyObjectAdaptor2.initialize();
    }

    public static
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> Triple<T, S, U> create(Rect bounds,
                                                                                                                              Class<T> visualElementclass,
                                                                                                                              Class<S> componentClass,
                                                                                                                              Class<U> overrideClass) {
        try {
            S s = componentClass.getConstructor(new Class[]{Rect.class}).newInstance(bounds);
            T t = visualElementclass.getConstructor(new Class[]{iComponent.class}).newInstance(s);
            U u = overrideClass.newInstance();
            t.setElementOverride(u);
            u.setVisualElement(t);
            t.setFrame(bounds);
            s.setVisualElement(t);
            return new Triple<T, S, U>(t, s, u);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> Triple<T, S, U> createAddAndName(Rect bounds,
                                                                                                                                        final IVisualElement root,
                                                                                                                                        String defaultName,
                                                                                                                                        Class<T> visualElementclass,
                                                                                                                                        Class<S> componentClass,
                                                                                                                                        Class<U> overrideClass,
                                                                                                                                        final IUpdateable continuation) {
        try {
            final S s = componentClass.getConstructor(new Class[]{Rect.class}).newInstance(bounds);
            final T t = visualElementclass.getConstructor(new Class[]{iComponent.class}).newInstance(s);
            U u = overrideClass.newInstance();
            t.setElementOverride(u);
            u.setVisualElement(t);
            t.setFrame(bounds);
            s.setVisualElement(t);

            t.addChild(root);

            t.setProperty(AutoExecutePythonPlugin.python_autoExec, "");

            IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(t).added(t);
            IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(t).added(t);

            PopupTextBox.Modal.getStringOrCancel(PopupTextBox.Modal.elementAt(t),
                                                 "name :",
                                                 defaultName,
                                                 new IAcceptor<String>() {
                                                     public
                                                     IAcceptor<String> set(String to) {
                                                         IVisualElement.name.set(t, t, to);
                                                         if (continuation != null)
                                                             continuation.update();

                                                         IVisualElement.dirty.set(t, t, true);
                                                         Rect rect = s.getBounds();

                                                         OverlayAnimationManager.notifyAsText(root,
                                                                                              "created element '"
                                                                                              + to
                                                                                              + '\'',
                                                                                              rect);


                                                         SelectionGroup<iComponent> selectionGroup =
                                                                 IVisualElement.selectionGroup.get(t);
                                                         selectionGroup.addToSelection(IVisualElement.localView.get(t));
                                                         IVisualElement.localView.get(t).setSelected(true);

                                                         return this;
                                                     }

                                                 },
                                                 new IUpdateable() {

                                                     @Override
                                                     public
                                                     void update() {
                                                         delete(t);
                                                     }
                                                 });

            return new Triple<T, S, U>(t, s, u);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> Triple<T, S, U> createWithName(Rect bounds,
                                                                                                                                      final IVisualElement root,
                                                                                                                                      Class<T> visualElementclass,
                                                                                                                                      Class<S> componentClass,
                                                                                                                                      Class<U> overrideClass,
                                                                                                                                      String name) {
        try {
            S s = componentClass.getConstructor(new Class[]{Rect.class}).newInstance(bounds);
            final T t = visualElementclass.getConstructor(new Class[]{iComponent.class}).newInstance(s);
            U u = overrideClass.newInstance();
            t.setElementOverride(u);
            u.setVisualElement(t);
            t.setFrame(bounds);
            s.setVisualElement(t);


            t.addChild(root);
            IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(t).added(t);
            IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(t).added(t);

            IVisualElement.name.set(t, t, name);
            IVisualElement.dirty.set(t, t, true);

            return new Triple<T, S, U>(t, s, u);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> Triple<T, S, U> createWithToken(final Object token,
                                                                                                                                       IVisualElement root,
                                                                                                                                       Rect bounds,
                                                                                                                                       Class<T> visualElementclass,
                                                                                                                                       Class<S> componentClass,
                                                                                                                                       Class<U> overrideClass) {
        try {
            final IVisualElement[] ans = new IVisualElement[1];
            TopologyVisitor_breadthFirst<IVisualElement> search =
                    new TopologyVisitor_breadthFirst<IVisualElement>(true) {
                        @Override
                        protected
                        TraversalHint visit(IVisualElement n) {
                            Object tok = n.getProperty(IVisualElement.creationToken);
                            if (tok != null && tok.equals(token)) {
                                ans[0] = n;
                                return StandardTraversalHint.STOP;
                            }
                            return StandardTraversalHint.CONTINUE;
                        }

                    };
            search.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);

            if (ans[0] == null) {

                Triple<T, S, U> r = create(bounds, visualElementclass, componentClass, overrideClass);
                r.left.addChild(root);
                IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(r.left).added(r.left);
                IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(r.left).added(r.left);
                r.left.setProperty(IVisualElement.creationToken, token);
                return r;
            }
            else {
                return new Triple<T, S, U>((T) ans[0],
                                           (S) ans[0].getProperty(IVisualElement.localView),
                                           (U) ans[0].getProperty(IVisualElement.overrides));
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static
    void delete(final IVisualElement node) {
        delete(node, node);
    }

    public static
    void delete(IVisualElement root, final IVisualElement node) {
        if (root == null)
            root = node;

        final IVisualElement froot = root;

        // Launcher.getLauncher().registerUpdateable(new iUpdateable(){
        //
        // public void update() {
        IVisualElementOverrides.topology.begin(froot);
        IVisualElementOverrides.forward.deleted.apply(node);
        IVisualElementOverrides.backward.deleted.apply(node);
        IVisualElementOverrides.topology.end(froot);

        for (IVisualElement ve : new ArrayList<IVisualElement>((Collection<IVisualElement>) node.getParents())) {
            ve.removeChild(node);

            // if there are parents that
            // have no children right now,
            // delete them too
            if (ve.getChildren().size() == 0 && ve.getParents().size() == 0)
                delete(root, ve);

        }
        for (IVisualElement ve : new ArrayList<IVisualElement>(node.getChildren())) {
            node.removeChild(ve);

            // if there are parents that
            // have no children right now,
            // delete them too
            if (ve.getChildren().size() == 0 && ve.getParents().size() == 0)
                delete(root, ve);

        }

        // }});
    }

    public static
    void deleteWithToken(final Object token, IVisualElement root) {
        final IVisualElement[] ans = new IVisualElement[1];
        TopologyVisitor_breadthFirst<IVisualElement> search = new TopologyVisitor_breadthFirst<IVisualElement>(true) {
            @Override
            protected
            TraversalHint visit(IVisualElement n) {
                Object tok = n.getProperty(IVisualElement.creationToken);
                if (tok != null && tok.equals(token)) {
                    ans[0] = n;
                    return StandardTraversalHint.STOP;
                }
                return StandardTraversalHint.CONTINUE;
            }

        };
        search.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);

        if (ans[0] != null) {

            IVisualElement source = ans[0];
            delete(root, source);
        }

    }

    private iComponent d;

    private HashSet<IVisualElement> cachedChildren;

    Rect frame = new Rect(0, 0, 0, 0);

    String id = "__" + new UID().toString();

    Map<Object, Object> properties = new HashMap<Object, Object>();

    IVisualElementOverrides elementOverride;

    public
    VisualElement() {
        reverseInsertionOrder = true;
        setElementOverride(new DefaultOverride().setVisualElement(this));
        //System.out.println(" -- new visual element --");
    }

    public
    VisualElement(iComponent d) {
        reverseInsertionOrder = true;
        this.d = d;
        properties.put(localView, d);
        setElementOverride(new DefaultOverride().setVisualElement(this));

        //System.out.println(" -- new visual element --");
    }

    @Override
    public
    void addChild(IVisualElement newChild) {
        super.addChild(newChild);
        cachedChildren = new HashSet<IVisualElement>(this.getChildren());
    }

    public
    <T> void deleteProperty(VisualElementProperty<T> p) {
        properties.remove(p);
    }

    public
    void dispose() {
        if (d == null)
            return;

        if (d instanceof DraggableComponent)
            ((DraggableComponent) d).dispose();

        if (d instanceof PlainComponent)
            ((PlainComponent) d).dispose();

    }

    public
    HashSet<IVisualElement> getCachedChildren() {
        if (cachedChildren == null)
            cachedChildren = new HashSet<IVisualElement>(this.getChildren());
        return cachedChildren;
    }

    public
    Rect getFrame(Rect out) {
        if (out == null)
            out = new Rect(0, 0, 0, 0);
        out.setValue(frame);
        return out;
    }

    public
    <T> T getProperty(VisualElementProperty<T> p) {
        Object o = properties.get(p);
        return (T) o;
    }

    public
    String getUniqueID() {
        return id;
    }

    public
    Map<Object, Object> payload() {
        return properties;
    }

    @Override
    public
    void removeChild(IVisualElement newChild) {
        super.removeChild(newChild);
        cachedChildren = new HashSet<IVisualElement>(this.getChildren());
    }

    public
    VisualElement setElementOverride(IVisualElementOverrides elementOverride) {
        this.elementOverride = elementOverride;
        properties.put(overrides, elementOverride);
        return this;
    }

    public
    void setFrame(Rect out) {
        if (frame == null)
            frame = new Rect(0, 0, 0, 0);
        frame.setValue(out);
        if (d != null)
            d.setBounds(frame);
    }

    public
    IMutableContainer<Map<Object, Object>, IVisualElement> setPayload(Map<Object, Object> t) {
        properties = t;
        if (properties.get(overrides) != null)
            this.elementOverride = (IVisualElementOverrides) properties.get(overrides);
        if (properties.get(localView) != null) {
            this.d = (iComponent) properties.get(localView);
            this.d.setVisualElement(this);
        }
        if (properties.get(IVisualElement.hidden) != null) {
            if (d instanceof DraggableComponent)
                ((iDraggableComponent) d).setHidden((Boolean) properties.get(IVisualElement.hidden));
            if (d instanceof PlainDraggableComponent)
                ((iDraggableComponent) d).setHidden((Boolean) properties.get(IVisualElement.hidden));
        }
        return this;
    }

    public
    <T> IVisualElement setProperty(VisualElementProperty<T> p, T to) {

        properties.put(p, to);

        if (p.equals(IVisualElement.dirty)) {
            if (d instanceof DraggableComponent)
                ((iDraggableComponent) d).setDirty();
            if (d instanceof PlainComponent)
                ((PlainComponent) d).setDirty();
            if (d instanceof PlainDraggableComponent)
                ((iDraggableComponent) d).setDirty();
        }
        if (p.equals(IVisualElement.hidden)) {
            if (d instanceof DraggableComponent)
                ((iDraggableComponent) d).setHidden((Boolean) to);
            if (d instanceof PlainDraggableComponent)
                ((iDraggableComponent) d).setHidden((Boolean) to);
        }
        return this;
    }

    public
    void setUniqueID(String uid) {
        id = uid;
    }

    @Override
    public
    String toString() {
        return "Element, named <"
               + getProperty(IVisualElement.name)
               + "> : <"
               + id
               + '('
               + System.identityHashCode(this)
               + ")>";
    }

    public static
    List<Comp> getClassCustomCompletion(String prefix, Object of) {
        VisualElement adaptor = ((VisualElement) of);
        List<Comp> c = new ArrayList<Comp>();
        if (prefix.length() == 0) {
            c.add(new Comp("The _self variable (and any other box reference) gives you read and write access to properties which are stored in this element or it's parents. A great many things in Field are properties, and understanding them is often the key to managing the power of many visual elements or customizing the behavior of Field."));
        }
        else {
            String name = IVisualElement.name.get(adaptor);
            if (name == null)
                name = "unnamed element";
            c.add(new Comp("<h3><i><font color='#555555' >\u2014\u2014</font> "
                           + name
                           + " <font color='#555555' >\u2014\u2014</font></i> . "
                           + prefix
                           + " <font color='#555555' size=+3>\u2041\u2014</font></h3>"));
        }

        List<Comp> ps = new ArrayList<Comp>();
        ps.add(new Comp("", "<i>Psuedo</i>properties (generally read only)").setTitle(true));
        for (VisualElementProperty p : PseudoPropertiesPlugin.properties) {
            if (p.getName().startsWith(prefix)) {
                ps.add(new Comp(p.getName(), PythonTextEditor.limit(String.valueOf(p.get(adaptor)))));
            }
        }
        if (ps.size() > 1)
            c.addAll(ps);
        Map<Object, Object> set = adaptor.payload();
        String groupname = "Already set, local to this element";
        addPropertiesByInspection(prefix, adaptor, c, set, groupname);
        return c;
    }

    private static
    void addPropertiesByInspection(String prefix,
                                   IVisualElement visualElement,
                                   List<Comp> c,
                                   Map<Object, Object> set,
                                   String groupname) {
        if (set.size() > 0) {
            List<Comp> sub = new ArrayList<Comp>();
            Set<Entry<Object, Object>> e = set.entrySet();
            for (Entry<Object, Object> ee : e) {
                VisualElementProperty k = (VisualElementProperty) ee.getKey();
                String name = k.getName();
                if (name.startsWith(prefix)) {
                    Object value = ee.getValue();

                    Comp cc = new Comp(name, PythonTextEditor.limit("\u2190" + value));
                    sub.add(cc);
                }
            }
            if (sub.size() > 0) {
                Comp t = new Comp("", groupname).setTitle(true);
                c.add(t);
                c.addAll(sub);
            }
        }
        List<IVisualElement> childern = visualElement.getChildren();
        if (c != null) {
            for (IVisualElement vv : childern) {
                Map<Object, Object> setp = vv.payload();
                String name = vv.getProperty(IVisualElement.name);
                if (name == null)
                    name = vv.getClass().getName();
                addPropertiesByInspection(prefix, vv, c, setp, "Set in parent <b>" + name + "</b>");
            }
        }
    }

}
