package annotations;

import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.dispatch.Cont;

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
