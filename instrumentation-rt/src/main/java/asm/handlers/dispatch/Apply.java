package asm.handlers.dispatch;

/**
 * Created by jason on 7/14/14.
 */
public
interface Apply {
    public
    void head(Object[] args);

    public
    Object tail(Object[] args, Object returnWas);
}
