package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class LineStipple extends TwoPassElement {

    public
    LineStipple() {
        super("", StandardPass.preRender, StandardPass.postRender);
    }

    int a = 1;

    short mask = (short) 0xffff;

    @Override
    public
    void pre() {
        if (BasicUtilities.thinState)
            return;

        glEnable(GL_LINE_STIPPLE);
        glLineStipple(a, mask);

    }

    @Override
    protected
    void post() {
        glDisable(GL_LINE_STIPPLE);
    }

    @Override
    protected
    void setup() {
        // TODO Auto-generated method stub

    }

}
