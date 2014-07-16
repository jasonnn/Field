package reflections;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.reflections.ReflectionUtils;
import org.reflections.ReflectionsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Created by jason on 7/15/14.
 */
public class Reflect {
    private static final String pathSeparator = "_";
    private static final String doubleSeparator = "__";
    private static final String dotSeparator = ".";
    private static final String arrayDescriptor = "$$";
    private static final String tokenSeparator = "_";

    //
    public static Class<?> resolveClassOf(final Class element) throws ClassNotFoundException {
        Class<?> cursor = element;
        LinkedList<String> ognl = Lists.newLinkedList();

        while (cursor != null) {
            ognl.addFirst(cursor.getSimpleName());
            cursor = cursor.getDeclaringClass();
        }

        String classOgnl = Joiner.on(".").join(ognl.subList(1, ognl.size())).replace(".$", "$");
        return Class.forName(classOgnl);
    }

    public static Class<?> resolveClass(final Class aClass) {
        try {
            return resolveClassOf(aClass);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to class " + aClass.getName(), e);
        }
    }

    public static Field resolveField(final Class aField) {
        try {
            String name = aField.getSimpleName();
            Class<?> declaringClass = aField.getDeclaringClass().getDeclaringClass();
            return resolveClassOf(declaringClass).getDeclaredField(name);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to field " + aField.getName(), e);
        }
    }

    public static Annotation resolveAnnotation(Class annotation) {
        try {
            String name = annotation.getSimpleName().replace(pathSeparator, dotSeparator);
            Class<?> declaringClass = annotation.getDeclaringClass().getDeclaringClass();
            Class<?> aClass = resolveClassOf(declaringClass);
            Class<? extends Annotation> aClass1 = (Class<? extends Annotation>) ReflectionUtils.forName(name);
            Annotation annotation1 = aClass.getAnnotation(aClass1);
            return annotation1;
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to annotation " + annotation.getName(), e);
        }
    }

    public static Method resolveMethod(final Class aMethod) {
        String methodOgnl = aMethod.getSimpleName();

        try {
            String methodName;
            Class<?>[] paramTypes;
            if (methodOgnl.contains(tokenSeparator)) {
                methodName = methodOgnl.substring(0, methodOgnl.indexOf(tokenSeparator));
                String[] params = methodOgnl.substring(methodOgnl.indexOf(tokenSeparator) + 1).split(doubleSeparator);
                paramTypes = new Class<?>[params.length];
                for (int i = 0; i < params.length; i++) {
                    String typeName = params[i].replace(arrayDescriptor, "[]").replace(pathSeparator, dotSeparator);
                    paramTypes[i] = ReflectionUtils.forName(typeName);
                }
            } else {
                methodName = methodOgnl;
                paramTypes = null;
            }

            Class<?> declaringClass = aMethod.getDeclaringClass().getDeclaringClass();
            return resolveClassOf(declaringClass).getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e) {
            throw new ReflectionsException("could not resolve to method " + aMethod.getName(), e);
        }
    }
}
