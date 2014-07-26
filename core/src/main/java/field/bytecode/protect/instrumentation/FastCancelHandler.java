package field.bytecode.protect.instrumentation;

/**
 * Created by jason on 7/14/14.
 */
public
interface FastCancelHandler {
    public
    Object handle(int fromName, Object fromThis, String originalMethodName, Object[] argArray);
}
