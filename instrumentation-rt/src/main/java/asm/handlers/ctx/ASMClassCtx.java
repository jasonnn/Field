package asm.handlers.ctx;

import field.protect.asm.ASMType;
import field.util.MapOfMaps;
import org.objectweb.asm.ClassVisitor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

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


    public
    Map<String, Object> annotation(Class<? extends Annotation> ann) {
        return annotation(ASMType.getDescriptor(ann));
    }

    public
    Map<String, Object> annotation(String desc) {
        return getAnnotations().get(desc);
    }

    public
    MapOfMaps<String, String, Object> getAnnotations() {
        MapOfMaps<String, String, Object> ann = annotations;
        if (ann == null) ann = annotations = MapOfMaps.create();
        return ann;
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
