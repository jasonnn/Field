package field.namespace.context;

public
interface ISupportsBeginEnd<T> {
    public
    void begin(T b);

    public
    void end(T b);

    public
    Class<T> getBeginEndSupportedClass();
}
