package field.bytecode.protect.instrumentation2;

import org.objectweb.asm.ClassVisitor;

import java.util.Arrays;

/**
 * Created by jason on 7/21/14.
 */
public class ASMClassCtx {
    public int access;
    public String name;
    public String signature;
    public String superName;
    public String[] interfaces;
    public ClassVisitor cv;


    void copyTo(ASMClassCtx other) {
        other.access = access;
        other.name = name;
        other.signature = signature;
        other.superName = superName;
        other.interfaces = interfaces == null ? null : Arrays.copyOf(interfaces, interfaces.length);
        other.cv = cv;
    }

    void copyFrom(ASMClassCtx other) {
        this.access = other.access;
        this.name = other.name;
        this.signature = other.signature;
        this.superName = other.superName;
        this.interfaces = other.interfaces == null ? null : Arrays.copyOf(other.interfaces, other.interfaces.length);
        this.cv = other.cv;
    }
}
