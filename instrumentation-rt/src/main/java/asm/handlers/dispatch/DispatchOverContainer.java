package asm.handlers.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
* Created by jason on 7/26/14.
*/
public
class DispatchOverContainer {
    public static
    void dispatch(String tag, Class clazz, Method method, iContainer on, Object... args) {
        if (clazz == null) clazz = on.getClass();

        List list = on.propagateTo(tag, clazz, method, args);
        if (list == null) return;

        for (Object o : list) {
            if (clazz.isInstance(o)) {
                try {
                    method.invoke(o, args);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Object o : list) {
            if (o instanceof iContainer) {
                dispatch(tag, clazz, method, (iContainer) o, args);
            }
        }
    }

    public static
    void dispatchBackwards(String tag, Class clazz, Method method, iContainer on, Object... args) {
        if (clazz == null) clazz = on.getClass();
        List list = on.propagateTo(tag, clazz, method, args);
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(list.size() - 1 - i);
            if (clazz.isInstance(o)) {
                try {
                    method.invoke(o, args);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(list.size() - 1 - i);
            if (o instanceof iContainer) {
                dispatch(tag, clazz, method, (iContainer) o, args);
            }
        }
    }
}
