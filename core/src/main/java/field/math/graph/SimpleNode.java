package field.math.graph;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public
class SimpleNode<T> implements IMutableContainer<T, SimpleNode<T>>, Serializable {

    static final long serialVersionUID = -3352742657632020774L;

    T payload;

    LinkedHashSet<WeakReference<INotification<? super SimpleNode<T>>>> notes =
            new LinkedHashSet<WeakReference<INotification<? super SimpleNode<T>>>>();

    List<SimpleNode<T>> children = new ArrayList<SimpleNode<T>>();

    List<IMutable<SimpleNode<T>>> parents = new ArrayList<IMutable<SimpleNode<T>>>();

    public
    SimpleNode<T> setPayload(T t) {
        payload = t;
        boolean cleanNeeded = false;
        for (WeakReference<INotification<? super SimpleNode<T>>> n : notes) {
            if (n instanceof IMutableContainerNotification) {
                IMutableContainerNotification<T, SimpleNode<T>> h =
                        (IMutableContainerNotification<T, SimpleNode<T>>) n.get();
                if (h != null) h.payloadChanged(this, t);
                else cleanNeeded = true;
            }
        }
        if (cleanNeeded) clean(notes);
        return this;
    }

    public
    void addChild(SimpleNode<T> newChild) {
        children.add(newChild);
        boolean cleanNeeded = false;
        newChild.notifyAddParent(this);
        for (WeakReference<INotification<? super SimpleNode<T>>> n : notes) {
            INotification<? super SimpleNode> nn = (INotification<? super SimpleNode>) n.get();
            if (nn != null) nn.newRelationship(this, newChild);
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    public
    void notifyAddParent(IMutable<SimpleNode<T>> newParent) {
        parents.add(newParent);
    }

    public
    void removeChild(SimpleNode<T> newChild) {
        children.remove(newChild);
        newChild.notifyRemoveParent(this);
        boolean cleanNeeded = false;
        for (WeakReference<INotification<? super SimpleNode<T>>> n : notes) {
            INotification<? super SimpleNode> nn = (INotification<? super SimpleNode>) n.get();
            if (nn != null) nn.deletedRelationship(this, newChild);
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    public
    void notifyRemoveParent(IMutable<SimpleNode<T>> newParent) {
        parents.remove(newParent);
    }

    int changeCount = 0;

    public
    void beginChange() {
        changeCount++;
        boolean cleanNeeded = false;
        if (changeCount == 1) for (WeakReference<INotification<? super SimpleNode<T>>> n : notes) {
            INotification<? super SimpleNode> nn = (INotification<? super SimpleNode>) n.get();
            if (nn != null) nn.beginChange();
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    public
    void endChange() {
        changeCount--;
        boolean cleanNeeded = false;
        if (changeCount == 0) for (WeakReference<INotification<? super SimpleNode<T>>> n : notes) {
            INotification<? super SimpleNode> nn = (INotification<? super SimpleNode>) n.get();
            if (nn != null) nn.endChange();
            else cleanNeeded = true;
        }
        if (cleanNeeded) clean(notes);
    }

    private
    void clean(LinkedHashSet<WeakReference<INotification<? super SimpleNode<T>>>> notes2) {
        Iterator<WeakReference<INotification<? super SimpleNode<T>>>> i = notes2.iterator();
        while (i.hasNext()) if (i.next().get() == null) i.remove();
    }

    public
    void registerListener(INotification<IMutable<SimpleNode<T>>> note) {
        notes.add(new WeakReference<INotification<? super SimpleNode<T>>>(note));
    }

    public
    void deregisterListener(INotification<IMutable<SimpleNode<T>>> note) {
        Iterator<WeakReference<INotification<? super SimpleNode<T>>>> w = notes.iterator();
        while (w.hasNext()) if (w.next().get() == note) w.remove();
    }

    public
    void catchupListener(INotification<IMutable<SimpleNode<T>>> note) {
        for (SimpleNode<T> c : children) {
            note.newRelationship(this, c);
        }
    }

    public
    List<IMutable<SimpleNode<T>>> getParents() {
        return parents;
    }

    public
    List<SimpleNode<T>> getChildren() {
        return children;
    }

    public
    T payload() {
        return payload;
    }

    @Override
    public
    String toString() {
        return "n:{" + payload() + '}';
    }
}
