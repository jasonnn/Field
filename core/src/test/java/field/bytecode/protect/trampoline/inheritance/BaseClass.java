package field.bytecode.protect.trampoline.inheritance;

/**
 * Created by jason on 7/16/14.
 */
@MyAnnotation
public abstract class BaseClass {
    @MyAnnotation
    abstract void someMethod();

    abstract void notAnnotated();

}
