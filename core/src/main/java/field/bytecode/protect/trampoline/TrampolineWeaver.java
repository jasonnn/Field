package field.bytecode.protect.trampoline;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by jason on 7/14/14.
 */
public class TrampolineWeaver implements ClassFileTransformer {
    public static final TrampolineWeaver INSTANCE = new TrampolineWeaver();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return classfileBuffer;
    }
}
