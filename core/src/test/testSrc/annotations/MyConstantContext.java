package annotations;

import field.bytecode.protect.annotations.ConstantContext;
import field.graphics.core.Base;
import field.graphics.core.BasicContextManager;

/**
 * Created by jason on 7/22/14.
 */
public
class MyConstantContext {

    @ConstantContext(immediate = false, topology = Base.class)
    public
    void performPass() {
        int id = BasicContextManager.getId(this);
        if ((id == BasicContextManager.ID_NOT_FOUND) || (!BasicContextManager.isValid(this))) {
            System.out.println("1st~!");

        }
    }
}
