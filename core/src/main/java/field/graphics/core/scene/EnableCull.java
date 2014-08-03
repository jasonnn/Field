package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by jason on 8/2/14.
 */
public
class EnableCull extends OnePassElement {

    private final Boolean b;

    public
    EnableCull() {
        super(StandardPass.render);
        this.b = null;
    }

    public
    EnableCull(Boolean b) {
        super(StandardPass.render);
        this.b = b;
    }

    @Override
    public
    void performPass() {
        if (BasicUtilities.thinState)
            return;

        glCullFace((b == null ? BasicUtilities.back : b) ? GL_BACK : GL_FRONT);
        glEnable(GL_CULL_FACE);
    }

}
