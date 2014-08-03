package field.graphics.core.scene;

import field.math.linalg.CoordinateFrame;
import field.math.linalg.Quaternion;
import field.math.linalg.Vector3;

/**
 * Created by jason on 8/2/14.
 */
public
class Position extends CoordinateFrame {

    public
    Position() {
    }

    public
    Position(Vector3 v) {
        super.setTranslation(v);
    }

    public
    Position(Vector3 v, Quaternion q) {
        super.setTranslation(v);
        super.setRotation(q);
    }

}
