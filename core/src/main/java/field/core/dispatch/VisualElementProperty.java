package field.core.dispatch;

import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.override.Ref;

import java.util.*;

/**
* Created by jason on 7/31/14.
*/
public
class VisualElementProperty<T> {
    private final String name;

    private VisualElementProperty<T> aliasedTo;

    boolean freezable = false;

    public
    VisualElementProperty(String name) {
        if (name.startsWith("//")) name = name.substring(2) + '_';
        this.name = name;
    }

    public
    VisualElementProperty(String name, VisualElementProperty<T> anAliasFor) {
        this.name = name;
        this.setAliasedTo(anAliasFor);
    }

    public
    <P, Q extends List<P>> void addToList(Class<Q> q, IVisualElement forElement, P p) {
        Q t = (Q) get(forElement);
        if (t == null) try {
            t = q.newInstance();
            t.add(p);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        else t.add(p);

        set(forElement, forElement, (T) t);
    }

    public
    boolean containsSuffix(String string) {
        if (name.contains("_")) {
            String[] s = name.split("_");
            for (int i = 1; i < s.length; i++) {
                if (s[i].equals(string)) return true;
            }
        }
        return false;
    }

    public
    void delete(IVisualElement from, IVisualElement on) {
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(on).deleteProperty(from, this);
         IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(on).deleteProperty(from, this);
    }

    @Override
    public
    boolean equals(Object obj) {
        return obj instanceof VisualElementProperty && name.equals(((VisualElementProperty) obj).name);
    }

    public
    List<T> accumulateList(IVisualElement startAt) {

        List<T> at = new ArrayList<T>();
        LinkedHashSet seen = new LinkedHashSet();
        _accumulateList(startAt, at, seen);

        return at;
    }

    private
    void _accumulateList(IVisualElement from, List<T> into, Set seen) {
        if (seen.contains(from)) return;
        seen.add(from);
        Object x = from.getProperty(this);
        if (x != null) {
            if (x instanceof Collection) into.addAll(((Collection) x));
            else into.add((T) x);
        }
        Set<IVisualElement> c = IVisualElementOverrides.topology.childrenOf(from);
        if (c != null) {
            for (IVisualElement cc : c)
                _accumulateList(cc, into, seen);
        }
    }

    public
    T get(IVisualElement e) {
        Ref<T> ref = new Ref<T>(null);
        IVisualElement ee = IVisualElementOverrides.topology.getAt();
        IVisualElementOverrides.topology.setAt(e);
        IVisualElementOverrides.forward.getProperty.getProperty(e, this, ref);
        IVisualElementOverrides.topology.setAt(ee);
        // new
        // iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(e).getProperty(e,
        // this, ref);
        return ref.get();
    }

    public
    T get(IVisualElement e, IVisualElement from) {
        Ref<T> ref = new Ref<T>(null);

        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(from).getProperty(e, this, ref);
        return ref.get();
    }

    public
    T getAbove(IVisualElement e) {
        Ref<T> ref = new Ref<T>(null);
        IVisualElement ee = IVisualElementOverrides.topology.getAt();
        IVisualElementOverrides.topology.setAt(e);
        IVisualElementOverrides.forwardAbove.getProperty.getProperty(e, this, ref);
        IVisualElementOverrides.topology.setAt(ee);
        // new
        // iVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(e).getProperty(e,
        // this, ref);
        return ref.get();
    }

    public
    VisualElementProperty<T> getAliasedTo() {
        return aliasedTo;
    }

    public
    boolean getBoolean(IVisualElement forElement, boolean def) {
        T r = get(forElement);
        if (r == null) return def;
        if (r instanceof Number) return ((Number) r).doubleValue() != 0;
        if (r instanceof Boolean) return ((Boolean) r);
        return true;
    }

    public
    float getFloat(IVisualElement forElement, float def) {
        T g = get(forElement);
        if (g == null) return def;
        if (g instanceof Boolean) {
            return ((Boolean) g) ? 1f : 0f;
        }
        return ((Number) g).floatValue();
    }

    public
    String getName() {
        return name;
    }

    public
    String getNameNoSuffix() {
        String[] s = name.split("_");
        String a = "";
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 1) {
                a = a + (i > 0 ? "_" : "") + s[i];
            }
        }
        return a;
    }

    public
    Ref<T> getRef(IVisualElement e) {
        Ref<T> ref = new Ref<T>(null);
        IVisualElementOverrides.topology.begin(e);
        IVisualElementOverrides.forward.getProperty.getProperty(e, this, ref);
        return ref;
    }

    @Override
    public
    int hashCode() {
        return name.hashCode();
    }

    public
    boolean isFreezable() {
        return freezable;
    }

    public
    <Q, P> void putInMap(IVisualElement on, Q name, P aa) {
        Map m = (Map) get(on);
        if (m == null) m = new HashMap();
        m.put(name, aa);
        set(on, on, (T) m);
    }

    public
    T set(IVisualElement from, IVisualElement on, T to) {
        Ref<T> ref = new Ref<T>(to);
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(on)
                                                       .setProperty(from, this, ref);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(on).setProperty(from, this, ref);
        return ref.get();
    }

    public
    VisualElementProperty<T> setAliasedTo(VisualElementProperty<T> to) {
        aliasedTo = to;
        return this;
    }

    public
    VisualElementProperty<T> setFreezable() {
        freezable = true;
        return this;
    }

    public
    boolean shouldPersist() {
        return !name.endsWith("_");
    }

    public
    boolean shouldVersion() {
        return containsSuffix("v");
    }

    @Override
    public
    String toString() {
        return "vep;" + name;
    }
}
