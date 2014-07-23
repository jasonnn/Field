package field.bytecode.protect.instrumentation;

import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public interface DeferedHandler {
    public void handleDefered(String fromName,
                              Object fromThis,
                              String methodName,
                              Map<String, Object> parameters,
                              Object[] argArray,
                              Class[] argTypeArray);
}
