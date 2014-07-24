package field.bytecode.protect.instrumentation2;

import field.bytecode.BytecodeTestCase;
import field.bytecode.protect.Woven;
import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public
class AbstractMethodInstrumentationTest extends BytecodeTestCase {

    @Test
    public
    void testMakeDelegate() throws Exception {
        byte[] data = readClass(TstCls.class);

        ClassReader reader = new ClassReader(data);
        ClassWriter writer = new ClassWriter(0);

        StringWriter sw = new StringWriter();


        MyClassVisitor cv =
                new MyClassVisitor(new CheckClassAdapter(new TraceClassVisitor(writer, new PrintWriter(sw, true))));

        reader.accept(cv, ClassReader.EXPAND_FRAMES);


        Class<?> tstClsRuined = loadClass(TstCls.class.getName(), writer.toByteArray());

        Method[] methods = tstClsRuined.getDeclaredMethods();
        assertEquals(4, methods.length);

        Method myMethod = tstClsRuined.getDeclaredMethod("myMethod");
        assertTrue(myMethod.isAnnotationPresent(Woven.class));


        Object inst = tstClsRuined.newInstance();
        myMethod.invoke(inst);


    }


    static
    class MyClassVisitor extends MainVisitorThing {

        public
        MyClassVisitor(ClassVisitor cv) {
            super(cv, Collections.<String, AnnotatedMethodHandler2>emptyMap());
        }

        @Override
        public
        boolean canHandle(String desc) {
            return true;
        }

        @Override
        public
        AnnotatedMethodHandler2 getHandler(String desc) {
            return MakeMethodDelegateVisitor.HANDLER;
        }
    }

    static
    class MakeMethodDelegateVisitor extends AbstractMethodInstrumentation implements VisitEndCallback {
        static final AnnotatedMethodHandler2 HANDLER = new AnnotatedMethodHandler2() {
            @NotNull
            @Override
            public
            MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
                MethodNode methodNode =
                        new MethodNode(Opcodes.ASM5, ctx.access, ctx.name, ctx.desc, ctx.signature, ctx.exceptions);
                MakeMethodDelegateVisitor mv = new MakeMethodDelegateVisitor(ctx, methodNode);
                ctx.classCtx.callAfterClass(mv);
                return methodNode;
            }
        };


        boolean isMyMethod;
        MethodNode origMethod;

        protected
        MakeMethodDelegateVisitor(ASMAnnotatedMethodCtx ctx, MethodNode origMethod) {
            super(ctx);
            this.origMethod = origMethod;
            if ("myMethod".equals(ctx.name)) isMyMethod = true;

            if (isMyMethod) {
                callOrig();
            }
        }

        void callOrig() {
            assert mv != null;
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, ctx.classCtx.name, ctx.name + "_orig", ctx.desc, false);
            mv.visitInsn(RETURN);
            //  mv.visitLocalVariable();
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


        @Override
        public
        void apply(ClassVisitor cv) {
            assert origMethod.name.equals("myMethod");

            origMethod.name = "myMethod_orig";


            ctx.name = origMethod.name;
            ctx.delegate = cv.visitMethod(ctx.access, ctx.name, ctx.desc, ctx.desc, ctx.exceptions);

            origMethod.accept(new SimpleIntercept(ctx));

        }
    }

    static
    class SimpleIntercept extends AbstractMethodInstrumentation {

        public
        SimpleIntercept(ASMAnnotatedMethodCtx ctx) {
            super(ctx);
        }

        private
        void call(boolean before) {
            invokeStatic(ASMType.getType(TstCls.class), new ASMMethod(before ? "before" : "after", "()V"));
        }

        @Override
        protected
        void onMethodEnter() {
            call(true);
        }

        @Override
        protected
        void onMethodExit(int opcode) {
            call(false);
        }
    }

}