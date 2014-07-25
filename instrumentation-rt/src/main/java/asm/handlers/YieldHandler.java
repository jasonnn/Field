package asm.handlers;

/**
* Created by jason on 7/14/14.
*/
public interface YieldHandler {

    public int yieldIndexFor(String fromName, Object fromThis, String methodName);

    public Object[] yieldLoad(String fromName, Object fromThis, String methodName);

    public Object yieldStore(Object wasReturn, Object[] localStorage, String fromName, Object fromThis, String methodName, int resumeLabel);
}
