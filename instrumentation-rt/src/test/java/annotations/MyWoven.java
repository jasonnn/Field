package annotations;

import field.bytecode.protect.Woven;

/**
 * Created by jason on 7/21/14.
 */
@Woven
public
class MyWoven {
    public
    MyWoven() {
    }

    @Woven
    public
    int wovenMethod(int i) {
        return i + 2;
    }
}
