package field.graphics.core.scene;

import field.graphics.core.BasicGeometry;
import field.graphics.core.pass.StandardPass;
import field.math.abstraction.IFloatProvider;

/**
 * Created by jason on 8/2/14.
 */
public
class ChangeLineWidth extends TwoPassElement implements ISceneListElement {

    private float o;

    private final IFloatProvider f;

    public
    ChangeLineWidth(String name, IFloatProvider f) {
        super(name, StandardPass.preTransform, StandardPass.preDisplay);
        this.f = f;
    }

    @Override
    protected
    void post() {
        BasicGeometry.globalLineScale = o;
    }

    @Override
    protected
    void pre() {
        o = BasicGeometry.globalLineScale;
        BasicGeometry.globalLineScale = f.evaluate() * o;
    }

    @Override
    protected
    void setup() {
    }
}
