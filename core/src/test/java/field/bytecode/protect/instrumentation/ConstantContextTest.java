package field.bytecode.protect.instrumentation;

import annotations.MyConstantContext;
import field.bytecode.BytecodeTestCase;
import field.bytecode.protect.annotations.ConstantContext;
import field.bytecode.protect.instrumentation2.StandardMethodAnnotationHandlers;
import field.graphics.core.Base;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jason on 7/21/14.
 */
public
class ConstantContextTest extends BytecodeTestCase {
    static final Class<MyConstantContext> testOn = MyConstantContext.class;

    static
    void checkClass(Class<?> c) throws NoSuchMethodException {
        Method m = c.getDeclaredMethod("performPass");
        ConstantContext ann = m.getAnnotation(ConstantContext.class);
        assertNotNull(ann);
        assertEquals(false, ann.immediate());
        assertEquals(Base.class, ann.topology());
    }

    @Test
    public
    void testSanity() throws Exception {
        checkClass(testOn);
    }

    @Test
    public
    void testInstrumentation() throws Exception {
        byte[] data = readClass(testOn);

        StringWriter pre = new StringWriter();
        StringWriter post = new StringWriter();


        TestingMainVisitorThing mainVisitorThing =
                new TestingMainVisitorThing(new CheckClassAdapter(new TraceClassVisitor(new PrintWriter(post))),
                                            StandardMethodAnnotationHandlers.CONSTANT_CONTEXT);

        try {
            new ClassReader(data).accept(new TraceClassVisitor(mainVisitorThing, new PrintWriter(pre)),
                                         ClassReader.EXPAND_FRAMES);


        } catch (Exception e) {
            System.out.println("pre:\n" + pre.toString());
            System.out.println("\npost:\n" + post.toString());
            throw e;
        }


        System.out.println(pre.toString());
        // assertEquals(pre.toString(), post.toString());
    }


}