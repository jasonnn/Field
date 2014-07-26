package field.core.ui.text.embedded;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;

public
interface iMinimalLayerPainter {

    public
    void associate(JComponent inside);

    public
    void paintNow(Graphics2D g, Dimension2D size);

}
