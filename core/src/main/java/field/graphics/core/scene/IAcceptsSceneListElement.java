package field.graphics.core.scene;

/**
 * Created by jason on 8/2/14.
 */
public
interface IAcceptsSceneListElement {
    public
    void addChild(ISceneListElement e);

    public
    void removeChild(ISceneListElement e);

    public
    boolean isChild(ISceneListElement e);

}
