package annotations;

import asm.handlers.dispatch.DispatchProvider;
import asm.handlers.dispatch.impl.Cont;
import field.bytecode.protect.annotations.ConstantContext;


/**
 * Created by jason on 7/22/14.
 */
public
class MyConstantContext {

    static final
    DispatchProvider context = new Cont();

    @ConstantContext(immediate = false, topology =MyConstantContext.class)
    public
    void performPass() {
        System.out.println("MyConstantContext.performPass");
    }
}
