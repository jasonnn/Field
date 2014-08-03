package field.graphics.core.scene;

import field.graphics.core.CoreHelpers;
import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class NoLineSmooth extends OnePassElement {

    public
    NoLineSmooth() {
        super(StandardPass.preRender);

    }

    @Override
    public
    void performPass() {

        if (BasicUtilities.thinState)
            return;

        glEnable(GL_BLEND);

        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        // glEnable(GL_POLYGON_SMOOTH);
        // glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        if (!CoreHelpers.isCore) {
            glDisable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        }
    }
}
