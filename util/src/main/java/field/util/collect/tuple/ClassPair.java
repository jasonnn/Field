package field.util.collect.tuple;

/**
 * Created by jason on 7/21/14.
 */
public
class ClassPair<A, B> extends Pair<Class<A>, Class<B>> {


    public
    ClassPair(Class<A> aClass, Class<B> bClass) {
        super(aClass, bClass);
    }
}
