package field.core.ui;

import field.math.graph.IMutableContainer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import java.util.List;

public
class GraphNodeToTree {


    private final Tree target;

    public
    GraphNodeToTree(Tree target) {
        this.target = target;
    }

    public
    void reset(IMutableContainer m) {
        target.removeAll();

        for (Object o : m.getChildren())
            populate((IMutableContainer) o, target);

    }

    private static
    void populate(IMutableContainer m, Widget i) {

        TreeItem item = (i instanceof TreeItem) ? new TreeItem(((TreeItem) i), 0) : new TreeItem((Tree) i, 0);
        item.setText(String.valueOf(m));
        item.setData(m.payload());

        List<IMutableContainer> c = m.getChildren();
        for (IMutableContainer cc : c) {
            populate(cc, item);
        }

    }

}
