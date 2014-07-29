package field.math.graph.visitors.hint;

/**
* Created by jason on 7/29/14.
*/
public
class TraversalHintImpl implements TraversalHint {

    private final String string;

    public
    TraversalHintImpl(String string) {
        this.string = string;
    }

    @Override
    public
    String toString() {
        return string;
    }

    @Override
    public String name(){
        return string;
    }
}
