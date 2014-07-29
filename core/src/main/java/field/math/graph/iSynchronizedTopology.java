package field.math.graph;

import java.util.List;


public
interface ISynchronizedTopology<T> extends IMutableTopology<T> {
    public
    void added(T t);

    public
    void removed(T t);

    public
    void update(T t);

    public
    List<T> getAll();

}