//package field.bytecode.protect.instrumentation;
//
//import field.bytecode.protect.instrumentation2.AnnotatedMethodHandler2;
//import field.bytecode.protect.instrumentation2.MainVisitorThing;
//import field.bytecode.protect.instrumentation2.StandardMethodAnnotationHandlers;
//import org.objectweb.asm.ClassVisitor;
//
//import java.util.Collections;
//import java.util.Map;
//
///**
// * Created by jason on 7/22/14.
// */
//public
//class TestingMainVisitorThing extends MainVisitorThing {
//    public static
//    Map<String, AnnotatedMethodHandler2> handler(StandardMethodAnnotationHandlers handler) {
//        return Collections.<String, AnnotatedMethodHandler2>singletonMap(handler.desc, handler);
//    }
//
//    public
//    TestingMainVisitorThing(ClassVisitor cv, StandardMethodAnnotationHandlers h) {
//        this(cv, handler(h));
//    }
//
//    public
//    TestingMainVisitorThing(ClassVisitor cv, Map<String, AnnotatedMethodHandler2> handlers) {
//        super(cv, handlers);
//    }
//
//    public boolean wasCalled = false;
//
//    @Override
//    public
//    AnnotatedMethodHandler2 getHandler(String desc) {
//        wasCalled = true;
//        return super.getHandler(desc);
//    }
//}
