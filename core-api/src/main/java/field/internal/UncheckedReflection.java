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
            return c.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
