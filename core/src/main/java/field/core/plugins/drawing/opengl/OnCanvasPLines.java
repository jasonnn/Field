package field.core.plugins.drawing.opengl;

import field.graphics.core.scene.IAcceptsSceneListElement;
import field.graphics.windowing.FullScreenCanvasSWT;

/**
 * class for backwards compatability with Field 12 and 13
 */
public
class OnCanvasPLines extends OnCanvasLines {

    public
    OnCanvasPLines(IAcceptsSceneListElement on, FullScreenCanvasSWT canvas) {
        super(on, canvas);
    }

}
