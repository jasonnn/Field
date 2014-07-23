package field.bytecode.protect.instrumentation;

import annotations.MyDispatchOverTopology;
import field.bytecode.BytecodeTestCase;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.instrumentation2.AnnotatedMethodHandler2;
import field.bytecode.protect.instrumentation2.DispatchOverTopologyTransformer;
import field.protect.asm.ASMType;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jason on 7/22/14.
 */
public
class DispatchOverTopologyTest extends BytecodeTestCase {
    static final Class<MyDispatchOverTopology> testOn = MyDispatchOverTopology.class;


    @Test
    public
    void testTransformer() throws Exception {
        byte[] data = readClass(testOn);

        StringWriter pre = new StringWriter();
        StringWriter post = new StringWriter();

        Map<String, AnnotatedMethodHandler2> handler =
                Collections.singletonMap(ASMType.getDescriptor(DispatchOverTopology.class),
                                         DispatchOverTopologyTransformer.HANDLER);


        TestingMainVisitorThing mainVisitorThing =
                new TestingMainVisitorThing(new CheckClassAdapter(new TraceClassVisitor(new PrintWriter(post))),
                                            handler);

        try {
            new ClassReader(data).accept(new TraceClassVisitor(mainVisitorThing, new PrintWriter(pre)),
                                         ClassReader.EXPAND_FRAMES);


        } catch (Exception e) {
            System.out.println("pre:\n" + pre.toString());
            System.out.println("\npost:\n" + post.toString());
            throw e;
        }


        System.out.println(pre.toString());

    }

    static
    void checkClass(Class<?> c) throws NoSuchMethodException {
        Method m = c.getDeclaredMethod("update");
        DispatchOverTopology ann = m.getAnnotation(DispatchOverTopology.class);
        assertNotNull(ann);
        assertEquals(Cont.class, ann.topology());
    }

    @Test
    public
    void testSanity() throws Exception {
        checkClass(testOn);
    }
}
