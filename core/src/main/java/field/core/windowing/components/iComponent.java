/**
 *
 */
package field.core.windowing.components;

import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.windowing.GLComponentWindow.ComponentContainer;
import field.core.windowing.components.DraggableComponent.Resize;
import field.math.linalg.iCoordinateFrame;
import org.eclipse.swt.widgets.Event;

import java.util.Set;

@GenerateMethods
public
interface iComponent {
    // return depth
    @Mirror
    public
    float isHit(Event event);

    // return subcomponent hit
    @Mirror
    public
    iComponent hit(Event event);

    @Mirror
    public
    Rect getBounds();

    @Mirror
    public
    void setBounds(Rect r);

    // event demultiplexing
    @Mirror
    public
    void keyTyped(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void keyPressed(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void keyReleased(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseClicked(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mousePressed(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseReleased(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseEntered(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseExited(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseDragged(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void mouseMoved(ComponentContainer inside, Event arg0);

    @Mirror
    public
    void beginMouseFocus(ComponentContainer inside);

    @Mirror
    public
    void endMouseFocus(ComponentContainer inside);

    @Mirror
    public
    void paint(ComponentContainer inside, iCoordinateFrame frameSoFar, boolean visible);

    @Mirror
    public
    void handleResize(Set<Resize> currentResize, float dx, float dy);

    @Mirror
    public
    IVisualElement getVisualElement();

    @Mirror
    public
    iComponent setVisualElement(IVisualElement ve);

    public
    void setSelected(boolean selected);

    public
    boolean isSelected();

}