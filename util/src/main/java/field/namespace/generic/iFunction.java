package field.namespace.generic;

/**
* Created by jason on 7/29/14.
*/
public
interface IFunction<I, O> {
    public
    O apply(I in);
}
