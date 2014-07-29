package field.math.graph;

import field.bytecode.protect.annotations.HiddenInAutocomplete;
import field.namespace.generic.IFunction;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@HiddenInAutocomplete
public
class NodeImpl<C extends IMutable<C>> implements IMutable<C>, Serializable {

    static final long serialVersionUID = -4300376251521807702L;


    protected List<C> children = new ArrayList<C>();
    protected List<IMutable<C>> parents = new ArrayList<IMutable<C>>();

    protected transient LinkedHashSet<WeakReference<INotification<IMutable<C>>>> notes =
            new LinkedHashSet<WeakReference<INotification<IMutable<C>>>>();

    protected boolean reverseInsertionOrder = false;

    @HiddenInAutocomplete
    public
    void addChild(C newChild) {
        if (reverseInsertionOrder) children.add(0, newChild);
        else children.add(newChild);
        newChild.notifyAddParent(this);
        boolean cleanNeeded = false;

        for (WeakReference<INotification<IMutable<C>>> n : new ArrayList<WeakReference<INotification<IMutable<C>>>>(notes)) {
            INotification<IMutable<C>> note = n.get();
            if (note != null) note.newRelationship(this, newChild);
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    public
    void main() {
    }

    private
    void clean(LinkedHashSet<WeakReference<INotification<IMutable<C>>>> notes2) {
        Iterator<WeakReference<INotification<IMutable<C>>>> w = notes2.iterator();
        while (w.hasNext()) if (w.next().get() == null) w.remove();
    }

    public
    void notifyAddParent(IMutable<C> newParent) {
        parents.add(newParent);
    }

    @HiddenInAutocomplete
    public
    void removeChild(C newChild) {
        children.remove(newChild);
        newChild.notifyRemoveParent(this);
        boolean cleanNeeded = false;
        for (WeakReference<INotification<IMutable<C>>> n : new ArrayList<WeakReference<INotification<IMutable<C>>>>(notes)) {
            INotification<IMutable<C>> note = n.get();
            if (note != null) note.deletedRelationship(this, newChild);
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    public
    void removeAll() {
        for (C c : new ArrayList<C>(children))
            removeChild(c);
    }

    public
    void removeMatching(IFunction<C, Boolean> cc) {
        for (C c : new ArrayList<C>(children))
            if (cc.apply(c)) removeChild(c);
    }

    public
    void notifyRemoveParent(IMutable<C> newParent) {
        parents.remove(newParent);
    }

    int changeCount = 0;

    public
    void beginChange() {
        changeCount++;
        boolean cleanNeeded = false;
        if (changeCount == 1)
            for (WeakReference<INotification<IMutable<C>>> n : new ArrayList<WeakReference<INotification<IMutable<C>>>>(notes)) {
                INotification<IMutable<C>> nn = n.get();
                if (nn != null) nn.beginChange();
                else cleanNeeded = true;
            }
        if (cleanNeeded) clean(notes);
    }

    public
    void endChange() {
        changeCount--;
        boolean cleanNeeded = false;
        if (changeCount == 0)
            for (WeakReference<INotification<IMutable<C>>> n : new ArrayList<WeakReference<INotification<IMutable<C>>>>(notes)) {
                INotification<IMutable<C>> nn = n.get();
                if (nn != null) nn.endChange();
                else cleanNeeded = true;
            }
        if (cleanNeeded) clean(notes);
    }

    public
    List<? extends IMutable<C>> getParents() {
        return parents;
    }

    public
    List<C> getChildren() {
        return children;
    }

    public
    void registerListener(INotification<IMutable<C>> note) {
        notes.add(new MWeakReference(note));
    }

    public static
    class MWeakReference<T> extends WeakReference<T> {

        public
        MWeakReference(T referent) {
            super(referent);
        }

        @Override
        public
        boolean equals(Object obj) {
            return (obj instanceof MWeakReference) && (((MWeakReference) obj).get() == this.get());
        }

        @Override
        public
        int hashCode() {
            T t = this.get();
            return (t == null) ? 0 : t.hashCode();
        }

    }

    public
    void deregisterListener(INotification<IMutable<C>> note) {
        Iterator<WeakReference<INotification<IMutable<C>>>> w = notes.iterator();
        while (w.hasNext()) if (w.next().get() == note) w.remove();
    }

    public
    void catchupListener(INotification<IMutable<C>> note) {
        for (C c : children) {
            note.newRelationship(this, c);
        }
    }
}
