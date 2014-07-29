package field.math.graph.visitors.hint;

import java.util.Collection;

/**
* Created by jason on 7/29/14.
*/
public
class SkipMultiple extends TraversalHintImpl {
    public Collection c;

    public
    SkipMultiple(Collection c) {
        super("skip_multiple");
        this.c = c;
    }
}
