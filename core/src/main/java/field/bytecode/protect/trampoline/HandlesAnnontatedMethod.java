package field.bytecode.protect.trampoline;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
* Created by jason on 7/14/14.
*/
public interface HandlesAnnontatedMethod {
    public MethodVisitor handleEnd(int access, String methodName, String methodDesc, String signature, ClassVisitor classDelegate, MethodVisitor delegate, HashMap<String, Object> paramters, byte[] originalByteCode, String className);
}
