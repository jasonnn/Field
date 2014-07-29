package field.core.plugins.drawing;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElement;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.iComponent;

public
class OrderingUtils {

    public static
    void moveToFront(IVisualElement element) {
        GLComponentWindow frame = VisualElement.enclosingFrame.get(element);
        iComponent component = VisualElement.localView.get(element);

        if (frame != null && component != null) frame.getRoot().moveToFront(component);
    }

    public static
    void moveToBack(IVisualElement element) {
        GLComponentWindow frame = VisualElement.enclosingFrame.get(element);
        iComponent component = VisualElement.localView.get(element);

        if (frame != null && component != null) frame.getRoot().moveToBack(component);
    }

}
