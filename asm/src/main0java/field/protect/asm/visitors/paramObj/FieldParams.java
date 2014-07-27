package field.protect.asm.visitors.paramObj;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public
class FieldParams {
    public
    FieldVisitor callVisitField(@Nullable ClassVisitor cv){
        if(cv==null) return null;
      return  cv.visitField(access,name,desc,signature,value);
    }

    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final Object value;

    public
    FieldParams(int access, String name, String desc, String signature, Object value) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.value = value;
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
    Object getValue() {
        return value;
    }
}
