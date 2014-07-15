package field.bytecode.protect.trampoline;

import java.lang.ClassLoader;

/**
 * Created by jason on 7/14/14.
 */
public interface TrampolineInstrumentation {
    byte[] instrumentClass(ClassLoader deferTo, String className);
}
