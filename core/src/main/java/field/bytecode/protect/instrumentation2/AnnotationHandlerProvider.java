package field.bytecode.protect.instrumentation2;

/**
 * Created by jason on 7/21/14.
 */
public interface AnnotationHandlerProvider {
    boolean canHandle(String annDesc);

    AnnotatedMethodHandler2 getHandler(String annDesc);
}
