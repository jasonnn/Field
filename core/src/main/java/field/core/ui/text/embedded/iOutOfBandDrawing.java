package field.core.ui.text.embedded;

import field.core.dispatch.Rect;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;

public
interface iOutOfBandDrawing {

    public
    void paintOutOfBand(GC gc, StyledText ed);

    public
    void expandDamage(Rect d);

}
