package field.graphics.core.pass;

/**
 * Created by jason on 8/2/14.
 */
public
enum StandardPass implements IPass {
    preTransform(-1), transform(0), postTransform(1), preRender(2), render(3), postRender(4), preDisplay(5);

    private final int value;

    StandardPass(int value) {
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
