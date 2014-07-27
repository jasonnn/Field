package field.protect.asm.visitors.paramObj;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public
class MethodParams {
    public
    MethodVisitor callVisitMethod(@Nullable ClassVisitor cv){
        if(cv==null) return null;
        return cv.visitMethod(access,name,desc,signature,exceptions);
    }

    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;

    public
    MethodParams(int access, String name, String desc, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public
    int getAccess() {
        return access;
    }

    public
    String getName() {
        return name;
    }

    public
    String getDesc() {
        return desc;
    }

    public
    String getSignature() {
        return signature;
    }

    public
    String[] getExceptions() {
        return exceptions;
    }
}
