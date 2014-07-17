package field.bytecode.protect.asm;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.InheritWeave;

/**
 * Created by jason on 7/16/14.
 */
public class CommonTypes {
    public static ASMType WOVEN = ASMType.getType(Woven.class);
    public static ASMType INHERIT_WEAVE = ASMType.getType(InheritWeave.class);
}
