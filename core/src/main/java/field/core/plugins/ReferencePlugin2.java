package field.core.plugins;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElementProperty;
import field.core.plugins.selection.DrawTopology;
import field.core.plugins.selection.DrawTopology.DefaultDrawer;
import field.math.graph.ITopology;

import java.util.*;
import java.util.Map.Entry;

public
class ReferencePlugin2 extends BaseSimplePlugin {

    @Override
    public
    void registeredWith(IVisualElement root) {
        super.registeredWith(root);

        DrawTopology.addTopologyAsSelectionAxis("<html><font face='gill sans'>Embedded GUI Element <b>References</b></font>",
                                                root,
                                                new ITopology<IVisualElement>() {

                                                    public
                                                    List<IVisualElement> getChildrenOf(IVisualElement source) {

                                                        List<IVisualElement> r = new ArrayList<IVisualElement>();

                                                        Map<Object, Object> allProperties = source.payload();
                                                        for (Entry<Object, Object> o : new HashMap<Object, Object>(allProperties)
                                                                                               .entrySet()) {
                                                            if (o.getKey() instanceof VisualElementProperty) {
                                                                if (((VisualElementProperty) o.getKey()).getName()
                                                                                                        .startsWith("__minimalReference")) {
                                                                    if (o.getValue() instanceof List) {
                                                                        List<IVisualElement> connectedTo =
                                                                                (List<IVisualElement>) o.getValue();
                                                                        if (connectedTo != null) {
                                                                            r.addAll(connectedTo);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        return r;
                                                    }

                                                    public
                                                    List<IVisualElement> getParentsOf(IVisualElement of) {
                                                        return Collections.EMPTY_LIST;
                                                    }
                                                },
                                                new DefaultDrawer(),
                                                "referencing",
                                                "referend",
                                                "r1",
                                                "r2");

    }

    @Override
    protected
    String getPluginNameImpl() {
        return "referencePlugin2";
    }

}
