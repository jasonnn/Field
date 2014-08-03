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

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.glGetError;

/**
 * Created by jason on 8/2/14.
 */
@Woven
abstract public
class TwoPassElement extends BasicSceneList implements ISceneListElement {

    public Object gl = null;

    public Object glu = null;

    Set preRender = new HashSet();

    Set postRender = new HashSet();

    StandardPass prePass;

    StandardPass postPass;

    Object first = new Object();

    public
    TwoPassElement(String name, StandardPass prePass, StandardPass postPass) {
        this.prePass = prePass;
        this.postPass = postPass;
    }

    @Override
    public
    void notifyAddParent(IMutable<ISceneListElement> newParent) {
        super.notifyAddParent(newParent);

        preRender.add(((ISceneListElement) newParent).requestPass(prePass));
        postRender.add(((ISceneListElement) newParent).requestPass(postPass));
    }

    // a two pass calls pre() on preRender and
    // post() on postRender
    @Override
    @DispatchOverTopology(topology = Cont.class)
    @ConstantContext(immediate = false, topology = Base.class)
    public
    void performPass(IPass p) {
        gl = BasicContextManager.getGl();
        glu = BasicContextManager.getGlu();

        // System.err.println("Tp "+p+"
        // "+System.identityHashCode(this)+"
        // "+this.getClass()+" in
        // pre<"+preRender+">
        // post<"+postRender+">");

        assert (glGetError() == 0);
        if ((p == null) || (preRender.contains(p))) {
            if (!BasicContextManager.isValid(first)) {
                BasicContextManager.markAsValidInThisContext(first);
                assert (glGetError() == 0);
                setup();
                assert (glGetError() == 0);
            }
            assert (glGetError() == 0);

            // System.err.println("
            // update to and
            // including
            // <"+p+">"+this);
            super.updateUpToAndIncluding(p);
            // System.err.println("
            // x1"+this);

            pre();
            uniform.push();
            // System.err.println("
            // y1"+this);
            assert (glGetError() == 0) : this.getClass();
        }
        else if ((p == null) || (postRender.contains(p))) {
            assert (glGetError() == 0);

            // System.err.println("
            // update from
            // and not
            // including
            // <"+(iPass)preRender.iterator().next()+"
            // -> "+p+">");
            super.updateFromButNotIncluding((IPass) preRender.iterator().next(), p);

            post();
            uniform.pop();

            // System.err.println("
            // update from
            // including
            // "+p+">");
            super.updateFromButNotIncluding(p);
            assert (glGetError() == 0);
        }
        // assert (glGetError() ==
        // 0);
        // super.performPass(p);
        // assert (glGetError() ==
        // 0);
        // System.err.println("Tp "+p+"
        // "+System.identityHashCode(this)+"
        // "+this.getClass()+" out
        // pre<"+preRender+">
        // post<"+postRender+">");

    }

    @ConstantContext(immediate = false, topology = Base.class)
    abstract protected
    void post();

    @ConstantContext(immediate = false, topology = Base.class)
    abstract protected
    void pre();

    @ConstantContext(immediate = false, topology = Base.class)
    abstract protected
    void setup();

    ContextualUniform.TagGroup uniform = new ContextualUniform.TagGroup();

    public
    void setTag(String key, String value) {
        uniform.put(key, value);
    }

}
