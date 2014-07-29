package field.core.dispatch;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElementOverrides.Ref;
import field.core.execution.BasicRunner;
import field.core.persistance.FluidCopyPastePersistence;
import field.core.plugins.drawing.ToolPalette2;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.ui.MarkingMenuBuilder;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.GlassComponent;
import field.core.windowing.components.RootComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.math.graph.IMutableContainer;
import field.math.linalg.Vector2;
import field.math.linalg.Vector3;
import field.math.linalg.Vector4;
import field.util.collect.tuple.Pair;

import java.io.Serializable;
import java.util.*;

public
interface IVisualElement extends IMutableContainer<Map<Object, Object>, IVisualElement> {

    public static
    class Rect implements Serializable {
        public static
        Rect slowUnion(Rect r, Rect rect, Vector3 cameraPosition) {
            return union(r, rect);
        }

        public
        Rect() {
            this(0, 0, 0, 0);
        }

        public
        Rect(Rect r) {
            this(0, 0, 0, 0);
            setValue(r);
        }

        public static
        Rect union(Rect r, Rect rect) {
            if (r == null) return new Rect(0, 0, 0, 0).setValue(rect);
            if (rect == null) return new Rect(0, 0, 0, 0).setValue(r);
            return r.union(rect);
        }

        public double x;

        public double y;

        public double w;

        public double h;

        public
        Rect(double x2, double y2, double w2, double h2) {
            this.x = x2;
            this.y = y2;
            this.w = w2;
            this.h = h2;
        }

        public
        Rect(float x2, float y2, float w2, float h2) {
            this.x = x2;
            this.y = y2;
            this.w = w2;
            this.h = h2;
        }

        public
        Rect(Vector2 a, Vector2 b) {
            this.x = Math.min(a.x, b.x);
            this.y = Math.min(a.y, b.y);
            this.w = Math.max(a.x, b.x) - this.x;
            this.h = Math.max(a.y, b.y) - this.y;
        }

        public
        Rect alignLeftTo(Rect here) {
            this.x = here.x;
            return this;
        }

        public
        Rect alignTopTo(Rect here) {
            this.y = here.y;
            return this;
        }

        public
        float area() {
            return (float) (w * h);
        }

        public
        Rect blendTowards(float d, Rect a) {
            return new Rect(x * (1 - d) + d * a.x, y * (1 - d) + d * a.y, w * (1 - d) + d * a.w, h * (1 - d) + d * a.h);
        }

        public
        Vector3 bottomLeft() {
            return new Vector3(x, y + h, 0);
        }

        public
        Vector3 bottomRight() {
            return new Vector3(x + w, y + h, 0);
        }

        public
        Rect convertFromNDC(Rect input) {
            return new Rect(input.x + x * input.w, input.y + y * input.h, input.w * w, input.h * h);
        }

        public
        Vector3 convertFromNDC(Vector3 v2) {
            return new Vector3(v2.x * this.w + this.x, v2.y * this.h + this.y, 0);
        }

        public
        Vector2 convertFromNDC(Vector2 v2) {
            return new Vector2(v2.x * this.w + this.x, v2.y * this.h + this.y);
        }

        public
        Vector3 convertToNDC(Vector3 v2) {
            return new Vector3((v2.x - this.x) / this.w, (v2.y - this.y) / this.h, 0);
        }

        public
        float distanceFrom(Rect currentRect) {
            return (float) (Math.abs(x - currentRect.x)
                            + Math.abs(y - currentRect.y)
                            + Math.abs(w - currentRect.w)
                            + Math.abs(h - currentRect.h));
        }

        @Override
        public
        int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(h);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(w);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(x);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public
        boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Rect other = (Rect) obj;
            if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h)) return false;
            if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w)) return false;
            if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
            if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
            return true;
        }

        public
        Vector3[] fourCorners() {
            return new Vector3[]{topLeft(), topRight(), bottomRight(), bottomLeft()};
        }

        public
        Rect includePoint(float cx, float cy) {
            double minx = Math.min(cx, x);
            double miny = Math.min(cy, y);

            double maxx = Math.max(cx, x + w);
            double maxy = Math.max(cy, y + h);

            return new Rect(minx, miny, maxx - minx, maxy - miny);

        }

        public
        Rect inset(float f) {
            x = x + w * f;
            y = y + h * f;
            w = w - 2 * w * f;
            h = h - 2 * h * f;
            return this;
        }

        public
        Rect insetAbsolute(float f) {
            x = x + f;
            y = y + f;
            w = w - 2 * f;
            h = h - 2 * f;
            return this;
        }

        public
        void insetByMin(float f) {
            double am = Math.min(w, h) * f;
            x = x + am;
            y = y + am;
            w = w - 2 * am;
            h = h - 2 * am;
        }

        public
        Rect intersect(Rect r) {
            double minx = Math.max(r.x, x);
            double miny = Math.max(r.y, y);

            double maxx = Math.min(r.x + r.w, x + w);
            double maxy = Math.min(r.y + r.h, y + h);

            return new Rect(minx, miny, Math.max(0, maxx - minx), Math.max(0, maxy - miny));
        }

        public
        boolean isInside(Vector2 v2) {
            if (v2.x >= x && v2.y >= y && v2.x < x + w && v2.y < y + h) {
                return true;
            }
            else return false;
        }

        public
        Vector3 midPoint() {
            return new Vector3(x + w / 2, y + h / 2, 0);
        }

        public
        Vector2 midpoint2() {
            return new Vector2(x + w / 2, y + h / 2);
        }

        public
        Vector3 midPointLeftEdge() {
            return new Vector3(x, y + h / 2, 0);
        }

        public
        Vector3 midPointRightEdge() {
            return new Vector3(x + w, y + h / 2, 0);
        }

        public
        Vector2 midPointTopEdge() {
            return new Vector2(x + w / 2, y + h);
        }

        public
        Rect moveToInclude(Vector2 v) {
            if (v.x > x + w) {
                x = v.x - w;
            }
            else if (v.x < x) {
                x = v.x;
            }
            if (v.y > y + h) {
                y = v.y - h;
            }
            else if (v.y < y) {
                y = v.y;
            }
            return this;
        }

        public
        boolean overlaps(Rect rect) {

            if (Math.min(rect.x, rect.x + rect.w) <= Math.max(x, x + w)
                && Math.max(rect.x, rect.x + rect.w) >= Math.min(x, x + w)
                && Math.min(rect.y, rect.y + rect.h) < Math.max(y, y + h)
                && Math.max(rect.y, rect.y + rect.h) >= Math.min(y, y + h)) return true;

            // if (rect.x <= x + w && (rect.x + rect.w) >= x &&
            // rect.y <= y + h && (rect.y + rect.h) >= y) return
            // true;
            return false;
        }

        public
        Vector3 relativize(Vector3 v) {
            return new Vector3(v.x * w + x, v.y * h + y, 0);
        }

        public
        Rect setSize(float w, float h) {
            this.w = w;
            this.h = h;
            return this;
        }

        public
        Rect setValue(Rect newFrame) {
            this.x = newFrame.x;
            this.y = newFrame.y;
            this.w = newFrame.w;
            this.h = newFrame.h;
            return this;
        }

        public
        float size() {
            return (float) (w * w + h * h);
        }

        public
        Vector3 topLeft() {
            return new Vector3(x, y, 0);
        }

        public
        Vector3 topRight() {
            return new Vector3(x + w, y, 0);
        }

        @Override
        public
        String toString() {
            return "r:" + x + ' ' + y + ' ' + w + ' ' + h;
        }

        public
        Rect union(Rect r) {
            if (r == null) return new Rect(x, y, w, h);

            double minx = Math.min(r.x, x);
            double miny = Math.min(r.y, y);

            double maxx = Math.max(r.x + r.w, x + w);
            double maxy = Math.max(r.y + r.h, y + h);

            return new Rect(minx, miny, maxx - minx, maxy - miny);
        }

        public
        Pair<Vector2, Vector2> connectOver(Rect other) {
            return connectOver(other,
                               new Vector2(0.5, 0),
                               new Vector2(0, 0.5),
                               new Vector2(1, 0.5),
                               new Vector2(0.5, 1));
        }

        public
        Pair<Vector2, Vector2> connectOver(Rect other, Vector2... options) {
            float best = Float.POSITIVE_INFINITY;
            Pair<Vector2, Vector2> bestIs = null;
            for (int i = 0; i < options.length; i++) {
                Vector3 a = this.convertFromNDC(options[i].toVector3());
                for (int j = 0; j < options.length; j++) {
                    Vector3 b = other.convertFromNDC(options[j].toVector3());
                    float d = a.distanceFrom(b);
                    if (d < best) {
                        best = d;
                        bestIs = new Pair<Vector2, Vector2>(a.toVector2(), b.toVector2());
                    }
                }
            }
            return bestIs;
        }

    }

    public static
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
            new IVisualElementOverrides.MakeDispatchProxy().getBackwardsOverrideProxyFor(on).deleteProperty(from, this);
            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(on).deleteProperty(from, this);
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

            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(from).getProperty(e, this, ref);
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
            new IVisualElementOverrides.MakeDispatchProxy().getBackwardsOverrideProxyFor(on)
                                                           .setProperty(from, this, ref);
            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(on).setProperty(from, this, ref);
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

    public static final VisualElementProperty<String> name = new VisualElementProperty<String>("name");

    public static final VisualElementProperty<IVisualElementOverrides> overrides =
            new VisualElementProperty<IVisualElementOverrides>("overrides");

    public static final VisualElementProperty<SelectionGroup<iComponent>> selectionGroup =
            new VisualElementProperty<SelectionGroup<iComponent>>("selectionGroup");

    public static final VisualElementProperty<SelectionGroup<iComponent>> markingGroup =
            new VisualElementProperty<SelectionGroup<iComponent>>("markingGroup");

    public static final VisualElementProperty<GLComponentWindow> enclosingFrame =
            new VisualElementProperty<GLComponentWindow>("enclosingFrame");

    public static final VisualElementProperty<RootComponent> rootComponent =
            new VisualElementProperty<RootComponent>("rootComponent");

    public static final VisualElementProperty<GlassComponent> glassComponent =
            new VisualElementProperty<GlassComponent>("glassComponent");

    public static final VisualElementProperty<ToolPalette2> toolPalette2 =
            new VisualElementProperty<ToolPalette2>("toolPalette2");

    public static final VisualElementProperty<StandardFluidSheet> sheetView =
            new VisualElementProperty<StandardFluidSheet>("sheetView");

    public static final VisualElementProperty<iComponent> localView =
            new VisualElementProperty<iComponent>("localView");

    public static final VisualElementProperty<Boolean> dirty = new VisualElementProperty<Boolean>("dirty");

    public static final VisualElementProperty<Boolean> hidden = new VisualElementProperty<Boolean>("hidden");

    public static final VisualElementProperty<Object> creationToken =
            new VisualElementProperty<Object>("creationToken");

    public static final VisualElementProperty<Boolean> doNotSave = new VisualElementProperty<Boolean>("doNotSave");
    public static final VisualElementProperty<IVisualElement> timeSlider =
            new VisualElementProperty<IVisualElement>("timeSlider");

    public static final VisualElementProperty<FluidCopyPastePersistence> copyPaste =
            new VisualElementProperty<FluidCopyPastePersistence>("copyPaste");

    public static final VisualElementProperty<Boolean> hasFocusLock =
            new VisualElementProperty<Boolean>("hasFocusLock_");

    public static final VisualElementProperty<iLinearGraphicsContext> fastContext =
            new VisualElementProperty<iLinearGraphicsContext>("fastContext");

    public static final VisualElementProperty<MarkingMenuBuilder> spaceMenu =
            new VisualElementProperty<MarkingMenuBuilder>("spaceMenu_");

    public static final VisualElementProperty<Number> isRenderer = new VisualElementProperty<Number>("isRenderer");
    public static final VisualElementProperty<BasicRunner> multithreadedRunner =
            new VisualElementProperty<BasicRunner>("multithreadedRunner");


    public static final VisualElementProperty<Vector4> color1 = new VisualElementProperty<Vector4>("color1");
    public static final VisualElementProperty<Vector4> color2 = new VisualElementProperty<Vector4>("color2");
    public static final VisualElementProperty<Object> visibleInPreview =
            new VisualElementProperty<Object>("visibleInPreview");

    public static final VisualElementProperty<String> boundTo = new VisualElementProperty<String>("boundTo");

    public
    <T> void deleteProperty(VisualElementProperty<T> p);

    public
    void dispose();

    public
    Rect getFrame(Rect out);

    public
    <T> T getProperty(VisualElementProperty<T> p);

    public
    String getUniqueID();

    public
    void setFrame(Rect out);

    public
    <T> IVisualElement setProperty(VisualElementProperty<T> p, T to);

    public
    void setUniqueID(String uid);

}
