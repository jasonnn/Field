package field.core.persistance;

import field.core.dispatch.IVisualElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// a tag class for special handling of loading
public
class VEList extends ArrayList<IVisualElement> implements List<IVisualElement> {

    public
    VEList(VEList list) {
        super(list);
    }

    public
    VEList() {
    }

    @NotNull
    @Override
    public
    Iterator<IVisualElement> iterator() {
        scrubNull();
        return super.iterator();
    }

    protected
    void scrubNull() {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) == null) {
                this.remove(i);
                i--;
            }
        }
    }
}
