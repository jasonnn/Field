package asm.handlers.ctx;

import field.protect.asm.ASMType;
import field.util.collect.MapOfMaps;
import org.objectweb.asm.ClassVisitor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jason on 7/21/14.
 */
public
class ASMClassCtx {
    public int access;
    public String name;
    public String signature;
    public String superName;
    public String[] interfaces;
    public ClassVisitor cv;
    public MapOfMaps<String, String, Object> annotations;
    public List<ASMMethodCtx> collectedMethods = new ArrayList<ASMMethodCtx>(4);

    ASMClassCtx(MapOfMaps<String, String, Object> annotations) {
        this.annotations = annotations;
    }

    ASMClassCtx() {
        this(MapOfMaps.<String, String, Object>create());
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
    ASMMethodCtx newMethod() {
        ASMMethodCtx newMethod = new ASMMethodCtx(this);
        // collectedMethods.add(newMethod);
        return newMethod;
    }

    public
    ASMClassCtx set(int access, String name, String signature, String superName, String[] interfaces) {
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
        return this;
    }

    void copyTo(ASMClassCtx other) {
        copy(this, other);
    }

    void copyFrom(ASMClassCtx other) {
        copy(other, this);
    }


    static
    void copy(ASMClassCtx from, ASMClassCtx to) {
        to.access = from.access;
        to.name = from.name;
        to.signature = from.signature;
        to.superName = from.superName;
        to.interfaces = from.interfaces == null ? null : Arrays.copyOf(from.interfaces, from.interfaces.length);
        to.cv = from.cv;
        to.annotations = from.annotations.copy();
    }

    public
    ASMMethodCtx newMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new ASMMethodCtx(this).set(access, name, desc, signature, exceptions);

    }

    public
    void keepMethod(ASMMethodCtx asmMethodCtx) {
        collectedMethods.add(asmMethodCtx);
    }

//    public
//    void callAfterClass(VisitEndCallback cb) {
//        if (cv != null && cv instanceof MainVisitorThing) {
//            ((MainVisitorThing) cv).callBeforeEnd(cb);
//        }
//        else {
//            Logger.getLogger(ASMClassCtx.class.getName()).warning("callback not added: cb=" + cb + ", cv=" + cv);
//        }
//    }
}
