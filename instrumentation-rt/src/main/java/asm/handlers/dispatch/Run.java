package asm.handlers.dispatch;

/**
 * Created by jason on 7/14/14.
 */
public
interface Run {
    public
    ReturnCode head(Object calledOn, Object[] args);

    public
    ReturnCode tail(Object calledOn, Object[] args, Object returnWas);
}
