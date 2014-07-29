package field.math.graph;

/**
* Created by jason on 7/29/14.
*/
public
interface INotification<P extends IMutable> {
    public
    void beginChange();

    public
    void newRelationship(P parent, P child);

    public
    void deletedRelationship(P parent, P child);

    public
    void endChange();
}
