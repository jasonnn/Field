package field.graphics.core.scene;

import field.graphics.core.pass.IPass;
import field.launch.IUpdateable;
import field.math.graph.IMutable;

/**
 * call update to swap
 */

public
class ToggleWrapper extends BasicSceneList implements ISceneListElement, IUpdateable {

    protected ISceneListElement one;

    protected ISceneListElement two;

    boolean first = true;

    public
    ToggleWrapper(ISceneListElement one, ISceneListElement two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public
    void notifyAddParent(IMutable<ISceneListElement> list) {
        super.notifyAddParent(list);
        one.notifyAddParent(list);
        two.notifyAddParent(list);
    }

    @Override
    public
    void performPass(IPass p) {
        if (first)
            one.performPass(p);
        else
            two.performPass(p);
    }

    @Override
    public
    void update() {
        first = !first;
    }

}
