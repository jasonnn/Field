package field.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 7/24/14.
 */
public
class MapOfMaps<K, KM, VM> extends MapWithDefault<K, Map<KM, VM>> {
    public static
    <K, KM, VM> MapOfMaps<K, KM, VM> create() {
        return new MapOfMaps<K, KM, VM>();
    }

    @Override
    protected
    Map<K, Map<KM, VM>> createDelegate() {
        return new HashMap<K, Map<KM, VM>>(2);
    }

    @Override
    protected
    Map<KM, VM> defaultValue(K key) {
        return new HashMap<KM, VM>(2);
    }
}
