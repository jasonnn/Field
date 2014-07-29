package field.core.dispatch;

import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.launch.IUpdateable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.Dict.Prop;
import org.eclipse.swt.widgets.Event;

import java.util.*;

/**
 * we're 'de-abstracting' our Dispatch<x,y> code for the purposes of speed. For
 * many things, this has become part of our inner loops.
 *
 * @author marc
 */
public
class FastVisualElementOverridesDispatch implements IVisualElementOverrides {

    private final boolean backwards;

    public
    FastVisualElementOverridesDispatch(boolean backwards) {
        this.backwards = backwards;
    }

    public
    TraversalHint added(IVisualElement newSource) {

        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.added(newSource);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint beginExecution(IVisualElement source) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.beginExecution(source);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint deleteProperty(IVisualElement source, VisualElementProperty<T> prop) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.deleteProperty(source, prop);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint deleted(IVisualElement source) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.deleted(source);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint endExecution(IVisualElement source) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.endExecution(source);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {

        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.getProperty(source, prop, ref);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint handleKeyboardEvent(IVisualElement newSource, Event event) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.handleKeyboardEvent(newSource, event);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint inspectablePropertiesFor(IVisualElement source, List<Prop> properties) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.inspectablePropertiesFor(source, properties);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {

        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.isHit(source, event, is);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {

        //System.out.println(" menu items for <"+source+">  starting at <"+iVisualElementOverrides.topology.getAt()+">");

        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.menuItemsFor(source, items);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());


                        //System.out.println(" fringe now <"+fringe+"> elements now <"+items.size()+">");

                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.paintNow(source, bounds, visible);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint prepareForSave() {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.prepareForSave();
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.setProperty(source, prop, to);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {
        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(IVisualElementOverrides.topology.getAt());

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {
                    TraversalHint o = over.shouldChangeFrame(source, newFrame, oldFrame, now);
                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return StandardTraversalHint.STOP;
                    }
                    else {
                        if (backwards) fringe.addAll((Collection<? extends IVisualElement>) next.getParents());
                        else fringe.addAll(next.getChildren());
                    }
                }
                seen.add(next);
            }
        }
        return StandardTraversalHint.CONTINUE;
    }

}
