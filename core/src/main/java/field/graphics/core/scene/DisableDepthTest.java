package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class DisableDepthTest extends OnePassElement {

    boolean enable = false;

    public
    DisableDepthTest() {
        super(StandardPass.preRender);

        // assert false : "disable depth
        // test makes point sprites
        // disappear";
    }

    /**
     * @param b
     */
    public
    DisableDepthTest(boolean b) {
        super(StandardPass.preRender);
        enable = !b;
        // assert false : "disable depth
        // test makes point sprites
        // disappear";
    }

    @Override
    public
    void performPass() {

        if (BasicUtilities.thinState)
            return;

        if (!enable) {
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_ALWAYS);
            glDepthMask(true);
        }
        else {
            glDepthFunc(GL_LESS);
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
        }
    }
}
