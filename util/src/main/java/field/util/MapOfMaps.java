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

    public
    MapOfMaps() {
    }

    MapOfMaps(Map<K, Map<KM, VM>> delegate) {
        super(delegate);
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

    public
    MapOfMaps<K, KM, VM> copy() {
        if (delegate == null) return create();
        Map<K, Map<KM, VM>> delegateCopy = new HashMap<K, Map<KM, VM>>(delegate.size());
        for (Map.Entry<K, Map<KM, VM>> entry : entrySet()) {
            delegateCopy.put(entry.getKey(), new HashMap<KM, VM>(entry.getValue()));
        }
        return new MapOfMaps<K, KM, VM>(delegateCopy);
    }
}
