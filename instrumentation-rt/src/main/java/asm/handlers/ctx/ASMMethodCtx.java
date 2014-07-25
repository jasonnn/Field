package asm.handlers.ctx;

import java.util.Arrays;

/**
 * Created by jason on 7/21/14.
 */
public
class ASMMethodCtx {
    public final ASMClassCtx classCtx = new ASMClassCtx();
    public int access;
    public String name;
    public String desc;
    public String signature;
    public String[] exceptions;


    public
    ASMMethodCtx copy() {
        return copyTo(new ASMMethodCtx());
    }

    public
    ASMMethodCtx copyTo(ASMMethodCtx copy) {

        copy.classCtx.copyFrom(this.classCtx);
        copy.access = access;
        copy.name = name;
        copy.desc = desc;
        copy.signature = signature;
        copy.exceptions = exceptions == null ? null : Arrays.copyOf(exceptions, exceptions.length);
        return copy;
    }
}
