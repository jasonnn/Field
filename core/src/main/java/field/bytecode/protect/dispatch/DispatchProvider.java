package field.bytecode.protect.dispatch;

import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public interface DispatchProvider {
    public Apply getTopologyForEntrance(Object root, Map<String, Object> parameters, Object[] args, String className);

    public Apply getTopologyForExit(Object root, Map<String, Object> parameters, Object[] args, String className);

    public void notifyExecuteBegin(Object fromThis, Map<String, Object> parameterName);

    public void notifyExecuteEnds(Object fromThis, Map<String, Object> parameterName);
}
