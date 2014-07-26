package annotations;

import asm.handlers.dispatch.impl.Cont;
import field.bytecode.protect.annotations.DispatchOverTopology;

/**
 * Created by jason on 7/22/14.
 */
public
class MyDispatchOverTopology {
    int tick = 0;

    @DispatchOverTopology(topology = Cont.class)
    public
    void update() {
        tick++;
    }
}
