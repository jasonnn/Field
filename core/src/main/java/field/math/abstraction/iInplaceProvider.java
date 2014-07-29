package field.math.abstraction;

/**
 * Similar to iObjectProvider, but inplace.
 *
 * @author synchar
 * @see iObjectProvider
 */
public
interface IInplaceProvider<T> {
    public
    T get(T o);
}
