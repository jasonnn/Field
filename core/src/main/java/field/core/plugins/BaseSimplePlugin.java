package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.Rect;
import field.math.graph.NodeImpl;
import field.math.graph.IMutableContainer;
import field.util.collect.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public abstract
class BaseSimplePlugin implements iPlugin {

    protected
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
            if (p == overrides) return (T) BaseSimplePlugin.this.overrides;
            Object o = properties.get(p);
            return (T) o;
        }

        public
        String getUniqueID() {
            return getPluginName();
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

        @Override
        public
        String toString() {
            return "lve for plugin:" + BaseSimplePlugin.this.getClass();
        }
    }

    protected static
    class Overrides extends DefaultOverride {
    }

    protected IVisualElement element;
    protected DefaultOverride overrides;
    protected Map<Object, Object> properties = new HashMap<Object, Object>();

    protected IVisualElement root;

    public
    void close() {
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, Object>("version0", null);
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        String n = getPluginName();
        if (id.equals(n)) return element;
        return null;
    }

    public
    void registeredWith(IVisualElement root) {
        this.root = root;

        element = newVisualElement();
        overrides = newVisualElementOverrides();
        overrides.setVisualElement(element);
        element.setProperty(IVisualElement.overrides, overrides);

        root.addChild(element);

        new VisualElementProperty(getPluginName()).set(root, root, element);

    }

    public
    void setPersistanceInformation(Object o) {
    }

    public
    void update() {
    }

    protected
    String getPluginName() {
        String name = getPluginNameImpl();
        if (!name.startsWith("//plugin_")) name = "//plugin_" + name;
        return name;
    }

    protected abstract
    String getPluginNameImpl();

    protected
    IVisualElement newVisualElement() {
        return new LocalVisualElement();
    }

    protected
    DefaultOverride newVisualElementOverrides() {
        return new Overrides();
    }


}
