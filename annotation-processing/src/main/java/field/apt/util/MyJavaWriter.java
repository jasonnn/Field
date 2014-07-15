package field.apt.util;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by jason on 4/19/14.
 */
public abstract class MyJavaWriter<W extends MyJavaWriter<W>> extends ExtendableJavaWriter<W> {

    public static class MyImpl extends MyJavaWriter<MyImpl> {

        public MyImpl(Writer out) {
            super(out);

        }

        @Override
        protected MyImpl self() {
            return this;
        }
    }


    public MyJavaWriter(Writer out) {
        super(out);
    }


    static <T extends Enum<T>> Set<T> varargs2set(T first, T... rest) {
        EnumSet<T> set = EnumSet.of(first);
        set.addAll(Arrays.asList(rest));
        return set;
    }


    public W beginType(String type, String kind, Modifier first, Modifier... modifiers) throws IOException {
        return super.beginType(type, kind, varargs2set(first, modifiers));
    }


    public W beginType(String type, String kind) throws IOException {
        return super.beginType(type, kind, Collections.<Modifier>emptySet());
    }


}
