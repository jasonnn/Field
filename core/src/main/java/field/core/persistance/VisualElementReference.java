package field.core.persistance;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;

public
class VisualElementReference {

    String uid;
    transient IVisualElement cached;

    public
    VisualElementReference(String uid) {
        this.uid = uid;
    }

    public
    VisualElementReference(IVisualElement element) {
        this.uid = element.getUniqueID();
        this.cached = element;
    }

    public
    IVisualElement get(IVisualElement root) {
        return (cached == null) ? (cached = StandardFluidSheet.findVisualElement(root, uid)) : cached;
    }

    @Override
    public
    String toString() {
        return uid + ':' + cached;
    }
}
