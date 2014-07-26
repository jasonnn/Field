package field.bytecode.protect.util;

import java.util.*;

/**
 * Created by jason on 7/16/14.
 * Intended to be a replacement for TrampolineReflection
 */
public class DeepReflection {
    private static final Class[] EMPTY_ARRAY = new Class[0];

    private static final ThreadLocal<MyDeque> dq = new ThreadLocal<MyDeque>() {
        @Override
        protected MyDeque initialValue() {
            return new MyDeque();
        }
    };

    static class MyDeque extends ArrayDeque<Class> {

        public void pushAll(Class[] classes) {
            if ((classes == null) || (classes.length == 0)) return;
            for (int i = classes.length - 1; i > -1; i--) {
                push(classes[i]);
            }
        }

        public void addAllLast(Class[] classes) {
            for (Class c : classes) {
                addLast(c);
            }

        }
    }


    private static Class[] notNull(Class[] arr) {
        return (arr == null) ? EMPTY_ARRAY : arr;
    }

    public static Set<Class> getInterfacesDF(Class base) {
        return getInterfaces(base, new LinkedHashSet<Class>(), true, true);
    }

    public static Set<Class> getInterfacesBF(Class base) {
        return getInterfaces(base, new LinkedHashSet<Class>(), true, false);
    }

    public static Set<Class> getHeirarchyDF(Class base) {
        return getHeirarchy(base, new LinkedHashSet<Class>(), true, true);
    }

    public static Set<Class> getHeirarchyBF(Class base) {
        return getHeirarchy(base, new LinkedHashSet<Class>(), true, false);
    }


    public static Set<Class> getHeirarchy(Class base, Set<Class> accum, boolean includeSelf, boolean depthFirst) {
        if (base.isInterface()) return getInterfaces(base, accum, includeSelf, depthFirst);

        MyDeque dq = new MyDeque();

        addDirectParents(base, dq, depthFirst);
        if (includeSelf) dq.push(base);

        while (!dq.isEmpty()) {
            Class next = dq.pop();
            accum.add(next);
            addDirectParents(next, dq, depthFirst);
        }


        return accum;
    }

    static void addDirectParents(Class c, MyDeque deq, boolean depthFirst) {
        Class sup = c.getSuperclass();
        Class[] inter = c.getInterfaces();
        if ((sup != null) && (sup != Object.class)) {
            if (depthFirst) {
                deq.pushAll(inter);
                deq.push(sup);
            } else {
                deq.addLast(sup);
                deq.addAllLast(inter);
            }
        } else {
            if (depthFirst) deq.pushAll(inter);
            else deq.addAllLast(inter);
        }

    }

    static Collection<Class> getDirectParents(Class c) {
        Class sup = c.getSuperclass();
        Class[] inter = c.getInterfaces();
        if ((sup != null) && (sup != Object.class)) {
            if (inter.length > 0) {
                LinkedHashSet<Class> accum = new LinkedHashSet<Class>(inter.length + 1);
                accum.add(sup);
                Collections.addAll(accum, inter);
                return accum;
            } else {
                return Collections.singleton(sup);
            }
        }
        return Collections.emptySet();
    }


    public static Set<Class> getInterfaces(Class base, Set<Class> accum, boolean maybeIncludeSelf, boolean depthFirst) {
        MyDeque deque = new MyDeque();
        if (base.isInterface() && maybeIncludeSelf) {
            deque.push(base);
        } else {
            deque.pushAll(base.getInterfaces());
        }

        while (!deque.isEmpty()) {
            Class next = deque.pop();
            accum.add(next);
            if (depthFirst) {
                deque.pushAll(next.getInterfaces());
            } else {
                deque.addAllLast(next.getInterfaces());
            }
        }


        return accum;
    }

    public static Set<Class> getSuperClasses(Class base) {
        return getSuperClasses(base, new LinkedHashSet<Class>(), true);
    }

    public static Set<Class> getSuperClasses(Class base, Set<Class> accum, boolean includeSelf) {
        Class parent = includeSelf ? base : base.getSuperclass();
        for (; (parent != null) && (parent != Object.class); parent = parent.getSuperclass()) {
            accum.add(parent);
        }
        return accum;
    }
}
