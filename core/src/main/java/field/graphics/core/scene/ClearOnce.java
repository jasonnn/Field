package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;
import field.math.linalg.Vector3;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author marc
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and
 *         Comments
 */
public
class ClearOnce extends OnePassElement implements ISceneListElement {

    Vector3 background = new Vector3(0.1, 0.1, 0.1);

    float alpha = 0;

    int tick = 0;

    public
    ClearOnce(Vector3 colour) {
        super(StandardPass.render);
        background = colour;
    }

    public
    ClearOnce(Vector3 colour, float alpha) {
        super(StandardPass.render);
        background = colour;
        this.alpha = alpha;
    }

    @Override
    public
    void performPass() {
        tick++;
        if (tick < 4) {
            glClearColor(background.get(0), background.get(1), background.get(2), alpha);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            // glClear(GL_DEPTH_BUFFER_BIT);
        }

    }

    public
    void setClearColor(Vector3 c) {
        background = c;
    }

    public
    void setClearColor(Vector3 c, float alpha) {
        background = c;
        this.alpha = alpha;
    }

}
