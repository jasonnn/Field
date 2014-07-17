package field.bytecode.protect.trampoline;

import field.bytecode.protect.Woven;
import field.util.Ref;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jason on 7/16/14.
 */

@Deprecated
public class PreWeaveScanner extends ClassVisitor {
    @Deprecated
    public static ScanResult scan(byte[] bytes) {
        return scan(bytes, null);
    }

    @Deprecated
    public static ScanResult scan(byte[] bytes, ClassVisitor delegate) {
        PreWeaveScanner scanner = new PreWeaveScanner(delegate);
        new ClassReader(bytes).accept(scanner, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return scanner.getResult();
    }

    public static class ScanResult {
        public final boolean isWoven;
        public final boolean isInner;
        public final String extendsClass;
        public final List<String> implementsClasses;

        public String[] getImplementsArray() {
            return implementsClasses.toArray(new String[implementsClasses.size()]);
        }


        public ScanResult(boolean isWoven, boolean isInner, @NotNull String extendsClass, @NotNull List<String> implementsClasses) {
            this.isWoven = isWoven;
            this.isInner = isInner;
            this.extendsClass = extendsClass;
            this.implementsClasses = implementsClasses;
        }
    }

    private static final String WOVEN = Type.getDescriptor(Woven.class);
    private String extendsClass;
    private String[] implementsClasses;
    private final Ref<Boolean> isWoven = Ref.create();
    private boolean isInner;

    private boolean scanFinished;

    public PreWeaveScanner() {
        this(null);
    }

    public PreWeaveScanner(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        extendsClass = superName;
        implementsClasses = interfaces;
        isInner = name.indexOf('$') != -1;
        scanFinished = false;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        scanFinished = true;
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (WOVEN.equals(desc)) isWoven.set(true);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!isInner) return mv;
        return new WovenMethodScanner(isWoven, mv);

    }

    public ScanResult getResult() {
        assert scanFinished;
        return new ScanResult(isWoven.get(), isInner, extendsClass,
                (implementsClasses == null) ? Collections.<String>emptyList() : Arrays.asList(implementsClasses));
    }

    static class WovenMethodScanner extends MethodVisitor {
        private final Ref<Boolean> ref;

        public WovenMethodScanner(Ref<Boolean> ref) {
            this(ref, null);
        }

        public WovenMethodScanner(Ref<Boolean> ref, MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
            this.ref = ref;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (WOVEN.equals(desc)) ref.set(true);
            return super.visitAnnotation(desc, visible);
        }
    }

}
