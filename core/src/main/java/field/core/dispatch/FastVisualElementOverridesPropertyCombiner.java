package field.core.dispatch;

import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.dispatch.IVisualElementOverrides.Ref;
import field.math.graph.IMutable;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.*;

public
class FastVisualElementOverridesPropertyCombiner<U, T> {

    /**
     * oh look, it's a monad
     */
    public
    interface iCombiner<U, T> {
        public
        T unit();

        public
        T bind(T t, U u);
    }

    private final boolean backwards;

    public
    FastVisualElementOverridesPropertyCombiner(boolean backwards) {
        this.backwards = backwards;
    }

    public
    T getProperty(IVisualElement source, VisualElementProperty<U> prop, iCombiner<U, T> combiner) {

        List<IVisualElement> fringe = new LinkedList<IVisualElement>();
        fringe.add(source);

        T at = combiner.unit();

        HashSet<IVisualElement> seen = new LinkedHashSet<IVisualElement>();


        Ref<U> ref = new Ref<U>(null);

        while (!fringe.isEmpty()) {
            IVisualElement next = fringe.remove(0);
            if (!seen.contains(next)) {
                IVisualElementOverrides over = next.getProperty(IVisualElement.overrides);
                if (over != null) {


                    ref.set(null);
                    TraversalHint o = over.getProperty(source, prop, ref);
                    U u = ref.get();
                    at = combiner.bind(at, u);

                    if (o.equals(StandardTraversalHint.SKIP)) {
                    }
                    else if (o.equals(StandardTraversalHint.STOP)) {
                        return at;
                    }
                    else {
                        if (backwards) fringe.addAll(sort(next.getParents()));
                        else fringe.addAll(sort(next.getChildren()));
                    }
                }
                seen.add(next);
            }
        }
        return at;
    }

    protected
    Collection<? extends IVisualElement> sort(List<? extends IMutable<IVisualElement>> parents) {
        return (Collection<? extends IVisualElement>) parents;
    }

}
