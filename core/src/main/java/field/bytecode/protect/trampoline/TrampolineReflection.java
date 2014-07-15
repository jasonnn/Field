package field.bytecode.protect.trampoline;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by jason on 7/14/14.
 */
public class TrampolineReflection {
    static HashMap<Class, Field[]> fieldsCache = new HashMap<Class, Field[]>();
    static HashMap<Class, Method[]> methodsCache = new HashMap<Class, Method[]>();



    static public Field[] getAllFields(Class of) {
        Field[] ret = fieldsCache.get(of);
        if (ret == null) {
            List<Field> fieldsList = new ArrayList<Field>();
            _getAllFields(of, fieldsList);
            fieldsCache.put(of, ret = fieldsList.toArray(new Field[fieldsList.size()]));
        }
        return ret;
    }

    static public Method[] getAllMethods(Class of) {
        Method[] ret = methodsCache.get(of);
        if (ret == null) {
            ArrayList<Method> methodsList = new ArrayList<Method>();
            _getAllMethods(of, methodsList);
            methodsCache.put(of, ret = methodsList.toArray(new Method[methodsList.size()]));
        }
        return ret;
    }

    static public Field getFirstFieldCalled(Class of, String name) {
        Field[] allFields = getAllFields(of);
        for (Field f : allFields) {
            if (f.getName().equals(name)) {
                f.setAccessible(true);
                return f;
            }
        }
        return null;
    }

    static protected void _getAllFields(Class of, List<Field> into) {
        if (of == null)
            return;
        Field[] m = of.getDeclaredFields();
        List<Field> list = Arrays.asList(m);
        Collections.sort(list, FieldComparator.INSTANCE);
        into.addAll(list);
        _getAllFields(of.getSuperclass(), into);
        Class[] interfaces = of.getInterfaces();
        for (Class anInterface : interfaces) _getAllFields(anInterface, into);
    }

    static protected void _getAllMethods(Class of, List<Method> into) {
        if (of == null)
            return;
        Method[] m = of.getDeclaredMethods();
        List<Method> list = Arrays.asList(m);
        into.addAll(list);
        _getAllMethods(of.getSuperclass(), into);
        Class[] interfaces = of.getInterfaces();
        for (Class anInterface : interfaces) _getAllMethods(anInterface, into);
    }
}