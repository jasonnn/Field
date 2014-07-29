package field.core.plugins;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;
import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.dispatch.IVisualElementOverrides.DefaultOverride;
import field.core.plugins.python.PythonPluginEditor;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.List;

@Woven
public
class BindingPlugin extends BaseSimplePlugin {

    @Override
    protected
    String getPluginNameImpl() {
        return "binding";
    }

    @Override
    public
    void registeredWith(IVisualElement root) {
        super.registeredWith(root);
    }

    boolean first = true;

    @Override
    public
    void update() {
        super.update();
        if (first) {
            first = false;
            addAll();

        }
    }

    private
    void addAll() {
        List<IVisualElement> all = StandardFluidSheet.allVisualElements(root);
        for (IVisualElement e : all) {
            added(e);
        }
    }

    @NextUpdate(delay = 1)
    protected
    void addAllNext() {
        List<IVisualElement> all = StandardFluidSheet.allVisualElements(root);
        for (IVisualElement e : all) {
            added(e);
        }
    }

    protected static
    void added(IVisualElement e) {
        String b = e.getProperty(IVisualElement.boundTo);
        if ((b != null) && !b.trim().isEmpty()) {

            //System.out.println(" initializing boundto with <" + b + "> for <" + e + ">");

            PythonPluginEditor.makeBoxLocalEverywhere(b.trim());
            List<IVisualElement> c = e.getChildren();
            for (IVisualElement ee : c)
                ee.setProperty(new VisualElementProperty<IVisualElement>(b.trim() + '_'), e);
            e.setProperty(new VisualElementProperty<IVisualElement>(b.trim() + '_'), e);
        }
    }

    @Override
    protected
    DefaultOverride newVisualElementOverrides() {
        return new DefaultOverride() {
            @Override
            public
            <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
                if (prop.equals(IVisualElement.boundTo)) {
                    String was = source.getProperty(IVisualElement.boundTo);

                    if ((was == null) || was.trim().isEmpty()) {
                        if ((to.get() == null) || ((String) to.get()).trim().isEmpty()) {
                        }
                        else {
                            PythonPluginEditor.makeBoxLocalEverywhere(((String) to.get()).trim());
                            List<IVisualElement> c = source.getChildren();
                            for (IVisualElement ee : c)
                                ee.setProperty(new VisualElementProperty<IVisualElement>(((String) to.get()).trim()
                                                                                         + '_'), source);
                            source.setProperty(new VisualElementProperty<IVisualElement>(((String) to.get()).trim()
                                                                                         + '_'), source);
                        }
                    }
                    else {
                        if ((to.get() == null) || ((String) to.get()).trim().isEmpty()) {
                            PythonPluginEditor.removeBoxLocalEverywhere(was.trim());
                            List<IVisualElement> c = source.getChildren();
                            for (IVisualElement ee : c)
                                ee.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));
                            source.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));

                            addAllNext();

                        }
                        else {
                            PythonPluginEditor.removeBoxLocalEverywhere(was.trim());
                            List<IVisualElement> c = source.getChildren();
                            for (IVisualElement ee : c)
                                ee.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));
                            source.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));

                            PythonPluginEditor.makeBoxLocalEverywhere(((String) to.get()).trim());
                            for (IVisualElement ee : c)
                                ee.setProperty(new VisualElementProperty<IVisualElement>(((String) to.get()).trim()
                                                                                         + '_'), source);
                            source.setProperty(new VisualElementProperty<IVisualElement>(((String) to.get()).trim()
                                                                                         + '_'), source);

                        }

                    }
                }
                return super.setProperty(source, prop, to);
            }

            @Override
            public
            TraversalHint deleted(IVisualElement source) {

                String was = source.getProperty(IVisualElement.boundTo);
                //System.out.println(" handling deleted for <" + source + " -> " + was);

                if ((was == null) || was.trim().isEmpty()) {
                }
                else {

                    //System.out.println(" children are <" + source.getChildren() + ">");
                    PythonPluginEditor.removeBoxLocalEverywhere(was.trim());
                    List<IVisualElement> c = source.getChildren();
                    for (IVisualElement ee : c)
                        ee.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));
                    source.deleteProperty(new VisualElementProperty<IVisualElement>(was + '_'));
                }

                addAllNext();

                return super.deleted(source);
            }

            @Override
            public
            TraversalHint added(IVisualElement source) {
                BindingPlugin.added(source);
                return super.added(source);
            }

        };
    }

}
