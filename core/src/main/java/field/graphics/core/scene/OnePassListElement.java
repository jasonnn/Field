package field.graphics.core.scene;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.ConstantContext;
import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.dispatch.Cont;
import field.graphics.core.Base;
import field.graphics.core.BasicContextManager;
import field.graphics.core.ContextualUniform;
import field.graphics.core.pass.IPass;
import field.graphics.core.pass.StandardPass;
import field.math.graph.IMutable;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.glGetError;

/**
 * Created by jason on 8/2/14.
 */
@Woven
abstract public
class OnePassListElement extends BasicSceneList implements ISceneListElement {

    static public final Method method_performPass = ReflectionTools.methodOf("performPass", OnePassListElement.class);

    public Object gl = null;

    public Object glu = null;

    final StandardPass ourPass;

    protected StandardPass requestPass;

    protected Set renderPass = new HashSet();

    protected IPass ourRenderPass;

    protected boolean preCalled = false;

    protected boolean postCalled = false;

    boolean skipIfEmpty = false;

    IFunction<OnePassListElement, Boolean> guard;

    public
    OnePassListElement(StandardPass parentPass, StandardPass ourPass) {
        this.ourPass = ourPass;
        this.ourRenderPass = this.requestPass(ourPass);
        this.requestPass = parentPass;
    }

    @Override
    public
    void notifyAddParent(IMutable<ISceneListElement> newParent) {
        super.notifyAddParent(newParent);
        renderPass.add(((ISceneListElement) newParent).requestPass(requestPass));
    }

    @DispatchOverTopology(topology = Cont.class)
    @ConstantContext(immediate = false, topology = Base.class)
    abstract public
    void performPass();

    boolean visible = true;

    @Override
    public
    void performPass(IPass p) {
        gl = BasicContextManager.getGl();
        glu = BasicContextManager.getGlu();

        if ((p == null) || (renderPass.contains(p))) {
            if (skipIfEmpty && this.getChildren().size() == 0)
                return;

            if (guard != null && !guard.apply(this)) {

                visible = false;
                return;
            }
            visible = true;

            preCalled = false;
            postCalled = false;
            assert (glGetError() == 0) : this.getClass();
            performPass();
            assert (glGetError() == 0) : this.getClass();
            assert preCalled : this.getClass();
            assert postCalled : this.getClass();
        }
        // System.err.println("pp "+p+"
        // "+System.identityHashCode(this)+"
        // "+this.getClass()+" out ");
    }

    public
    void setSkipIfEmpty() {
        skipIfEmpty = true;
    }

    protected
    void post() {
        postCalled = true;
        updateFromButNotIncluding(ourRenderPass);
    }

    /**
     * subclasses must call these, typically on entry and exit to
     * performPass ()
     */
    protected
    void pre() {
        preCalled = true;
        updateUpToAndIncluding(ourRenderPass);

        uniform.push();
    }

    public
    boolean isVisible() {
        return visible;
    }

    ContextualUniform.TagGroup uniform = new ContextualUniform.TagGroup();

    public
    void setTag(String key, String value) {
        uniform.put(key, value);
    }

    public
    void setGaurd(IFunction<OnePassListElement, Boolean> guard) {
        this.guard = guard;
    }
}
