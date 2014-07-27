package field.protect.asm.visitors.paramObj;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;

public
class ClassParams {
    public void callVisit(@Nullable ClassVisitor cv){
        if(cv==null)return;
        cv.visit(version,access,name,signature,superName,interfaces);
    }

    private final int version;
    private final int access;
    private final String name;
    private final String signature;
    private final String superName;
    private final String[] interfaces;

    public
    ClassParams(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    public
    int getVersion() {
        return version;
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
    String getSignature() {
        return signature;
    }

    public
    String getSuperName() {
        return superName;
    }

    public
    String[] getInterfaces() {
        return interfaces;
    }
}
