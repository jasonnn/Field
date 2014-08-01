package field.core.dispatch;

import field.core.dispatch.override.DefaultOverride;
import field.core.plugins.python.PythonPlugin;
import field.core.ui.text.embedded.FreezeProperties;
import field.core.ui.text.embedded.FreezeProperties.Freeze;
import field.core.windowing.components.iComponent;
import field.util.collect.tuple.Triple;
import field.util.diff.Diff3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public
class MergeGroupFreezer extends MergeGroup {
    public static final VisualElementProperty<HashMap<IVisualElement, Freeze>> mergeGroup_initialParameters =
            new VisualElementProperty<HashMap<IVisualElement, Freeze>>("mergeGroup_initialParameters");

    protected HashSet<IVisualElement> newlyCreated = new HashSet<IVisualElement>();

    Set<String> include = new HashSet<String>();

    FreezeProperties freezer = new FreezeProperties();

    HashMap<IVisualElement, Freeze> frozen = new HashMap<IVisualElement, Freeze>();

    public
    MergeGroupFreezer() {
        super();
    }

    public
    MergeGroupFreezer(IVisualElement owner) {
        super(owner);
        HashMap<IVisualElement, Freeze> q = mergeGroup_initialParameters.get(owner);
        if (q == null) mergeGroup_initialParameters.set(owner, owner, frozen);
        else frozen = q;

        FreezeProperties.standardCloneHelpers(freezer);

        freezer.setInclude(include);

    }

    public
    void addInclude(String a) {
        include.add(a);
        freezer.setInclude(include);
    }

    @Override
    public
    void begin() {
        if (open == 0) {
            newlyCreated.clear();
        }
        super.begin();
    }

    @Override
    public
    void end() {
        super.end();
        if (open == 0) {
            for (IVisualElement v : newlyCreated) {
                frozen.put(v, freezer.new Freeze().freeze((VisualElement) v));
            }
        }
    }

    public
    void reset(IVisualElement a) {
        assert open > 0;
        newlyCreated.add(a);
        frozen.remove(a);
    }

    // these echo those in PythonPlugin, but can diff3 merge on set
    // it might be better to defer these until end() time
    public
    void setAttr(IVisualElement a, String name, Object value) {
        System.err.println(" setting property <" + a + "> <" + name + "> <" + value + '>');
        if (frozen.containsKey(a)) {
            name = PythonPlugin.externalPropertyNameToInternalName(name);
            Freeze f = frozen.get(a);
            VisualElementProperty vp = new VisualElementProperty(name);
            Object initValue = f.getMap().get(vp);
            Object currentValue = vp.get(a, a);

            if ((initValue != null) && (currentValue != null) && (value != null))
                if ((initValue instanceof String) && (currentValue instanceof String) && (value instanceof String)) {
                    Diff3 d = new Diff3((String) currentValue, (String) initValue, (String) value);
                    String c = d.getResult();
                    vp.set(a, a, c);

                    f.getMap().put(vp, value);

                    return;
                }

            vp.set(a, a, value);
        }
        else {
            PythonPlugin.setAttr(a, name, value);
        }
    }

    @Override
    protected
    <T extends VisualElement, S extends iComponent, U extends DefaultOverride> void newlyCreated(Triple<T, S, U> triple) {
        super.newlyCreated(triple);
        newlyCreated.add(triple.left);
    }

    @Override
    protected
    boolean shouldCull(VisualElement element, int hash, int oldHash) {

        boolean a = super.shouldCull(element, hash, oldHash);

        a = true;

        if (a) frozen.remove(element);


        return a;
    }

}
