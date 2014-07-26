package field.bytecode.protect.dispatch;

import field.launch.iUpdateable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
import java.rmi.server.UID;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public
class Cont implements DispatchProvider {
    private static final Logger log = Logger.getLogger(Cont.class.getName());

    static HashMap<Method, List<Run>> class_links = new HashMap<Method, List<Run>>();

    public static WeakHashMap<Object, HashMap<Method, List<Run>>> instance_links =
            new WeakHashMap<Object, HashMap<Method, List<Run>>>();

    public static
    void linkWith(Object o, Method m, Run r) {

        Method om = m;
        // Method m might refer to a superclass, so we have to do a little work here
        m = ReflectionTools.findMethodWithParametersUpwards(m.getName(), m.getParameterTypes(), o.getClass());
        assert m != null : "couldn't really find <" + om + "> on <" + o + "> <" + o.getClass() + '>';
        HashMap<Method, List<Run>> name = instance_links.get(o);
        if (name != null) {
            List<Run> n = name.get(m);
            if (n != null) {
                n.add(r);
            }
            else {
                name.put(m, n = new ArrayList<Run>());
                n.add(r);
            }
        }
        else {
            List<Run> n = new ArrayList<Run>();
            n.add(r);
            HashMap<Method, List<Run>> me = new HashMap<Method, List<Run>>();
            me.put(m, n);
            instance_links.put(o, me);
        }

    }

    public static
    void linkWith_static(Method m, Class c, Run r) {

        Method om = m;
        // Method m might refer to a superclass, so we have to do a little work here
        m = ReflectionTools.findMethodWithParametersUpwards(m.getName(), m.getParameterTypes(), c);
        assert m != null : "couldn't really find <" + om + ">  on <" + c + '>';


        List<Run> name = class_links.get(m);
        if (name != null) name.add(r);
        else {
            ArrayList<Run> al = new ArrayList<Run>();
            al.add(r);
            class_links.put(m, al);
        }
    }

    public static
    void linkWith_static(Method m, Run r) {
        List<Run> name = class_links.get(m);
        if (name != null) name.add(r);
        else {
            ArrayList<Run> al = new ArrayList<Run>();
            al.add(r);
            class_links.put(m, al);
        }
    }

    public static
    void unlinkWith(Object o, Method m, Run r) {
        Method om = m;
        // Method m might refer to a superclass, so we have to do a little work here
        m = ReflectionTools.findMethodWithParametersUpwards(m.getName(), m.getParameterTypes(), o.getClass());
        assert m != null : "couldn't really find <" + om + ">  on <" + o + "> <" + o.getClass() + '>';

        HashMap<Method, List<Run>> name = instance_links.get(o);
        if (name != null) {
            List<Run> n = name.get(m);
            if (n != null) {
                n.remove(r);
                if (n.isEmpty()) {
                    name.remove(m);
                    if (name.isEmpty()) instance_links.remove(name);
                }
            }
            else {
                log.log(Level.INFO, "unlink failed: o=[{0}] m=[{1}] r=[{2}]",new Object[]{o,m,r});
            }
        }
        else {
            log.log(Level.INFO, "unlink failed: o=[{0}] m=[{1}] r=[{2}]", new Object[]{o, m, r});
        }
    }


    public static
    void unlinkWith_static(Method m, Class c, Run r) {

        Method om = m;
        // Method m might refer to a superclass, so we have to do a little work here
        m = ReflectionTools.findMethodWithParametersUpwards(m.getName(), m.getParameterTypes(), c);
        assert m != null : "couldn't really find <" + om + "> on <" + c + '>';

        List<Run> name = class_links.get(m);
        if (name != null) name.remove(r);
        else {
            log.log(Level.INFO, "unlink failed: o=[{0}] m=[{1}] r=[{2}]", new Object[]{ m, r});
        }
    }


    public static
    void unlinkWith_static(Method m, Run r) {
        List<Run> name = class_links.get(m);
        if (name != null) name.remove(r);
        else {
            log.log(Level.INFO, "unlink failed: m=[{1}] r=[{2}]",new Object[]{m,r});
        }
    }

    public MethodUtilities utilities = new MethodUtilities();

    HashSet<Object> alreadyDone = new HashSet<Object>();

    String uid = new UID().toString();

    public
    Apply getTopologyForEntrance(final Object root, Map<String, Object> parameters, Object[] args, String className) {
        if (!alreadyDone.contains(root)) {

            Method m = (Method) parameters.get(uid);
            if (m == null) {
                m = utilities.getMethodFor(root.getClass().getClassLoader(),
                                           (org.objectweb.asm.commons.Method) parameters.get("method"),
                                           null,
                                           className);
                parameters.put(uid, m);
            }

            final List<Run> r1 = class_links.get(m);
            HashMap<Method, List<Run>> mm = instance_links.get(root);
            List<Run> rr2 = null;
            if (mm != null) rr2 = mm.get(m);
            final List<Run> r2 = rr2;

            if ((r1 != null) || (r2 != null)) {
                alreadyDone.add(root);
                return new Apply() {

                    public
                    void head(Object[] args) {
                        if (r1 != null) for (Run run : new ArrayList<Run>(r1)) {
                            ReturnCode c = run.head(root, args);
                            if (c == ReturnCode.STOP) return;
                        }
                        if (r2 != null) for (Run run : new ArrayList<Run>(r2)) {
                            ReturnCode c = run.head(root, args);
                            if (c == ReturnCode.STOP) return;
                        }
                    }

                    public
                    Object tail(Object[] args, Object returnWas) {
                        return returnWas;
                    }
                };
            }
        }
        return null;
    }

    public
    Apply getTopologyForExit(final Object root, Map<String, Object> parameters, Object[] args, String className) {
        if (alreadyDone.contains(root)) {

            // experimental
            Method m = (Method) parameters.get(uid);
            if (m == null) {
                m = utilities.getMethodFor(root.getClass().getClassLoader(),
                                           (org.objectweb.asm.commons.Method) parameters.get("method"),
                                           null,
                                           className);
                parameters.put(uid, m);
            }

            final List<Run> r1 = class_links.get(m);

            HashMap<Method, List<Run>> mm = instance_links.get(root);
            List<Run> rr2 = null;
            if (mm != null) rr2 = mm.get(m);
            final List<Run> r2 = rr2;

            if ((r1 != null) || (r2 != null)) return new Apply() {

                public
                void head(Object[] args) {
                }

                public
                Object tail(Object[] args, Object returnWas) {
                    if (r1 != null) for (Run run : new ArrayList<Run>(r1)) {
                        ReturnCode c = run.tail(root, args, returnWas);
                        if (c == ReturnCode.STOP) return returnWas;
                    }
                    if (r2 != null) for (Run run : new ArrayList<Run>(r2)) {
                        ReturnCode c = run.tail(root, args, returnWas);
                        if (c == ReturnCode.STOP) return returnWas;
                    }
                    return returnWas;
                }
            };
        }
        return null;
    }

    public
    void notifyExecuteBegin(Object fromThis, Map<String, Object> parameterName) {
    }

    public
    void notifyExecuteEnds(Object fromThis, Map<String, Object> parameterName) {
        alreadyDone.remove(fromThis);
    }

    public static
    void wrap(Object inside, Method on, final iUpdateable enter, final iUpdateable exit) {
        Cont.linkWith(inside, on, new Run() {

            public
            ReturnCode head(Object calledOn, Object[] args) {
                enter.update();
                return ReturnCode.CONTINUE;
            }

            public
            ReturnCode tail(Object calledOn, Object[] args, Object returnWas) {
                exit.update();
                return ReturnCode.CONTINUE;
            }
        });


    }

}
