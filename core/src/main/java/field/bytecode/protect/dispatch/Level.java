package field.bytecode.protect.dispatch;

/**
* Created by jason on 7/14/14.
*/
public class Level {
    String name;

    Apply topology;

    int seen;

    public Object[] args;

    public DispatchProvider provider;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Level)) return false;
        return ((Level) obj).name.equals(name) && ((Level) obj).topology.equals(topology);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + ((topology == null) ? 0 : topology.hashCode());
    }
}
