package field.bytecode.protect;

import field.namespace.context.ContextTopology;

/**
* Created by jason on 7/15/14.
*/
public class ContextFor {
    boolean constant = true;

    Object contextOnCurrentEntry;

    Object contextTarget;

    int entryCount = 0;

    boolean immediate = true;

    ContextTopology on = null;

    boolean resets = true;

    public void enter() {

        if (entryCount == 0) contextOnCurrentEntry = on.getAt();
        if ((entryCount == 0) && (contextTarget != null)) on.setAt(contextTarget);
        entryCount++;
    }

    public void exit() {
        entryCount--;
        if (entryCount == 0) if (immediate || (on.getAt() != contextOnCurrentEntry)) if ((contextTarget == null)
                                                                                         || !constant) contextTarget = on.getAt();
        if (resets && (entryCount == 0)) on.setAt(contextOnCurrentEntry);

        assert entryCount >= 0;
    }
}
