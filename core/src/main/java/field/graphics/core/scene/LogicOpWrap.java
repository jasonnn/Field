package field.graphics.core.scene;

import field.graphics.core.pass.StandardPass;

/**
 * Created by jason on 8/2/14.
 */
public
class LogicOpWrap extends TwoPassElement {

    private final int op;

    boolean enable = false;

    public
    LogicOpWrap(int op) {
        super("standard", StandardPass.preTransform, StandardPass.preDisplay);
        this.op = op;
    }

    @Override
    protected
    void post() {
        // glDisable(GL_LOGIC_OP);
        // System.err.println(" logic op
        // off ");
    }

    @Override
    protected
    void pre() {
        // System.err.println(" logic op
        // on ");
        // glEnable(GL_LOGIC_OP);
        // glLogicOp(op);
    }

    @Override
    protected
    void setup() {
    }

}
