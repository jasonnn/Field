//package field.bytecode.protect.instrumentation;
//
//import annotations.MyWoven;
//import field.bytecode.protect.Woven;
//import org.junit.Test;
//
//import java.lang.reflect.Method;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * Created by jason on 7/21/14.
// */
//public
//class WovenTest extends AnnotatedMethodHandlersTest {
//
//    static final Class<MyWoven> toTest = MyWoven.class;
//
//
//    static
//    void checkClass(Class<?> c) throws NoSuchMethodException {
//        assertTrue(c.isAnnotationPresent(Woven.class));
//        Method wovenMethod = c.getDeclaredMethod("wovenMethod", Integer.TYPE);
//        assertNotNull(wovenMethod);
//        assertTrue(wovenMethod.isAnnotationPresent(Woven.class));
//
//    }
//
//    @Test
//    public
//    void testSanity() throws Exception {
//        checkClass(toTest);
//    }
//
//
////    @Test
////    public
////    void testInstrumentation() throws Exception {
////
////        byte[] data = readClass(toTest);
////
////        StringWriter pre = new StringWriter();
////        StringWriter post = new StringWriter();
////
////        MainVisitorThing mainVisitorThing =
////                new MainVisitorThing(new CheckClassAdapter(new TraceClassVisitor(new PrintWriter(post))));
////
////        new ClassReader(data).accept(new TraceClassVisitor(mainVisitorThing, new PrintWriter(pre)), 0);
////
////        System.out.println(pre.toString());
////        assertEquals(pre.toString(), post.toString());
////    }
////
////    @Test
////    public
////    void testCreateInstance() throws Exception {
////        //need ClassWriter.COMPUTE_FRAMES for java7
////        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
////
////        new ClassReader(readClass(toTest)).accept(new MainVisitorThing(cw), 0);
////
////
////        Class instrumentedCls = loadClass(toTest.getName(), cw.toByteArray());
////        checkClass(instrumentedCls);
////
////        Object instrumentedInstance = instrumentedCls.newInstance();
////        assertNotNull(instrumentedInstance);
////
////
////    }
//
//
//}