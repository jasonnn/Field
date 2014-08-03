package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.glBlendEquation;

/**
 * Created by jason on 8/2/14.
 */
public
class SetBlendEquation extends OnePassElement {

    private final int mode;

    public
    SetBlendEquation(StandardPass requestPass, int mode) {
        super(requestPass);
        this.mode = mode;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        glBlendEquation(mode);
        glEnable(GL_BLEND);
    }
}
