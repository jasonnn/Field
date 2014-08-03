package field.graphics.core.pass;

/**
 * these are the standard passes inside the renderer: do stuff that can be done before transform transform stuff into world space. do stuff that depends on world space (e.g. skinning) put it on the screen
 */

public
class LocalPass implements IPass {
    protected float value;

    public
    LocalPass(float value) {
        this.value = value;
    }

    public
    float getValue() {
        return value;
    }

    public
    boolean isLaterThan(IPass p) {
        return p.getValue() < value;
    }

    public
    boolean isEarlierThan(IPass p) {
        return p.getValue() > value;
    }
}
