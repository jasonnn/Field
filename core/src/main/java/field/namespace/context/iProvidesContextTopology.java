package field.namespace.context;

/**
 * Created by jason on 7/14/14.
 */
public
interface iProvidesContextTopology<K, I> {
    public
    ContextTopology<K, I> getContextTopology();
}
