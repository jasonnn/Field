package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.dispatch.IVisualElementOverrides.DefaultOverride;
import field.core.dispatch.iVisualElementOverrides_m;
import field.core.plugins.python.PythonPlugin.CapturedEnvironment;
import field.launch.IUpdateable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.util.Dict;
import field.util.HashMapOfLists;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;

import java.lang.reflect.Method;
import java.util.*;

public
class PythonOverridden extends DefaultOverride {

    public abstract static
    class Callable {
        public final Object source;
        public String name;
        Dict info;

        public
        Callable(Object o, String name) {
            this.source = o;
            this.name = name;
        }

        public abstract
        Object call(Method m, Object[] args);

        @Override
        public
        int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public
        boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            Callable other = (Callable) obj;
            if (name == null) {
                if (other.name != null) return false;
            }
            else if (!name.equals(other.name)) return false;
            return true;
        }

        @Override
        public
        String toString() {
            return name + "//" + System.identityHashCode(this) + ':' + source;
        }

        public
        Dict getInfo() {
            if (info == null) info = new Dict();
            return info;
        }

    }

    public static Object removeMe = new Object();

    transient HashMapOfLists<String, Callable> methods = new HashMapOfLists<String, Callable>() {
        @Override
        protected
        Collection<Callable> newList() {
            return new ArrayList<Callable>();
        }
    };


    public
    void add(String methodname, PyFunction call) {
        Callable c = callableForFunction(call);
        // System.out.println(" adding <" + methodname + "> <" + call +
        // "> <" + c + ">");
        // methods.addToList(methodname, c);
        Collection<Callable> cc = methods.getCollection(methodname);
        if (cc != null) {
            Iterator<Callable> ccc = cc.iterator();
            while (ccc.hasNext()) {
                Callable n = ccc.next();
                if (n.equals(c)) ccc.remove();
            }
        }
        methods.addToList(methodname, c);

        // System.out.println("  now <" + methods + ">");
    }

    @Override
    public
    TraversalHint added(IVisualElement newSource) {
        Method method = iVisualElementOverrides_m.added_m;
        Object[] args = {newSource};
        String methodName = "added";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint beginExecution(IVisualElement source) {
        Method method = iVisualElementOverrides_m.beginExecution_m;
        Object[] args = {source};
        String methodName = "beginExecution";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint deleted(IVisualElement source) {
        Method method = iVisualElementOverrides_m.deleted_m;
        Object[] args = {source};
        String methodName = "deleted";

        return call(method, args, methodName);
    }

    @Override
    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
        Method method = iVisualElementOverrides_m.deleteProperty_m;
        Object[] args = {source, prop};
        String methodName = "deleteProperty";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint endExecution(IVisualElement source) {
        Method method = iVisualElementOverrides_m.endExecution_m;
        Object[] args = {source};
        String methodName = "endExecution";

        return call(method, args, methodName);
    }

    @Override
    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
        Method method = iVisualElementOverrides_m.getProperty_m;
        Object[] args = {source, prop, ref};
        String methodName = "getProperty";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, org.eclipse.swt.widgets.Event event) {
        Method method = iVisualElementOverrides_m.handleKeyboardEvent_m;
        Object[] args = {newSource, event};
        String methodName = "handleKeyboardEvent";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
        Method method = iVisualElementOverrides_m.menuItemsFor_m;
        Object[] args = {source, items};
        String methodName = "menuItemsFor";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        Method method = iVisualElementOverrides_m.paintNow_m;
        Object[] args = {source, bounds, visible};
        String methodName = "paintNow";

        return call(method, args, methodName);
    }

    public
    void replace(String methodname, PyFunction call) {
        methods.remove(methodname);
        methods.addToList(methodname, callableForFunction(call));
    }

    @Override
    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
        Method method = iVisualElementOverrides_m.setProperty_m;
        Object[] args = {source, prop, to};
        String methodName = "setProperty";

        return call(method, args, methodName);
    }

    @Override
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        Method method = iVisualElementOverrides_m.shouldChangeFrame_m;
        Object[] args = {source, newFrame, oldFrame, now};
        String methodName = "shouldChangeFrame";

        return call(method, args, methodName);
    }

    private
    TraversalHint call(Method method, Object[] args, String methodName) {
        Collection<Callable> c = methods.get(methodName);
        if (c == null || c.size() == 0) return StandardTraversalHint.CONTINUE;

        HashMap<Callable, Throwable> faulted = null;
        for (Callable cc : c) {
            try {
                Object r = cc.call(method, args);

                if (r == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                if (r == StandardTraversalHint.STOP) return StandardTraversalHint.SKIP;
                if (r == removeMe) faulted.put(cc, null);

            } catch (Throwable t) {
                t.printStackTrace();
                if (faulted == null) faulted = new HashMap<Callable, Throwable>();
                faulted.put(cc, t);
            }
        }
        if (faulted != null) {
            notifyFaulted(faulted);
            for (Callable f : faulted.keySet()) {
                methods.remove(methodName, f);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public static
    Callable callableForFunction(final PyFunction call) {
        return new Callable(call, call.__name__) {
            @Override
            public
            Object call(Method arg0, Object[] arg1) {
                PyObject[] objects = new PyObject[arg1.length];
                for (int i = 0; i < objects.length; i++) {
                    Object a = arg1[i];
                    objects[i] = Py.java2py(a);
                }

                PyObject o = call.__call__(objects);
                if (o == null) return null;
                if (o == Py.None) return null;
                if (o == Py.Zero) return removeMe;
                return o.__tojava__(Object.class);
            }
        };
    }

    public static
    Callable callableForFunction(final PyObject call, final CapturedEnvironment e) {
        return new Callable(call,
                            call instanceof PyFunction
                            ? ((PyFunction) call).__name__
                            : (String.valueOf(call.hashCode()))) {
            @Override
            public
            Object call(Method arg0, Object[] arg1) {
                if (e != null) e.enter();
                try {
                    PyObject[] objects = new PyObject[arg1 == null ? 0 : arg1.length];
                    for (int i = 0; i < objects.length; i++) {
                        Object a = arg1[i];
                        objects[i] = Py.java2py(a);
                    }

                    PyObject o = call.__call__(objects);
                    if (o == null) return null;
                    if (o == Py.None) return null;
                    if (o == Py.Zero) return removeMe;
                    return o.__tojava__(Object.class);
                } finally {
                    if (e != null) e.exit();
                }
            }
        };
    }

    public static
    Callable callableForUpdatable(String name, final IUpdateable up) {
        return new Callable(null, name) {
            @Override
            public
            Object call(Method arg0, Object[] arg1) {
                up.update();
                return null;
            }
        };
    }

    private
    void notifyFaulted(HashMap<Callable, Throwable> faulted) {

    }

    public static
    Callable callableForFunction(String name, final IFunction f) {
        return new Callable(f, name) {

            @Override
            public
            Object call(Method m, Object[] args) {
                return f.apply(args[0]);
            }
        };
    }

}
