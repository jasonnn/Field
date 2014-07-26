package field.util;

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by jason on 7/25/14.
 */
public abstract
class MapWithDefault<K, V> extends ForwardingMap<K, V> {

    public static
    interface DefaultValueProvider<K, V> {
        V defaultValue(K key);
    }


    public static
    <K, V> MapWithDefault<K, V> newMapWithDefault(V defaultValue) {
        return new MapWithDefaultImpl<K, V>(new ConstantDefault<K, V>(defaultValue));
    }

    public static
    <K, V> MapWithDefault<K, V> newMapWithDefault(MapWithDefault.DefaultValueProvider<K, V> defaultValueProvider) {
        return new MapWithDefaultImpl<K, V>(defaultValueProvider);
    }

    public static
    <K, V> MapWithDefault<K, V> newMapWithDefault(Callable<V> defaultValueProvider) {
        return new MapWithDefaultImpl<K, V>(new CallableDefault<K, V>(defaultValueProvider));
    }

    static
    class CallableDefault<K, V> implements MapWithDefault.DefaultValueProvider<K, V> {
        private final Callable<V> defaultProducer;

        CallableDefault(Callable<V> defaultProducer) {this.defaultProducer = defaultProducer;}

        @Override
        public
        V defaultValue(K key) {
            try {
                return defaultProducer.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static
    class ConstantDefault<K, V> implements MapWithDefault.DefaultValueProvider<K, V> {


        private final V defaultVal;

        ConstantDefault(V defaultVal) {this.defaultVal = defaultVal;}

        @Override
        public
        V defaultValue(K key) {
            return defaultVal;
        }
    }

    static
    class MapWithDefaultImpl<K, V> extends MapWithDefault<K, V> {
        private final DefaultValueProvider<K, V> valueProvider;

        MapWithDefaultImpl(DefaultValueProvider<K, V> valueProvider) {this.valueProvider = valueProvider;}

        @Override
        protected
        V defaultValue(K key) {
            return valueProvider.defaultValue(key);
        }
    }

    protected
    MapWithDefault(Map<K, V> delegate) {
        this.delegate = delegate;
    }
    public MapWithDefault(){}

    protected Map<K, V> delegate;

    @Override
    protected
    Map<K, V> delegate() {
        if (delegate == null) delegate = createDelegate();
        return delegate;
    }

    protected
    Map<K, V> createDelegate() {
        return new HashMap<K, V>();
    }

    protected abstract
    V defaultValue(K key);

    @SuppressWarnings("unchecked")
    @Override
    public
    V get(Object key) {
        V val = super.get(key);
        if (val == null) {
            val = defaultValue((K) key);
            put((K) key, val);
        }
        return val;
    }


}
