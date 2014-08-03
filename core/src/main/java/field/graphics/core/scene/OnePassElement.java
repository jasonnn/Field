package field.graphics.core.scene;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.ConstantContext;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.annotations.HiddenInAutocomplete;
import field.bytecode.protect.dispatch.Cont;
import field.graphics.core.Base;
import field.graphics.core.BasicContextManager;
import field.graphics.core.ContextualUniform;
import field.graphics.core.pass.IPass;
import field.math.graph.IMutable;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 */ // abstract classes that are very useful
@HiddenInAutocomplete
@Woven
public abstract
class OnePassElement extends BasicSceneList implements ISceneListElement {

    static public final Method method_performPass = ReflectionTools.methodOf("performPass", OnePassElement.class);

    protected Set renderPass = new HashSet();

    protected IPass requestPass;

    protected Object gl = null;

    protected Object glu = null;

    public
    OnePassElement(IPass requestPass) {
        this.requestPass = requestPass;
    }

    @Override
    public
    void notifyAddParent(IMutable<ISceneListElement> newParent) {
        super.notifyAddParent(newParent);
        renderPass.add(((ISceneListElement) newParent).requestPass(requestPass));
    }

    // this is where you do the work of the element
    @DispatchOverTopology(topology = Cont.class)
    abstract public
    void performPass();

    // performs pass if renderPass or null pass
    @Override
    @DispatchOverTopology(topology = Cont.class)
    @ConstantContext(immediate = false, topology = Base.class)
    public
    void performPass(IPass p) {
        gl = BasicContextManager.getGl();
        glu = BasicContextManager.getGlu();
        if ((p == null) || (renderPass.contains(p))) {
            uniform.push();
            performPass();
        }
    }

    ContextualUniform.TagGroup uniform = new ContextualUniform.TagGroup();

    public
    void setTag(String key, String value) {
        uniform.put(key, value);
    }

}
