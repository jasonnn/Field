package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElementOverrides;
import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.persistance.VisualElementReference;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.math.graph.IMutableContainer;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Pair;
import field.util.HashMapOfLists;
import field.util.RectangleAllocator;

import java.util.*;
import java.util.Map.Entry;

public
class SimpleConstraints implements iPlugin {

    public static
    class AtPoint extends Constraint {
        private final float x;

        private final float y;

        private final float width;

        private final float height;

        private final float ox;

        private final float oy;

        public
        AtPoint(IVisualElement fireOn, IVisualElement inside, IVisualElement control, float ox, float oy) {
            super(fireOn, inside, control);
            // this.oy = oy;
            // this.ox = ox;
            Rect oldParentFrame = inside.getFrame(null);
            Rect oldFrame = control.getFrame(null);
            this.width = (float) oldFrame.w;
            this.height = (float) oldFrame.h;

            // this.x = (float)
            // ((oldFrame.x-oldParentFrame.x)/oldParentFrame.w);
            // this.y = (float)
            // ((oldFrame.y-oldParentFrame.y)/oldParentFrame.h);

            this.x = 1;
            this.y = 0;
            this.ox = -(float) (-ox + oldFrame.x - oldParentFrame.x - oldParentFrame.w);
            this.oy = -(float) (-oy + oldFrame.y - oldParentFrame.y);

            Rect newFrame = new Rect(5 + oldParentFrame.x + x * oldParentFrame.w - ox,
                                     oldParentFrame.y + y * oldParentFrame.h - oy,
                                     width,
                                     height);
            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(control)
                                                           .shouldChangeFrame(control, newFrame, oldFrame, true);
        }

        public
        AtPoint(IVisualElement fireOn,
                IVisualElement inside,
                IVisualElement control,
                float x,
                float y,
                float width,
                float height,
                float ox,
                float oy) {
            super(fireOn, inside, control);
            this.oy = oy;
            this.ox = ox;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            Rect oldFrame = control.getFrame(null);
            Rect oldParentFrame = inside.getFrame(null);
            Rect newFrame = new Rect(oldParentFrame.x + x * oldParentFrame.w - ox,
                                     oldParentFrame.y + y * oldParentFrame.h - oy,
                                     width,
                                     height);
            new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(control)
                                                           .shouldChangeFrame(control, newFrame, oldFrame, true);
        }

        @Override
        protected
        boolean doFire(IVisualElement root, Rect newRect, Rect oldRect, Rect currentRect) {
            Rect oldParentFrame = from.get(root).getFrame(null);
            Rect newFrame = new Rect(oldParentFrame.x + x * oldParentFrame.w - ox,
                                     oldParentFrame.y + y * oldParentFrame.h - oy,
                                     currentRect.w,
                                     currentRect.h);

            if (newFrame.distanceFrom(currentRect) > 0) {
                currentRect.setValue(newFrame);
                return true;
            }
            return false;
        }
    }

    public static
    class AtPointBelow extends Constraint {

        float oy;

        public
        AtPointBelow(IVisualElement fireOn, IVisualElement inside, IVisualElement control, float x, float y) {
            super(fireOn, inside, control);
            // this.oy = oy;
            // this.ox = ox;
            Rect oldParentFrame = inside.getFrame(null);
            Rect oldFrame = control.getFrame(null);

            oy = (float) (y - (oldParentFrame.y + oldParentFrame.h));
        }

        @Override
        protected
        boolean doFire(IVisualElement root, Rect newRect, Rect oldRect, Rect currentRect) {
            Rect oldParentFrame = from.get(root).getFrame(null);
            float delta = (float) ((newRect.y + newRect.h) - (oldRect.y + oldRect.h));

//			Rect newFrame = new Rect(oldParentFrame.x, oldParentFrame.y + oldParentFrame.h + oy, oldParentFrame.w, currentRect.h);
            Rect newFrame = new Rect(oldParentFrame.x, currentRect.y + delta, oldParentFrame.w, currentRect.h);

            if (newFrame.distanceFrom(currentRect) > 0) {
                currentRect.setValue(newFrame);
                return true;
            }
            return false;
        }
    }

    public static
    class AtPointBelowMinWidth extends Constraint {

        float oy;
        private final float minWidth;

        public
        AtPointBelowMinWidth(IVisualElement fireOn,
                             IVisualElement inside,
                             IVisualElement control,
                             float x,
                             float y,
                             float minWidth) {
            super(fireOn, inside, control);
            this.minWidth = minWidth;
            // this.oy = oy;
            // this.ox = ox;
            Rect oldParentFrame = inside.getFrame(null);
            Rect oldFrame = control.getFrame(null);

            oy = (float) (y - (oldParentFrame.y + oldParentFrame.h));
        }

        @Override
        protected
        boolean doFire(IVisualElement root, Rect newRect, Rect oldRect, Rect currentRect) {
            Rect oldParentFrame = from.get(root).getFrame(null);

            Rect newFrame = new Rect(oldParentFrame.x,
                                     oldParentFrame.y + oldParentFrame.h + oy,
                                     Math.max(minWidth, oldParentFrame.w),
                                     currentRect.h);

            if (newFrame.distanceFrom(currentRect) > 0) {
                currentRect.setValue(newFrame);
                return true;
            }
            return false;
        }
    }

    public static
    class RectangleAllocatorConstraint extends Constraint {

        private final VisualElementProperty<RectangleAllocator> allocator;

        public
        RectangleAllocatorConstraint(IVisualElement fireOn,
                                     IVisualElement from,
                                     IVisualElement to,
                                     VisualElementProperty<RectangleAllocator> allocator) {
            super(fireOn, from, to);
            this.allocator = allocator;
        }

        @Override
        protected
        boolean doFire(IVisualElement root, Rect newRect, Rect oldRect, Rect currentRect) {
            Rect oldParentFrame = from.get(root).getFrame(null);

            RectangleAllocator a = allocator.get(from.get(root));
            if (a == null) return false;

            Rect r = new Rect(oldParentFrame.x + oldParentFrame.w + 10, currentRect.y, currentRect.w, currentRect.h);
            r = a.allocate(to.get(root).getUniqueID(), r, RectangleAllocator.Move.down, 5);
            if (currentRect.equals(r)) return false;

            currentRect.setValue(r);
            return true;
        }

    }

    public abstract static
    class Constraint {
        VisualElementReference fireOn;

        VisualElementReference from;

        VisualElementReference to;

        boolean inside = false;

        public
        Constraint(IVisualElement fireOn, IVisualElement from, IVisualElement to) {
            this.fireOn = new VisualElementReference(fireOn);
            this.from = new VisualElementReference(from);
            this.to = new VisualElementReference(to);
        }

        public
        boolean fire(IVisualElement root, Rect newRect, Rect oldRect) {
            if (inside) return false;
            inside = true;

            if (to == null || to.get(root) == null) return false;
            if (from == null || from.get(root) == null) return false;

            Rect currentRect = to.get(root).getFrame(null);
            Rect oldCurrentRect = to.get(root).getFrame(null);
            boolean b = doFire(root, newRect, oldRect, currentRect);
            if (b) {
                new IVisualElementOverrides.MakeDispatchProxy().getOverrideProxyFor(to.get(root))
                                                               .shouldChangeFrame(to.get(root),
                                                                                  currentRect,
                                                                                  oldCurrentRect,
                                                                                  true);
            }
            inside = false;
            return b;
        }

        protected abstract
        boolean doFire(IVisualElement root, Rect newRect, Rect oldRect, Rect currentRect);
    }

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
        <T> T getProperty(IVisualElement.VisualElementProperty<T> p) {
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
        <T> IVisualElement setProperty(IVisualElement.VisualElementProperty<T> p, T to) {
            properties.put(p, to);
            return this;
        }

        public
        void setUniqueID(String uid) {
        }
    }

    public
    class Overrides extends IVisualElementOverrides.DefaultOverride {
        @Override
        public
        TraversalHint deleted(IVisualElement source) {

            constraints.remove(source);
            Iterator<Entry<IVisualElement, Collection<Constraint>>> i = constraints.entrySet().iterator();
            while (i.hasNext()) {
                Entry<IVisualElement, Collection<Constraint>> e = i.next();
                Iterator<Constraint> q = e.getValue().iterator();
                while (q.hasNext()) {
                    Constraint c = q.next();
                    if (c.fireOn == source || c.from == source || c.to == source) {
                        q.remove();
                    }
                }
            }

            return StandardTraversalHint.CONTINUE;
        }

        @Override
        public
        TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
            List<Constraint> list = constraints.getList(source);
            if (list != null) {
                for (Constraint c : list) {
                    c.fire(root, newFrame, oldFrame);
                }
            }
            return StandardTraversalHint.CONTINUE;
        }
    }

    public static final String pluginId = "//plugin_simpleConstraints";

    public static final VisualElementProperty<SimpleConstraints> simpleConstraints_plugin =
            new VisualElementProperty<SimpleConstraints>("simpleConstraints_plugin");

    private final field.core.plugins.SimpleConstraints.LocalVisualElement lve;

    private IVisualElement root;

    private SelectionGroup<iComponent> group;

    HashMapOfLists<IVisualElement, Constraint> constraints = new HashMapOfLists<IVisualElement, Constraint>();

    IVisualElementOverrides elementOverride;

    Map<Object, Object> properties = new HashMap<Object, Object>();

    public
    SimpleConstraints() {
        lve = new LocalVisualElement();

    }

    public
    void addConstraint(Constraint c) {
        constraints.addToList(c.fireOn.get(root), c);
    }

    public
    void close() {
    }

    public
    Object getPersistanceInformation() {
        return new Pair<String, Collection<Collection<Constraint>>>(pluginId + "version_1",
                                                                    new ArrayList(constraints.values()));
    }

    public
    IVisualElement getWellKnownVisualElement(String id) {
        if (id.equals(pluginId)) return lve;
        return null;
    }

    public
    void registeredWith(IVisualElement root) {

        this.root = root;

        // add a next
        // to root that
        // adds some
        // overrides
        root.addChild(lve);

        lve.setProperty(simpleConstraints_plugin, this);
        // register for
        // selection
        // updates? (no,
        // do it in
        // subclass)
        group = root.getProperty(IVisualElement.selectionGroup);

        elementOverride = createElementOverrides();
    }

    public
    void setPersistanceInformation(Object o) {
        if (o instanceof Pair) {
            Pair<String, Collection<Collection<Constraint>>> p = (Pair<String, Collection<Collection<Constraint>>>) o;
            if (p.left.equals(pluginId + "version_1")) {
                for (Collection<Constraint> cc : p.right) {
                    for (Constraint c : cc)
                        constraints.addToList(c.fireOn.get(root), c);
                }
            }
        }
    }

    public
    void update() {
    }

    protected
    IVisualElementOverrides createElementOverrides() {
        return new Overrides().setVisualElement(lve);
    }

}
