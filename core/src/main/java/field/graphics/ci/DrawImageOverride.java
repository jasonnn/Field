package field.graphics.ci;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.util.PythonCallableMap;
import field.math.graph.visitors.hint.TraversalHint;

public
class DrawImageOverride extends DefaultOverride {

    VisualElementProperty<PythonCallableMap> images = new VisualElementProperty<PythonCallableMap>("images_");

    public
    DrawImageOverride() {
    }

    @Override
    public
    DefaultOverride setVisualElement(IVisualElement ve) {
        ve.setProperty(images, new PythonCallableMap());
        super.setVisualElement(ve);
        return this;
    }

    @Override
    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
        if (source == forElement) {
            if (forElement.getProperty(images) == null) forElement.setProperty(images, new PythonCallableMap());
        }
        return super.getProperty(source, prop, ref);
    }

    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        //System.out.println(" ? ");
        if (source == forElement) {
            PythonCallableMap m = images.get(source);
            if (m != null) {
                m.invoke();
            }
        }
        return super.paintNow(source, bounds, visible);
    }
}
