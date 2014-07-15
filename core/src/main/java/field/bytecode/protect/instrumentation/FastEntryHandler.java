package field.bytecode.protect.instrumentation;

/**
* Created by jason on 7/14/14.
*/
public interface FastEntryHandler {
    public void handle(int fromName, Object fromThis, Object[] argArray);
}
