package field.bytecode.protect.trampoline;

import field.protect.asm.ASMType;

import java.lang.annotation.Annotation;

/**
 * Created by jason on 7/20/14.
 */
public
interface InheritWovenHelper {
    ClassLoader getLoader();

    Annotation[] getAllAnotationsForSuperMethodsOf(String name,
                                                   String desc,
                                                   ASMType[] at,
                                                   String super_name,
                                                   String[] interfaces);

    boolean shouldLoadLocal(String name);
}
