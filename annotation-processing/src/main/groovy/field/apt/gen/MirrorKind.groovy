package field.apt.gen

import javax.lang.model.type.TypeKind

enum MirrorKind {
    MirrorMethod,
    MirrorNoArgsMethod,
    MirrorNoReturnMethod,
    MirrorNoReturnNoArgsMethod

    public static MirrorKind forMethod(MethodElement me) {
        def noRet = me.returnType.kind in [TypeKind.VOID, TypeKind.NONE]
        def noArgs = me.parameters.isEmpty()
        return noRet ? (noArgs ? MirrorNoReturnNoArgsMethod
                               : MirrorNoReturnMethod)
                     : (noArgs ? MirrorNoArgsMethod
                               : MirrorMethod)
    }

}