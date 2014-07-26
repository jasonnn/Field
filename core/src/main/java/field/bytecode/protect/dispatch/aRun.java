package field.bytecode.protect.dispatch;

/**
 * Created by jason on 7/14/14.
 */
public
class aRun implements Run {

    public
    ReturnCode head(Object calledOn, Object[] args) {
        return ReturnCode.CONTINUE;
    }

    public
    ReturnCode tail(Object calledOn, Object[] args, Object returnWas) {
        return ReturnCode.CONTINUE;
    }
}
