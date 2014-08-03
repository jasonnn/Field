package field.graphics.core.pass;

/**
 * represents a rendering pass, Passes are interned by the iSceneList, so you can always use == to check to see what you should do
 */
public
interface IPass {
    public
    boolean isLaterThan(IPass p);

    public
    boolean isEarlierThan(IPass p);

    public
    float getValue();
}
