package field.graphics.core.scene;

import field.graphics.core.BasicContextManager;
import field.graphics.core.pass.IPass;
import field.math.graph.IMutable;

/**
 * Created by jason on 8/2/14.
 */
public abstract
class ContextWrapper extends BasicSceneList implements ISceneListElement {

    protected ISceneListElement[] one;

    public
    ContextWrapper(ISceneListElement[] one) {
        this.one = one;
    }

    @Override
    public
    void notifyAddParent(IMutable<ISceneListElement> list) {
        super.notifyAddParent(list);
        for (ISceneListElement anOne : one)
            anOne.notifyAddParent(list);
    }

    @Override
    public
    void performPass(IPass p) {
        one[indexForContext(BasicContextManager.getCurrentContext())].performPass(p);
    }

    protected abstract
    int indexForContext(Object object);

}
