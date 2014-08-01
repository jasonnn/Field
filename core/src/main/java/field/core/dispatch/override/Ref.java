package field.core.dispatch.override;

import field.bytecode.protect.BaseRef;
import field.core.dispatch.IVisualElement;

/**
* Created by jason on 7/31/14.
*/
public
class Ref<T> extends BaseRef<T> {
    public IVisualElement storageSource;

    public
    Ref(T to) {
        super(to);
    }

    public
    IVisualElement getStorageSource() {
        return storageSource;
    }

    public
    Ref<T> set(T to, IVisualElement storedBy) {
        this.to = to;
        this.storageSource = storedBy;
        unset = false;
        return this;
    }

}
