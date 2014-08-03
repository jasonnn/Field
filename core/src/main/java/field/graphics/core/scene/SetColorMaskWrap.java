package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.glColorMask;

/**
 * Created by jason on 8/2/14.
 */
public
class SetColorMaskWrap extends TwoPassElement {
    private boolean b;

    private final boolean red;

    private final boolean green;

    private final boolean blue;

    private final boolean alpha;

    public
    SetColorMaskWrap(boolean red, boolean green, boolean blue, boolean alpha) {
        super("", StandardPass.preTransform, StandardPass.postRender);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    boolean first = true;

    @Override
    protected
    void post() {

        if (BasicUtilities.thinState)
            return;

        glColorMask(true, true, true, true);
    }

    @Override
    protected
    void pre() {
        glColorMask(red, green, blue, alpha);
    }

    @Override
    protected
    void setup() {
    }
}
