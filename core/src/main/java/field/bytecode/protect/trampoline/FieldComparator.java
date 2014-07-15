package field.bytecode.protect.trampoline;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by jason on 7/14/14.
 */
public class FieldComparator implements Comparator<Field> {
    public static final FieldComparator INSTANCE=new FieldComparator();
    @Override
    public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
