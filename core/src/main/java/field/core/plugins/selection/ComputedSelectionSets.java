package field.core.plugins.selection;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.selection.SelectionSetDriver.iSelectionPredicate;
import field.core.windowing.components.DraggableComponent;
import field.core.windowing.components.PlainDraggableComponent;
import field.core.windowing.components.iComponent;

import java.util.HashSet;
import java.util.Set;

public
class ComputedSelectionSets {

    public static
    class ByClass implements iSelectionPredicate {

        private final Set<Class<?>> c;

        public
        ByClass(Class c) {
            this.c = new HashSet<Class<?>>();
            this.c.add(c);
        }

        public
        ByClass(Set<Class<?>> c2) {
            this.c = c2;
        }

        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
        }

        public
        boolean is(IVisualElement e) {
            iComponent component = IVisualElement.localView.get(e);
            IVisualElementOverrides overrides = IVisualElement.overrides.get(e);
            for (Class c : this.c) {
                if (c.isInstance(overrides)) return true;
            }
            return false;
        }
    }

    public static
    class ByComponentClass implements iSelectionPredicate {

        private final Set<Class<?>> c;

        public
        ByComponentClass(Class c) {
            this.c = new HashSet<Class<?>>();
            this.c.add(c);
        }

        public
        ByComponentClass(Set<Class<?>> c2) {
            this.c = c2;
        }

        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
        }

        public
        boolean is(IVisualElement e) {
            iComponent component = IVisualElement.localView.get(e);
            for (Class c : this.c) {
                if (c.isInstance(component)) return true;
            }
            return false;
        }
    }

    public static
    class ByClass_computedSpline implements iSelectionPredicate {
        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
        }

        public
        boolean is(IVisualElement e) {
            iComponent component = IVisualElement.localView.get(e);
            IVisualElementOverrides overrides = IVisualElement.overrides.get(e);
            return component instanceof PlainDraggableComponent && overrides instanceof SplineComputingOverride;
        }
    }

    public static
    class ByClass_plainPython implements iSelectionPredicate {
        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
        }

        public
        boolean is(IVisualElement e) {
            iComponent component = IVisualElement.localView.get(e);
            IVisualElementOverrides overrides = IVisualElement.overrides.get(e);
            return component instanceof DraggableComponent
                   && overrides instanceof DefaultOverride;
        }
    }


    public static
    class CurrentlySelected implements iSelectionPredicate {
        private Set<IVisualElement> currentlySelected;

        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
            this.currentlySelected = currentlySelected;
        }

        public
        boolean is(IVisualElement e) {
            return currentlySelected.contains(e);
        }
    }

    public static
    class Saved implements iSelectionPredicate {
        private final Set<String> saved;

        public
        Saved(Set<IVisualElement> saved) {
            super();
            this.saved = new HashSet<String>();
            for (IVisualElement v : saved) {
                this.saved.add(v.getUniqueID());
            }
        }

        public
        void begin(Set<IVisualElement> everything,
                   Set<IVisualElement> currentlySelected,
                   Set<IVisualElement> previousCache) {
        }

        public
        boolean is(IVisualElement e) {
            return saved.contains(e.getUniqueID());
        }
    }

}
