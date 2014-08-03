package field.graphics.core.scene;

/**
 * Created by jason on 8/2/14.
 */
public
class Masquerade extends OnePassListElement {

    private final OnePassListElement copyFrom;

    private final OnePassListElement execute;

    public
    Masquerade(OnePassListElement copyFrom, OnePassListElement execute) {
        super(execute.requestPass, copyFrom.ourPass);
        this.copyFrom = copyFrom;
        this.execute = execute;
    }

    @Override
    public
    void performPass() {
        this.pre();
        copyFrom.pre();
        execute.performPass();
        copyFrom.post();
        this.post();
    }
}
