package field.bytecode.protect.instrumentation2.rt.ctx;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jason on 7/23/14.
 */
@SuppressWarnings("unchecked")
public
class ParamKey<T> {
    public static
    <T> ParamKey<T> create() {
        return new ParamKey<T>();
    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final int id;

    ParamKey() {
        this.id = counter.getAndIncrement();
    }

    @Nullable
    public
    T get(@Nullable Map<ParamKey, ?> map) {
        return (map == null) ? null : (T) map.get(this);
    }


    @Contract("null,null->null")
    public
    T get(Map<ParamKey, ?> map, T defaultVal) {
        T val = get(map);
        return (val == null) ? defaultVal : val;
    }


    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) return false;

        ParamKey paramKey = (ParamKey) o;

        return id == paramKey.id;

    }

    @Override
    public
    int hashCode() {
        return id;
    }
}
