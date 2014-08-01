package field.core.dispatch.override;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.plugins.log.ElementInvocationLogging;
import field.core.plugins.log.Logging;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

/**
* Created by jason on 7/31/14.
*/
public
class DefaultOverride extends IVisualElementOverridesAdaptor implements IDefaultOverride {
    public IVisualElement forElement;

    public
    DefaultOverride() {
    }

    @Override
    public
    TraversalHint deleted(IVisualElement source) {
        if (source == forElement) {
            source.dispose();
        }
        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
        if (source == forElement) {
            VisualElementProperty<T> a = prop.getAliasedTo();
            while (a != null) {
                prop = a;
                a = a.getAliasedTo();
            }

            forElement.deleteProperty(prop);
        }
        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
        if ((ref.to == null || (source == forElement)) && forElement != null) {

            VisualElementProperty<T> a = prop.getAliasedTo();
            while (a != null) {
                prop = a;
                a = a.getAliasedTo();
            }
            assert forElement != null : "problem in class " + this.getClass();
            T property = forElement.getProperty(prop);
            if (property != null) {
                ref.set(property, forElement);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
        if (source == forElement) {
            VisualElementProperty<T> a = prop.getAliasedTo();
            while (a != null) {
                prop = a;
                a = a.getAliasedTo();
            }

            if (Logging.enabled()) Logging.logging.addEvent(new ElementInvocationLogging.DidSetProperty(prop,
                                                                                                        source,
                                                                                                        to.to,
                                                                                                        forElement.getProperty(prop)));
            forElement.setProperty(prop, to.get());
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    field.core.dispatch.override.DefaultOverride setVisualElement(IVisualElement ve) {
        forElement = ve;
        return this;
    }

    @Override
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        if (source == forElement) {
            forElement.setFrame(newFrame);
            return StandardTraversalHint.CONTINUE;
        }
        return StandardTraversalHint.CONTINUE;
    }
}
