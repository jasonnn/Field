package asm.handlers.ctx;

import field.protect.asm.ASMType;
import field.util.collect.MapOfMaps;
import org.objectweb.asm.MethodVisitor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by jason on 7/21/14.
 */
public
class ASMMethodCtx {
    public final ASMClassCtx classCtx;
    public int access;
    public String name;
    public String desc;
    public String signature;
    public String[] exceptions;
    public MapOfMaps<String, String, Object> annotations;
    public MethodVisitor mv;


    ASMMethodCtx(ASMClassCtx classCtx) {
        this.classCtx = classCtx;
    }

    public
    ASMMethodCtx() {
        this(new ASMClassCtx());
    }

    public
    Map<String, Object> annotation(Class<? extends Annotation> ann) {
        return annotation(ASMType.getDescriptor(ann));
    }

    public
    Map<String, Object> annotation(String desc) {
        return annotations.get(desc);
    }

    public
    ASMMethodCtx copy(boolean copyCls) {
        ASMMethodCtx copy = copyCls ? new ASMMethodCtx() : new ASMMethodCtx(classCtx);
        copy(this, copy, copyCls);
        return copy;
    }


    public
    void copyTo(ASMMethodCtx copy) {
        copyTo(copy, false);
    }

    public
    void copyTo(ASMMethodCtx copy, boolean copyCls) {
        copy(this, copy, copyCls);
    }

    static
    void copy(ASMMethodCtx from, ASMMethodCtx to, boolean copyCls) {
        if (copyCls) ASMClassCtx.copy(from.classCtx, to.classCtx);
        to.access = from.access;
        to.name = from.name;
        to.desc = from.desc;
        to.signature = from.signature;
        to.exceptions = from.exceptions == null ? null : Arrays.copyOf(from.exceptions, from.exceptions.length);
        to.annotations = from.annotations.copy();
    }
}
