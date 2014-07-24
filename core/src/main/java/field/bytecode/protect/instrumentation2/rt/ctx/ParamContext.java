package field.bytecode.protect.instrumentation2.rt.ctx;

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 7/23/14.
 */
public
class ParamContext extends ForwardingMap<ParamKey, Object> {
    private Map<ParamKey, Object> extraProps;

    @Override
    protected
    Map<ParamKey, Object> delegate() {
        if (extraProps == null) extraProps = new HashMap<ParamKey, Object>();
        return extraProps;
    }
}
