package field.bytecode.protect.instrumentation;

import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public interface ExitHandler {
    public Object handleExit(Object returningThis,
                             String fromName,
                             Object fromThis,
                             String methodName,
                             Map<String, Object> parameters,
                             String methodReturnName);
}
