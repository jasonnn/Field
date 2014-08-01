package field.core.dispatch.override;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.launch.IUpdateable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.Dict;
import org.eclipse.swt.widgets.Event;

import java.util.List;
import java.util.Map;

/**
* Created by jason on 7/31/14.
*/
public
class IVisualElementOverridesAdaptor implements IVisualElementOverrides {
    public
    TraversalHint added(IVisualElement newSource) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint beginExecution(IVisualElement source) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint deleted(IVisualElement source) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint endExecution(IVisualElement source) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint inspectablePropertiesFor(IVisualElement source, List<Dict.Prop> properties) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint prepareForSave() {
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        return StandardTraversalHint.CONTINUE;
    }

}
