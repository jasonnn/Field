package field.internal;

import java.lang.reflect.Method;

/**
 * Created by jason on 7/26/14.
 */
public
class UncheckedReflection {

    public static
    Method getDeclaredMethod(Class<?> c, String name, Class... params) {
        try {
            Method m = c.getDeclaredMethod(name, params);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
