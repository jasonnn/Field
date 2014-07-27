package field;

import field.bytecode.protect.Woven;

/**
 * Created by jason on 7/23/14.
 */
public
class TstCls {
    @Woven
    public
    void myMethod() {
        System.out.println("TstCls.myMethod");
    }

    static
    void before() {
        System.out.println("TstCls.before");
    }

    static
    void after() {
        System.out.println("TstCls.after");
    }
}
