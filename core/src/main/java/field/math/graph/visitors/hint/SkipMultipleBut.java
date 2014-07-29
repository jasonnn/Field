package field.math.graph.visitors.hint;

import java.util.Collection;

/**
* Created by jason on 7/29/14.
*/
public
class SkipMultipleBut extends TraversalHintImpl {
    public Collection c;

    public Object o;

    public
    SkipMultipleBut(Collection c, Object o) {
        super("skip_multiple_but");
        this.c = c;
        this.o = o;
    }
}
