package field.core.plugins.history;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElementProperty;
import field.core.plugins.history.HistoryExplorerHG.VersionNode;
import field.core.util.FieldPyObjectAdaptor.iHandlesAttributes;
import field.core.util.FieldPyObjectAdaptor2;
import field.util.collect.tuple.Pair;
import org.python.core.Py;
import org.python.core.PyObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public
class AttributeHistory {

    static {
        FieldPyObjectAdaptor2.isHandlesAttributes(AttributeHistoryAccess.class);
    }

    public static
    class AttributeHistoryAccess implements iHandlesAttributes {
        private final IVisualElement e;

        private final HistoryExplorerHG hg;

        HashMap<String, WeakReference<AttributeHistory>> cache = new HashMap<String, WeakReference<AttributeHistory>>();

        public
        AttributeHistoryAccess(HistoryExplorerHG hg, IVisualElement e) {
            this.hg = hg;
            this.e = e;
        }

        public
        Object getAttribute(String name) {
            VisualElementProperty p = new VisualElementProperty(name);
            if (!p.containsSuffix("v")) return null;

            WeakReference<AttributeHistory> n = cache.get(name);
            AttributeHistory ah = n == null ? null : n.get();
            if (ah == null) {
                ah = new AttributeHistory(hg, e, p);
                cache.put(name, new WeakReference<AttributeHistory>(ah));
            }
            return ah.new AttributeHistoryPy();
        }

        public
        void setAttribute(String name, Object value) {
        }

    }

    public
    class AttributeHistoryPy extends PyObject {
        @Override
        public
        PyObject __finditem__(PyObject key) {

            if (key.isNumberType()) {
                Number n = (Number) key.__tojava__(Number.class);
                Pair<VersionNode, Object> element = values.get(n.intValue());
                return Py.java2py(element);
            }
            else return null;
        }

        @Override
        public
        int __len__() {
            return values.size();
        }
    }

    public static final VisualElementProperty<AttributeHistoryAccess> history =
            new VisualElementProperty<AttributeHistoryAccess>("history");

    List<Pair<VersionNode, Object>> values = new ArrayList<Pair<VersionNode, Object>>();


    public
    AttributeHistory(HistoryExplorerHG ex, IVisualElement e, VisualElementProperty propertyName) {
        Set<VersionNode> versions = HistoryExplorerHG.buildHistoryGraph(ex.getSheetPrefix()
                                                                        + e.getUniqueID()
                                                                        + '/'
                                                                        + propertyName.getName()
                                                                        + ".property");
        for (VersionNode n : versions) {
            Object versionProperty =
                    ex.getVersionProperty(n.revision, ex.getSheetPrefix() + e.getUniqueID() + '/', propertyName);
            values.add(new Pair<VersionNode, Object>(n, versionProperty));
        }
    }

}
