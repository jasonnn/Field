package field.core.plugins.pseudo;

import field.core.StandardFluidSheet;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner;
import field.core.dispatch.FastVisualElementOverridesPropertyCombiner.iCombiner;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.Ref;
import field.core.execution.PythonInterface;
import field.core.plugins.BaseSimplePlugin;
import field.core.plugins.history.HGVersioningSystem;
import field.core.plugins.history.VersioningSystem;
import field.core.plugins.python.Action;
import field.core.plugins.python.PythonPlugin;
import field.core.util.FieldPyObjectAdaptor.iCallable;
import field.core.util.FieldPyObjectAdaptor.iHandlesAttributes;
import field.core.util.FieldPyObjectAdaptor.iHandlesFindItem;
import field.math.graph.IMutable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.WorkspaceDirectory;
import org.jetbrains.annotations.NotNull;
import org.python.core.Py;
import org.python.core.PyObject;

import java.io.File;
import java.util.*;

public
class PseudoPropertiesPlugin extends BaseSimplePlugin {

    // static
    // {
    // FieldPyObjectAdaptor2.isHandlesAttributes(Finder.class);
    // FieldPyObjectAdaptor2.isHandlesAttributes(SuperPropertier.class);
    // FieldPyObjectAdaptor2.isHandlesAttributes(Wherer.class);
    // FieldPyObjectAdaptor2.isHandlesFindItem(Finder.class);
    // }

    public
    class Finder implements iHandlesAttributes, iHandlesFindItem {

        private final List<IVisualElement> all;

        public
        Finder(List<IVisualElement> all) {
            this.all = all;
        }

        public
        Object getAttribute(String name) {
            return Py.None;
        }

        public
        Object getItem(Object object) {

            //System.out.println(" get item in finder called <" + object + ">");

            if ((object == null) || (object == Py.None)) return all;
            String r = object.toString();
            List<IVisualElement> found = StandardFluidSheet.findVisualElementWithNameExpression(root, r);
            PythonInterface.getPythonInterface().setVariable("__tmpFinderValue", found);

            return PythonInterface.getPythonInterface().eval("wl(__tmpFinderValue)");
        }

        public
        void setAttribute(String name, Object value) {
        }

        public
        void setItem(Object name, Object value) {
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 lets you find elements by name. For example <b>_self.find['something.*else']</b> \u2014\u2014";
        }
    }

    public static
    class Framer {

        private final IVisualElement on;
        private Rect r;

        public
        Framer(IVisualElement on, Rect r) {
            this.on = on;
            this.r = r;
        }

        public
        double getH() {
            r = on.getFrame(null);
            if (r == null) return 0;
            return r.h;
        }

        public
        double getW() {
            r = on.getFrame(null);
            if (r == null) return 0;
            return r.w;
        }

        public
        double getX() {
            r = on.getFrame(null);
            if (r == null) return 0;
            return r.x;
        }

        public
        double getY() {
            r = on.getFrame(null);
            if (r == null) return 0;
            return r.y;
        }

        public
        void setH(double x) {
            Rect was = new Rect(0, 0, 0, 0).setValue(r);
            r.h = x;
            on.getProperty(IVisualElement.overrides).shouldChangeFrame(on, r, was, true);
            on.setProperty(IVisualElement.dirty, true);
        }

        public
        void setW(double x) {
            Rect was = new Rect(0, 0, 0, 0).setValue(r);
            r.w = x;
            on.getProperty(IVisualElement.overrides).shouldChangeFrame(on, r, was, true);
            on.setProperty(IVisualElement.dirty, true);
        }

        public
        void setX(double x) {
            Rect was = new Rect(0, 0, 0, 0).setValue(r);
            r.x = x;
            on.getProperty(IVisualElement.overrides).shouldChangeFrame(on, r, was, true);
            on.setProperty(IVisualElement.dirty, true);
        }

        public
        void setY(double x) {
            Rect was = new Rect(0, 0, 0, 0).setValue(r);
            r.y = x;
            on.getProperty(IVisualElement.overrides).shouldChangeFrame(on, r, was, true);
            on.setProperty(IVisualElement.dirty, true);
        }

        @Override
        public
        String toString() {
            return "rectangle <b>" + on.getFrame(null) + "</b>";
        }
    }

    public
    class PropertyInjectionOverride extends DefaultOverride {

        @Override
        public
        <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
            if (!properties.contains(prop)) return super.getProperty(source, prop, ref);

            if (prop.equals(frame)) return getFrame(source, ref);
            if (prop.equals(subelements)) return getSubelements(source, ref);
            if (prop.equals(superelements)) return getSuperelements(source, ref);
            if (prop.equals(root_)) return getRoot(source, ref);
            if (prop.equals(collect)) return getCollector(source, ref);
            if (prop.equals(all)) return getAll(source, ref);
            if (prop.equals(find)) return getFinder(source, ref);
            if (prop.equals(where)) return getWherer(source, ref);
            if (prop.equals(superproperties)) return getSuperpropertier(source, ref);
            if (prop.equals(begin)) return getBeginner(source, ref);
            if (prop.equals(end)) return getEnder(source, ref);
            if (prop.equals(action)) return getActioner(source, ref);

            if (prop.equals(dataFolder)) return getDataFolder(source, (Ref<String>) ref);
            if (prop.equals(workspaceFolder)) return getWorkspaceFolder(source, (Ref<String>) ref);
            if (prop.equals(sheetFolder)) return getSheetFolder(source, (Ref<String>) ref);
            if (prop.equals(sheetDataFolder)) return getSheetDataFolder(source, (Ref<String>) ref);

            return super.getProperty(source, prop, ref);
        }

    }

    public static
    TraversalHint getDataFolder(IVisualElement source, Ref<String> r) {

        String w = WorkspaceDirectory.dir[0];
        String d = w + "/data";
        File dataDir = new File(d);
        if (!dataDir.exists()) {
            boolean made = dataDir.mkdir();
            VersioningSystem vs = StandardFluidSheet.versioningSystem.get(source);
            if (vs != null) {
                ((HGVersioningSystem) vs).scmAddFile(dataDir);
            }
        }

        String p = dataDir.getAbsolutePath();
        if (!p.endsWith("/")) p = p + "/";
        r.set(p);

        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getWorkspaceFolder(IVisualElement source, Ref<String> r) {
        String w = WorkspaceDirectory.dir[0];
        if (!w.endsWith("/")) w = w + "/";
        r.set(w);
        return StandardTraversalHint.STOP;
    }

    public static
    TraversalHint getSheetFolder(IVisualElement source, Ref<String> r) {
        VersioningSystem vs = StandardFluidSheet.versioningSystem.get(source);
        if (vs != null) {
            r.set(vs.getSheetPathName().replace("sheet.xml", "") + "/");
        }
        return StandardTraversalHint.STOP;
    }

    public static
    TraversalHint getSheetDataFolder(IVisualElement source, Ref<String> r) {
        getSheetFolder(source, r);
        String w = r.get();
        String d = w + "/data";
        File dataDir = new File(d);
        if (!dataDir.exists()) {
            boolean made = dataDir.mkdir();
            VersioningSystem vs = StandardFluidSheet.versioningSystem.get(source);
            if (vs != null) {
                ((HGVersioningSystem) vs).scmAddFile(dataDir);
            }
        }

        String p = dataDir.getAbsolutePath();
        if (!p.endsWith("/")) p = p + "/";
        r.set(p);
        return StandardTraversalHint.STOP;
    }

    public static
    class SuperPropertier implements iHandlesAttributes {

        private final IVisualElement on;

        public
        SuperPropertier(IVisualElement on) {
            this.on = on;
        }

        public
        Object getAttribute(String name) {
            return new VisualElementProperty(name).getAbove(on);
        }

        public
        void setAttribute(String name, Object value) {
            // todo, throw error ?
        }

        public
        void setItem(Object name, Object value) {
            // todo, throw error ?
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 Lets you access properties, while skipping the local level,  for example: <b>_self.superproperties.someProperty_</b> \u2014\u2014";
        }

    }

    public static
    class Wherer implements iHandlesAttributes {

        private final IVisualElement from;

        public
        Wherer(IVisualElement from) {
            this.from = from;
        }

        public
        Object getAttribute(String name) {
            String n = PythonPlugin.externalPropertyNameToInternalName(name);
            VisualElementProperty v = new VisualElementProperty(n);
            Ref ref = v.getRef(from);
            return ref.getStorageSource();
        }

        public
        void setAttribute(String name, Object value) {
        }

        public
        void setItem(Object name, Object value) {
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 Doesn't return the property, returns the visual element that actually has the property stored.  for example: <b>_self.where.someProperty_</b> \u2014\u2014";
        }
    }

    public static
    class Collector implements iHandlesAttributes {

        private final IVisualElement from;
        FastVisualElementOverridesPropertyCombiner<Object, List<Object>> combine =
                new FastVisualElementOverridesPropertyCombiner<Object, List<Object>>(false) {
                    protected
                    java.util.Collection<? extends IVisualElement> sort(java.util.List<? extends IMutable<IVisualElement>> parents) {

                        ArrayList<IMutable<IVisualElement>> a =
                                new ArrayList<IMutable<IVisualElement>>(parents);
                        Collections.sort(a, new Comparator<IMutable<IVisualElement>>() {

                            @Override
                            public
                            int compare(IMutable<IVisualElement> o1, IMutable<IVisualElement> o2) {
                                if (o1 == null || ((IVisualElement) o1).getFrame(null) == null) return -1;
                                if (o2 == null || ((IVisualElement) o2).getFrame(null) == null) return 1;


                                float dy = (float) Math.abs(((IVisualElement) o1).getFrame(null).y
                                                            - ((IVisualElement) o2).getFrame(null).y);
                                float dx = (float) Math.abs(((IVisualElement) o1).getFrame(null).x
                                                            - ((IVisualElement) o2).getFrame(null).x);

                                if (dy > dx) return Double.compare(((IVisualElement) o1).getFrame(null).y,
                                                                   ((IVisualElement) o2).getFrame(null).y);
                                else return Double.compare(((IVisualElement) o1).getFrame(null).x,
                                                           ((IVisualElement) o2).getFrame(null).x);
                            }
                        });
                        return (Collection) a;
                    }
                };

        public
        Collector(IVisualElement from) {
            this.from = from;
        }

        public
        Object getAttribute(String name) {
            String n = PythonPlugin.externalPropertyNameToInternalName(name);

            List<Object> x = combine.getProperty(from,
                                                 new VisualElementProperty<Object>(n),
                                                 new iCombiner<Object, List<Object>>() {

                                                     @Override
                                                     public
                                                     List<Object> unit() {
                                                         return new ArrayList<Object>();
                                                     }

                                                     @Override
                                                     public
                                                     List<Object> bind(List<Object> t, Object u) {
                                                         if (u != null) t.add(u);
                                                         return t;
                                                     }
                                                 });

// if (x.size()>0 && x.get(0) instanceof iVisualElement)
// {
// Collections.sort(x, new Comparator(){
//
// @Override
// public int compare(Object arg0, Object arg1) {
// if (arg0 instanceof iVisualElement && arg1 instanceof
// iVisualElement)
// {
// return Double.compare(
// ((iVisualElement)arg0).getFrame(null).y,
// ((iVisualElement)arg1).getFrame(null).y);
// }
// return 0;
// }});
// }

            return x;
        }

        public
        Object getAttribute(String name, final Class tojava) {
            String n = PythonPlugin.externalPropertyNameToInternalName(name);

            List<Object> x = combine.getProperty(from,
                                                 new VisualElementProperty<Object>(n),
                                                 new iCombiner<Object, List<Object>>() {

                                                     @Override
                                                     public
                                                     List<Object> unit() {
                                                         return new ArrayList<Object>();
                                                     }

                                                     @Override
                                                     public
                                                     List<Object> bind(List<Object> t, Object u) {

                                                         if (u instanceof PyObject) u = Py.tojava((PyObject) u, tojava);

                                                         if (u != null) t.add(u);
                                                         return t;
                                                     }
                                                 });

// if (x.size()>0 && x.get(0) instanceof iVisualElement)
// {
// Collections.sort(x, new Comparator(){
//
// @Override
// public int compare(Object arg0, Object arg1) {
// if (arg0 instanceof iVisualElement && arg1 instanceof
// iVisualElement)
// {
// return Double.compare(
// ((iVisualElement)arg0).getFrame(null).y,
// ((iVisualElement)arg1).getFrame(null).y);
// }
// return 0;
// }});
// }

            return x;
        }

        public
        void setAttribute(String name, Object value) {
        }

        public
        void setItem(Object name, Object value) {
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 Doesn't return the property, returns the visual element that actually has the property stored.  for example: <b>_self.where.someProperty_</b> \u2014\u2014";
        }
    }

    public static
    class Actioner implements iHandlesAttributes {

        private final IVisualElement from;

        public
        Actioner(IVisualElement from) {
            this.from = from;
        }

        public
        Object getAttribute(String name) {

            VisualElementProperty<Action> v = new VisualElementProperty<Action>(name);
            Action m = v.get(from);

            if (m == null) {
                v.set(from, from, m = new Action());
            }

            return m;
        }

        public
        void setAttribute(String name, Object value) {
        }

        public
        void setItem(Object name, Object value) {
        }

        @Override
        public
        String toString() {
            return "Creates actions on demand";
        }
    }

    public static
    class Subelements implements iHandlesFindItem, List<IVisualElement> {

        protected final IVisualElement from;

        List<IVisualElement> current;

        public
        Subelements(IVisualElement from) {
            this.from = from;
            refresh();
        }

        protected
        void refresh() {
            current = (List<IVisualElement>) from.getParents();
        }

        @Override
        public
        String toString() {
            return current
                   + " (a list (and a map) of elements that delegate to this element. You can add and delete thrings from the list to change the delegation tree of Field)";
        }

        public
        boolean add(IVisualElement e) {
            connect(e);
            refresh();
            return true;
        }

        public
        void add(int index, IVisualElement element) {

            connect(element);
            refresh();
        }

        protected
        void connect(IVisualElement element) {
            element.addChild(from);
        }

        public
        boolean addAll(Collection<? extends IVisualElement> c) {
            for (IVisualElement v : c)
                add(v);
            return true;
        }

        public
        boolean addAll(int index, Collection<? extends IVisualElement> c) {
            for (IVisualElement v : c)
                add(v);
            return true;
        }

        public
        void clear() {
            for (IVisualElement v : new ArrayList<IVisualElement>(current))
                disconnect(v);
        }

        protected
        void disconnect(IVisualElement v) {
            v.removeChild(from);
        }

        public
        boolean contains(Object o) {
            return current.contains(o);
        }

        public
        boolean containsAll(Collection<?> c) {
            return current.contains(c);
        }

        public
        IVisualElement get(int index) {
            return current.get(index);
        }

        public
        int indexOf(Object o) {
            return current.indexOf(o);
        }

        public
        boolean isEmpty() {
            return current.isEmpty();
        }

        @NotNull
        public
        Iterator<IVisualElement> iterator() {
            return current.iterator();
        }

        public
        int lastIndexOf(Object o) {
            return current.lastIndexOf(o);
        }

        @NotNull
        public
        ListIterator<IVisualElement> listIterator() {
            return current.listIterator();
        }

        @NotNull
        public
        ListIterator<IVisualElement> listIterator(int index) {
            return current.listIterator(index);
        }

        public
        boolean remove(Object o) {
            disconnect((IVisualElement) o);
            refresh();
            return true;
        }

        public
        IVisualElement remove(int index) {
            IVisualElement r = current.get(index);
            disconnect(current.get(index));
            refresh();
            return r;
        }

        public
        boolean removeAll(Collection<?> c) {
            for (Object o : c)
                remove(o);
            return true;
        }

        public
        boolean retainAll(Collection<?> c) {
            throw new IllegalStateException(" not implemented");
        }

        public
        IVisualElement set(int index, IVisualElement element) {
            IVisualElement a = current.get(index);
            remove(a);
            add(index, element);
            return a;
        }

        public
        int size() {
            return current.size();
        }

        @NotNull
        public
        List<IVisualElement> subList(int fromIndex, int toIndex) {
            return current.subList(fromIndex, toIndex);
        }

        @NotNull
        public
        Object[] toArray() {
            return current.toArray();
        }

        @NotNull
        public
        <T> T[] toArray(T[] a) {
            return current.toArray(a);
        }

        public
        Object getItem(Object object) {
            if (object instanceof Number) return get(((Number) object).intValue());
            for (IVisualElement e : current) {
                String nn = e.getProperty(IVisualElement.name);
                if (nn != null && nn.equals(object)) return e;
            }
            return null;
        }

        public
        void setItem(Object name, Object value) {
            throw new IllegalStateException(" can't call set item to change topology ");
        }

        public
        Object getAttribute(String name) {
            return getItem(name);
        }

        public
        void setAttribute(String name, Object value) {
            throw new IllegalStateException(" can't call set item to change topology ");
        }

        public
        List<IVisualElement> values() {
            return this;
        }

    }

    public
    class Superelements extends Subelements {

        public
        Superelements(IVisualElement from) {
            super(from);
        }

        @Override
        protected
        void refresh() {
            current = from.getChildren();
        }

        @Override
        public
        String toString() {
            return current
                   + "(a list (and a map) of elements this element delegates to . You can add and delete thrings from the list to change the delegation tree of Field)";
        }

        protected
        void connect(IVisualElement element) {
            from.addChild(element);
        }

        @Override
        protected
        void disconnect(IVisualElement v) {
            from.removeChild(v);
        }

    }

// static
// {
// FieldPyObjectAdaptor2.isCallable(Ender.class);
// FieldPyObjectAdaptor2.isCallable(Beginner.class);
// }

    public static
    class Ender implements iCallable {
        private final IVisualElement source;

        public
        Ender(IVisualElement source) {
            this.source = source;
        }

        public
        Object call(Object[] args) {
            if (args.length == 0) {
                IVisualElement old = IVisualElementOverrides.topology.setAt(source);
                IVisualElementOverrides.forward.endExecution.endExecution(source);
                IVisualElementOverrides.topology.setAt(old);
            }
            return Py.None;
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 if you 'call' this property that box will STOP running. For example <b>_self.find['something'].end()</b> \u2014\u2014";
        }
    }

    public static
    class Beginner implements iCallable {
        private final IVisualElement source;

        public
        Beginner(IVisualElement source) {
            this.source = source;
        }

        public
        Object call(Object[] args) {
            if (args.length == 0) {
                IVisualElement old = IVisualElementOverrides.topology.setAt(source);
                IVisualElementOverrides.forward.beginExecution.beginExecution(source);
                IVisualElementOverrides.topology.setAt(old);
            }
            return Py.None;
        }

        @Override
        public
        String toString() {
            return "\u2014\u2014 if you 'call' this property that box will start running. For example <b>_self.find['something'].begin()</b> \u2014\u2014";
        }
    }

    public static final VisualElementProperty<Rect> frame = new VisualElementProperty<Rect>("frame");
    public static final VisualElementProperty<Map<String, IVisualElement>> subelements =
            new VisualElementProperty<Map<String, IVisualElement>>("subelements");
    public static final VisualElementProperty<Map<String, IVisualElement>> superelements =
            new VisualElementProperty<Map<String, IVisualElement>>("superelements");
    public static final VisualElementProperty<IVisualElement> root_ = new VisualElementProperty<IVisualElement>("root");
    public static final VisualElementProperty<IVisualElement> all = new VisualElementProperty<IVisualElement>("all");
    public static final VisualElementProperty<Object> find = new VisualElementProperty<Object>("find");
    public static final VisualElementProperty<List> collect = new VisualElementProperty<List>("collect");
    public static final VisualElementProperty<Wherer> where = new VisualElementProperty<Wherer>("where");
    public static final VisualElementProperty<Object> superproperties =
            new VisualElementProperty<Object>("superproperties");
    public static final VisualElementProperty<Beginner> begin = new VisualElementProperty<Beginner>("begin");
    public static final VisualElementProperty<Ender> end = new VisualElementProperty<Ender>("end");

    public static final VisualElementProperty<Actioner> action = new VisualElementProperty<Actioner>("action");

    public static final VisualElementProperty<String> dataFolder = new VisualElementProperty<String>("dataFolder");
    public static final VisualElementProperty<String> workspaceFolder =
            new VisualElementProperty<String>("workspaceFolder");
    public static final VisualElementProperty<String> sheetDataFolder =
            new VisualElementProperty<String>("sheetDataFolder");
    public static final VisualElementProperty<String> sheetFolder = new VisualElementProperty<String>("sheetFolder");

    public static final LinkedHashSet<VisualElementProperty> properties =
            new LinkedHashSet<VisualElementProperty>(Arrays.asList(new VisualElementProperty[]{frame,
                                                                                               subelements,
                                                                                               superelements,
                                                                                               root_,
                                                                                               all,
                                                                                               find,
                                                                                               where,
                                                                                               superproperties,
                                                                                               begin,
                                                                                               end,
                                                                                               dataFolder,
                                                                                               workspaceFolder,
                                                                                               sheetDataFolder,
                                                                                               sheetFolder,
                                                                                               action,
                                                                                               collect}));

    public
    TraversalHint getAll(IVisualElement source, Ref t) {
        t.set(StandardFluidSheet.allVisualElements(root));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getFinder(IVisualElement source, Ref t) {
        t.set(new Finder(StandardFluidSheet.allVisualElements(root)));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getFrame(IVisualElement source, Ref t) {
        t.set(new Framer(source, source.getFrame(null)));
        return StandardTraversalHint.STOP;
    }

    public static
    TraversalHint getCollector(IVisualElement source, Ref t) {
        t.set(new Collector(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getRoot(IVisualElement source, Ref t) {
        t.set(root);
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getSubelements(IVisualElement source, Ref t) {
        t.set(new Subelements(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getSuperelements(IVisualElement source, Ref t) {
        t.set(new Superelements(source));
        return StandardTraversalHint.STOP;
    }

    private
    Map<String, IVisualElement> buildMap(List<IVisualElement> children) {
        HashMap<String, IVisualElement> m = new HashMap<String, IVisualElement>();
        for (IVisualElement v : children) {
            m.put(v.getProperty(IVisualElement.name), v);
        }
        return m;
    }

    public
    TraversalHint getSuperpropertier(IVisualElement source, Ref t) {
        t.set(new SuperPropertier(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getWherer(IVisualElement source, Ref t) {
        t.set(new Wherer(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getBeginner(IVisualElement source, Ref t) {
        t.set(new Beginner(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getEnder(IVisualElement source, Ref t) {
        t.set(new Ender(source));
        return StandardTraversalHint.STOP;
    }

    public
    TraversalHint getActioner(IVisualElement source, Ref t) {
        t.set(new Actioner(source));
        return StandardTraversalHint.STOP;
    }

    @Override
    protected
    String getPluginNameImpl() {
        return "pseudoproperties";
    }

    @Override
    protected
    DefaultOverride newVisualElementOverrides() {
        return new PropertyInjectionOverride();
    }

}
