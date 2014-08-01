package field.namespace.dispatch;

import field.math.graph.ITopology;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.graph.visitors.TopologyVisitor_breadthFirst;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public
class DispatchOverTopology<T> {
//TODO can these classes safely be changed to top level?
    public
    class And extends TopologyDispatchVaryingMethod<Boolean> {

        public
        And(boolean avoidLoops) {
            super(avoidLoops);
        }

        @Override
        public
        Boolean dispatch(Method method, T on, Object... args) {
            ret = true;
            super.dispatch(method, on, args);
            return ret;
        }

        @Override
        protected
        TraversalHint visit(T root) {
            try {
                Object r = getMethod(root).invoke(root, args);
                if (!((Boolean) r)) {
                    ret = false;
                    return StandardTraversalHint.STOP;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            return StandardTraversalHint.CONTINUE;
        }
    }

    public
    class Interpreted extends RawBase<Object> {

        private final ReturnInterpretation retint;

        public
        Interpreted(boolean avoidLoops, ReturnInterpretation retint) {
            super(avoidLoops);
            this.retint = retint;
        }

        @Override
        protected
        TraversalHint interpretReturn(Object invoke) {
            if (retint == ReturnInterpretation.always) return StandardTraversalHint.CONTINUE;

            if (retint == ReturnInterpretation.untilNotNull) return (invoke != null) ? StandardTraversalHint.STOP : StandardTraversalHint.CONTINUE;
            if (retint == ReturnInterpretation.untilNull) return (invoke != null) ? StandardTraversalHint.CONTINUE : StandardTraversalHint.STOP;
            if (retint == ReturnInterpretation.skipNotNull) return (invoke != null) ? StandardTraversalHint.SKIP : StandardTraversalHint.CONTINUE;
            if (retint == ReturnInterpretation.skipNull) return (invoke != null) ? StandardTraversalHint.CONTINUE : StandardTraversalHint.SKIP;

            return StandardTraversalHint.CONTINUE;
        }

    }

    public
    class Or extends TopologyDispatchVaryingMethod<Boolean> {

        public
        Or(boolean avoidLoops) {
            super(avoidLoops);
        }

        @Override
        public
        Boolean dispatch(Method method, T on, Object... args) {
            ret = false;
            super.dispatch(method, on, args);
            return ret;
        }

        @Override
        public
        Boolean dispatch(T on, Object... args) {
            ret = false;
            super.dispatch(on, args);
            return ret;
        }

        @Override
        protected
        TraversalHint visit(T root) {
            try {
                Object r = getMethod(root).invoke(root, args);
                if (((Boolean) r)) {
                    ret = true;
                    return StandardTraversalHint.STOP;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            return StandardTraversalHint.CONTINUE;
        }
    }

    public
    class Raw extends RawBase<TraversalHint> {
        public
        Raw(boolean avoidLoops) {
            super(avoidLoops);
        }

        @Override
        protected
        TraversalHint interpretReturn(TraversalHint invoke) {
            return invoke;
        }
    }

    public
    class RawBase<R> extends TopologyDispatchVaryingMethod<R> {

        ArrayList<R> allret;

        public
        RawBase(boolean avoidLoops) {
            super(avoidLoops);
        }


        @Override
        public
        void apply(ITopology<T> top, T root) {
            allret = new ArrayList<R>();
            super.apply(top, root);
        }


        public
        ArrayList<R> returns() {
            return allret;
        }

        protected
        Object getObject(T root) {
            return root;
        }

        protected
        TraversalHint interpretReturn(R invoke) {
            if (invoke instanceof TraversalHint) return ((TraversalHint) invoke);
            return StandardTraversalHint.CONTINUE;
        }

        @Override
        protected
        LinkedHashSet<T> maybeWrap(LinkedHashSet<T> f) {
            return new LinkedHashSet<T>(f);
        }

        protected
        void postamble(Object o, Object[] args, Object ret) {
        }

        protected
        void preamble(Object o, Object[] args) {
        }

        @Override
        protected
        TraversalHint visit(T root) {
            try {
                Object o = getObject(root);
                if (o == null) {
                    return StandardTraversalHint.CONTINUE;
                }

                preamble(o, args);
                Method m = getMethod(root);
                R rr = (R) m.invoke(o, args);
                TraversalHint r = interpretReturn(rr);
                postamble(o, args, rr);
                if (trace) ret = rr;
                return r;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {


                throw new IllegalArgumentException(e);
            }
        }

    }

    public
    enum ReturnInterpretation {
        always, untilNotNull, untilNull, skipNotNull, skipNull
    }

    public abstract
    class TopologyDispatchVaryingMethod<R> extends TopologyVisitor_breadthFirst<T> {
        private final boolean maintainCache;

        private final boolean doCache;

        protected Object[] args;

        protected R ret;

        protected Method fixedMethod;

        protected boolean trace = false;

        Map<Class, Method> cache = new HashMap<Class, Method>();

        public
        TopologyDispatchVaryingMethod(boolean avoidLoops) {
            super(avoidLoops);
            this.doCache = false;
            this.maintainCache = false;
        }

        public
        TopologyDispatchVaryingMethod(boolean avoidLoops, boolean cache, boolean maintainCache) {
            super(avoidLoops);
            doCache = cache;
            this.maintainCache = maintainCache;
        }

        public
        Method computeMethod(T root) {
            throw new NotImplementedException();
        }

        public
        R dispatch(Method m, T on, Object... args) {
            if ("added".equals(m.getName())) {
                trace = true;
            }
            fixedMethod = m;
            this.args = args;
            apply(topology, on);

            if ("added".equals(m.getName())) {
                trace = false;
            }

            return ret;
        }

        public
        R dispatch(T on, Object... args) {
            fixedMethod = null;
            this.args = args;
            apply(topology, on);
            return ret;
        }

        public
        Method getMethod(T root) {
            Method m = null;
            if (fixedMethod != null) return fixedMethod;
            else if (!doCache) return computeMethod(root);
            else {
                m = cache.get(root.getClass());
                if (m == null) {
                    cache.put(root.getClass(), m = computeMethod(root));
                }
                return m;
            }
        }

    }

    public
    class UntilNull<R> extends TopologyDispatchVaryingMethod<T> {

        private Method method;

        private Object[] args;

        protected List<R> ret = new ArrayList<R>();

        public
        UntilNull(boolean avoidLoops) {
            super(avoidLoops);
        }

        public
        List<R> accumulate(Method method, T on, Object... args) {
            ret.clear();
            this.fixedMethod = method;
            this.args = args;
            this.apply(topology, on);
            return ret;
        }

        public
        List<R> accumulate(T on, Object... args) {
            ret.clear();
            this.args = args;
            this.apply(topology, on);
            return ret;
        }

        @Override
        protected
        TraversalHint visit(T root) {
            try {
                Object r = getMethod(root).invoke(root, args);
                if (r != null) {
                    ret.add((R) r);
                    return StandardTraversalHint.STOP;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            return StandardTraversalHint.CONTINUE;
        }
    }

    private final ITopology<T> topology;

    public
    DispatchOverTopology(ITopology<T> topology) {
        this.topology = topology;
    }

}
