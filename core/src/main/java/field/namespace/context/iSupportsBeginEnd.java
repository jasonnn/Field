package field.namespace.context;

/**
* Created by jason on 7/14/14.
*/
public interface iSupportsBeginEnd<t_Key> {
    public void begin(t_Key b);

    public void end(t_Key b);

    public Class<t_Key> getBeginEndSupportedClass();
}
