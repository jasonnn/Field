package asm.handlers;

import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public interface EntryHandler {
    public void handleEntry(String fromName,
                            Object fromThis,
                            String methodName,
                            Map<String, Object> parameters,
                            Object[] argArray);
}
