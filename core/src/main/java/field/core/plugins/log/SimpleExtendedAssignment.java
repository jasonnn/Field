package field.core.plugins.log;

import field.core.execution.PythonInterface;
import field.core.plugins.python.PythonPlugin.CapturedEnvironment;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.ReflectionTools;
import field.util.PythonUtils;
import org.python.core.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

public
class SimpleExtendedAssignment extends InvocationLogging {

    public static
    interface iHandleAssignment {
        public
        boolean handle(Class fieldType, Class assignmentType, Field f, Object in, Object value)
                throws IllegalArgumentException, IllegalAccessException;
    }

    public static final ArrayList<iHandleAssignment> handlers = new ArrayList<iHandleAssignment>();

    static {
        handlers.add(new iHandleAssignment() {

            public
            boolean handle(Class fieldType, Class assignmentType, Field f, Object in, Object value)
                    throws IllegalArgumentException, IllegalAccessException {

                if (assignmentType.isAssignableFrom(fieldType)) {
                    f.set(in, value);
                    return true;
                }
                else {
                    // also
                    // allow
                    // down
                    // assignments

                    if (value instanceof Number) {

                        return setNumber(f, fieldType, in, (Number) value);
                    }
                }
                return false;
            }
        });

        handlers.add(new iHandleAssignment() {

            public
            boolean handle(final Class fieldType, Class assignmentType, final Field f, final Object in, Object value)
                    throws IllegalArgumentException, IllegalAccessException {

                if (value instanceof PyDictionary) {
                    PyDictionary d = ((PyDictionary) value);
                    PyObject vv = d.get(new PyString("value"));
                    final Number v = vv == Py.None ? 0f : (((Number) vv.__tojava__(Number.class)));

                    PyObject over = d.get(new PyString("over"));
                    final float o;

                    final Number n = (Number) f.get(in);

                    if (over == Py.None) {
                        PyObject speed = d.get(new PyString("speed"));
                        if (speed != Py.None) {
                            o = (float) (Math.abs(n.doubleValue() - v.doubleValue())
                                         / ((Number) speed.__tojava__(Number.class)).doubleValue());
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        o = ((Number) over.__tojava__(Number.class)).floatValue();
                    }

                    final PyObject curve = d.get(new PyString("curve"));

                    final CapturedEnvironment env =
                            (CapturedEnvironment) PythonInterface.getPythonInterface().getVariable("_environment");

                    Launcher.getLauncher().registerUpdateable(new IUpdateable() {

                        float t = 0;

                        public
                        void update() {
                            env.enter();
                            try {

                                float a = t / o;

                                if (curve != null && curve != Py.None) a = lookupCurve(a, curve);

                                boolean finished = false;

                                if (t > o) {
                                    t = o;
                                    finished = true;
                                }
                                double vNow = n.doubleValue() + (v.doubleValue() - n.doubleValue()) * t / o;

                                try {
                                    setNumber(f, fieldType, in, vNow);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                                if (t == o) finished = true;
                                t++;

                                if (finished) Launcher.getLauncher().deregisterUpdateable(this);

                            } finally {

                                env.exit();
                            }
                        }

                    });
                    return true;
                }
                return false;

            }
        });
        handlers.add(new iHandleAssignment() {

            public
            boolean handle(final Class fieldType, Class assignmentType, final Field f, final Object in, Object value)
                    throws IllegalArgumentException, IllegalAccessException {

                //System.out.println(" value is of type <"+value+">");

                if (value instanceof PyGenerator) {

                    PyGenerator g = ((PyGenerator) value);

                    new PythonUtils().stackPrePost(g, new IAcceptor<Object>() {
                        public
                        IAcceptor<Object> set(Object to) {

                            if (to == null || to == Py.None) return this;

                            if (to == Py.NoConversion) return this;

                            if (to instanceof PyObject) {
                                Object nc = ((PyObject) to).__tojava__(Number.class);
                                if (nc == Py.NoConversion) return this;
                                to = nc;
                            }
                            if (!(to instanceof Number)) return this;

                            Number num = (Number) to;
                            try {
                                setNumber(f, fieldType, in, num);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            return this;
                        }
                    }, (CapturedEnvironment) PythonInterface.getPythonInterface().getVariable("_environment"));


                    return true;

                }
                return false;

            }
        });
        handlers.add(new iHandleAssignment() {

            public
            boolean handle(final Class fieldType, Class assignmentType, final Field f, final Object in, Object value)
                    throws IllegalArgumentException, IllegalAccessException {

                if (value instanceof PySequence) {

                    PySequence g = ((PySequence) value);

                    new PythonUtils().stackPrePost(null, g, new IAcceptor<PyObject>() {
                        public
                        IAcceptor<PyObject> set(PyObject to) {

                            if (to == null || to == Py.None) return this;

                            Number num = (Number) to.__tojava__(Number.class);
                            try {
                                setNumber(f, fieldType, in, num);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            return this;
                        }
                    }, null);

                    return true;

                }
                return false;

            }
        });

    }

    public static
    float lookupCurve(float a, PyObject curve) {
        PyObject q = curve.__call__(new PyFloat(a));
        return ((Number) q.__tojava__(Number.class)).floatValue();
    }

    protected static
    boolean setNumber(Field f, Class fieldType, Object in, Number value)
            throws IllegalArgumentException, IllegalAccessException {
        if (fieldType == Double.class || fieldType == Double.TYPE) {
            f.set(in, (value).doubleValue());
            return true;
        }
        if (fieldType == Float.class || fieldType == Float.TYPE) {
            f.set(in, (value).floatValue());
            return true;
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            f.set(in, (value).intValue());
            return true;
        }
        if (fieldType == Short.class || fieldType == Short.TYPE) {
            f.set(in, (value).shortValue());
            return true;
        }
        if (fieldType == Byte.class || fieldType == Byte.TYPE) {
            f.set(in, (value).byteValue());
            return true;
        }
        if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
            f.set(in, (value).doubleValue() > 0);
            return true;
        }
        return false;
    }

    private final iTypeErrorRecovery recovery = new iTypeErrorRecovery() {
        public
        void recover(Object javaObject, String field, Object setToBe) {

            //System.out.println(" recovery <"+javaObject+"> <"+field+"> <"+setToBe+"> ");

            Field f = ReflectionTools.getFirstFIeldCalled(javaObject.getClass(), field);
            Class<?> fieldType = f.getType();

            Class<?> setToBeClass = setToBe.getClass();

            Exception cause = null;
            try {
                for (iHandleAssignment a : handlers) {
                    if (a.handle(fieldType, setToBeClass, f, javaObject, setToBe)) return;
                }
            } catch (IllegalArgumentException e) {
                cause = e;
            } catch (IllegalAccessException e) {
                cause = e;
            }

            throw (PyException) (new PyException(Py.None,
                                                 "cannot assign type <"
                                                 + setToBe.getClass()
                                                 + "> to <"
                                                 + fieldType
                                                 + '>').initCause(cause));
        }
    };

    public
    SimpleExtendedAssignment() {
        defaultTypeRecoveryForces = true;
    }

    @Override
    protected
    iTypeErrorRecovery getDefaultTypeRecovery() {
        return recovery;
    }

}
