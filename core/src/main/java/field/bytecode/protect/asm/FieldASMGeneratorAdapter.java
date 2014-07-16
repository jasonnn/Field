package field.bytecode.protect.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * Created by jason on 7/15/14.
 */
public class FieldASMGeneratorAdapter extends GeneratorAdapter{

    public FieldASMGeneratorAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(mv, access, name, desc);
    }

    protected FieldASMGeneratorAdapter(final int api, final MethodVisitor mv,
                               final int access, final String name, final String desc){
        super(Opcodes.ASM5,mv,access,name,desc);
    }

    public FieldASMGeneratorAdapter(int access, ASMMethod method, MethodVisitor mv) {
        super(access, method, mv);
    }

    public FieldASMGeneratorAdapter(int access, ASMMethod method, String signature, Type[] exceptions, ClassVisitor cv) {
        super(access, method, signature, exceptions, cv);
    }
}
