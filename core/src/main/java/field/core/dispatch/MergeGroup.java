package field.core.dispatch;

import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.override.Ref;
import field.core.plugins.python.PythonPluginEditor;
import field.core.windowing.components.iComponent;
import field.math.graph.IMutable;
import field.util.collect.tuple.Triple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/*
 * this could be extended to do three way merge with the contents of properties.
 * for this we'd have to save the properties themselves, not just the hashes
 */
public
class MergeGroup {

    public static final VisualElementProperty<Integer> mergeGroup_visitCount =
            new VisualElementProperty<Integer>("mergeGroup_visitCount_");

    public static final VisualElementProperty<Object> mergeGroup_token =
            new VisualElementProperty<Object>("mergeGroup_token");

    protected final IVisualElement owner;

    protected transient int open = 0;

    protected boolean lastWasNew = false;

    boolean defaultSave = true;

    int visitCount = 0;

    Map<Object, VisualElement> createdElements = new HashMap<Object, VisualElement>();

    Map<Object, iComponent> createdComponents = new HashMap<Object, iComponent>();

    Map<Object, IVisualElementOverrides> createdOverrides = new HashMap<Object, IVisualElementOverrides>();

    Map<VisualElement, Integer> propertyHashes = new HashMap<VisualElement, Integer>();

    boolean shouldResetFrames = true;

    public
    MergeGroup() {
        this.owner = null;
    }

    public
    MergeGroup(IVisualElement owner) {
        this.owner = owner;
        List<IVisualElement> parents = (List<IVisualElement>) getElementsForSynchronization(owner);
        for (IVisualElement v : parents) {
            Ref<Object> tok = new Ref<Object>(null);
            IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(v)
                                                           .getProperty(v, mergeGroup_token, tok);
            Object object = tok.get();
            if ((object != null) && (v instanceof VisualElement)) {
                createdElements.put(object, ((VisualElement) v));
                createdComponents.put(object, v.getProperty(IVisualElement.localView));
                createdOverrides.put(object, v.getProperty(IVisualElement.overrides));
            }
        }
    }

    public
    void begin() {
        open++;
        if (open == 1) {
            visitCount++;
            Iterator<Entry<Object, VisualElement>> i = createdElements.entrySet().iterator();
            while (i.hasNext()) {
                Entry<Object, VisualElement> e = i.next();
                VisualElement element = e.getValue();
                int hash = getPropertyHash(element);
                Integer oldHashI = propertyHashes.get(element);
                if (oldHashI != null) {
                    if (hash != oldHashI) {
                        element.setProperty(IVisualElement.doNotSave, false);
                    }
                }
            }
        }
    }

    public
    boolean contains(IVisualElement source) {
        return this.propertyHashes.containsKey(source);
    }

    public
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> Triple<T, S, U> create(Object token,
                                                                                                                              Rect bounds,
                                                                                                                              Class<T> visualElementclass,
                                                                                                                              Class<S> componentClass,
                                                                                                                              Class<U> overrideClass) {
        if (createdElements.containsKey(token)) {
            T t = (T) createdElements.get(token);
            S s = (S) createdComponents.get(token);
            U u = (U) createdOverrides.get(token);

            Triple<T, S, U> triple = new Triple<T, S, U>(t, s, u);
            triple.left.setProperty(mergeGroup_visitCount, visitCount);
            alreadyCreated(triple);
            if (shouldResetFrames) triple.left.setFrame(bounds);
            lastWasNew = false;

            return triple;
        }
        Triple<T, S, U> triple = VisualElement.create(bounds, visualElementclass, componentClass, overrideClass);
        triple.left.setProperty(mergeGroup_visitCount, visitCount);
        newlyCreated(triple);

        createdElements.put(token, triple.left);
        createdComponents.put(token, triple.middle);
        createdOverrides.put(token, triple.right);
        triple.left.setFrame(bounds);

        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(triple.left)
                                                       .setProperty(triple.left,
                                                                    mergeGroup_token,
                                                                    new Ref<Object>(token));
        if (!defaultSave) {
            triple.left.setProperty(IVisualElement.doNotSave, true);
        }

        lastWasNew = true;
        return triple;
    }

    public
    void end() {
        open--;
        if (open == 0) {

            Iterator<Entry<Object, VisualElement>> i = createdElements.entrySet().iterator();
            while (i.hasNext()) {
                Entry<Object, VisualElement> e = i.next();
                VisualElement element = e.getValue();
                assert element != null : createdElements;
                int hash = getPropertyHash(element);
                assert element != null : createdElements;
                Integer visitedAtI = element.getProperty(mergeGroup_visitCount);
                int vistedAt = (visitedAtI == null) ? -1 : visitedAtI;
                if (vistedAt != visitCount) {
                    Integer oldHashI = propertyHashes.get(element);
                    int oldHash = (oldHashI == null) ? -1 : oldHashI;


                    if (shouldCull(element, hash, oldHash)) {

                        PythonPluginEditor.delete(element, owner);
                    }
                    else {
                        lost(element);
                    }
                    createdComponents.remove(e.getKey());
                    createdOverrides.remove(e.getKey());
                    propertyHashes.remove(e.getValue());
                    i.remove();
                }
                else {
                    propertyHashes.put(e.getValue(), hash);
                }
            }
        }
    }

    public
    VisualElement findByToken(String string) {
        return createdElements.get(string);
    }

    public
    boolean getLastWasNew() {
        return lastWasNew;
    }

    public static
    int getPropertyHash(VisualElement element) {
        Map<Object, Object> name = element.payload();
        return name.hashCode();
    }

    public
    MergeGroup setDefaultSave(boolean save) {
        this.defaultSave = save;
        return this;
    }

    public
    MergeGroup setShouldResetFrames(boolean shouldResetFrames) {
        this.shouldResetFrames = shouldResetFrames;
        return this;
    }

    private static
    List<? extends IMutable<IVisualElement>> getElementsForSynchronization(IVisualElement owner) {
        return owner.getParents();
    }

    protected
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> void alreadyCreated(Triple<T, S, U> triple) {

    }

    protected
    void lost(VisualElement element) {
    }

    protected
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> void newlyCreated(Triple<T, S, U> triple) {
        parentNewlyCreated(triple);
        IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(triple.left).added(triple.left);
        IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(triple.left).added(triple.left);

    }

    protected
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> void parentNewlyCreated(Triple<T, S, U> triple) {
        triple.left.addChild(owner);
    }

    protected
    boolean shouldCull(VisualElement element, int hash, int oldHash) {
        return (hash == oldHash) || (oldHash == -1);
    }

}
