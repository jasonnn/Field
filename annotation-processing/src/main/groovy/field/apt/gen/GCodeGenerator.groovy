package field.apt.gen

import field.apt.CodeGen
import field.apt.util.CollectImportsScanner
import field.apt.util.GenUtils
import field.apt.util.TypeNameVisitor
import groovy.transform.InheritConstructors
import javabuilder.JavaBuilder
import javabuilder.writer.JavaWriterEx

import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

import static field.apt.gen.GCodeGenerator.MirrorKind.*

/**
 * Created by jason on 7/11/14.
 */
@InheritConstructors
class GCodeGenerator extends GeneratorBase implements CodeGen {

    @Delegate
    JavaBuilder javaBuilder
    JavaWriterEx javaWriter

    static final String VISUAL_ELEMENT = 'field.core.dispatch.IVisualElement';
    static final String TRAVERSAL_HINT = 'field.math.graph.visitors.hint.TraversalHint'
//TODO this doesnt handle methods with arguments?

    def emitEmptyLine() {
        javaWriter.emitEmptyLine()
    }

    @Override
    void generate() throws IOException {

        compilationUnit {
            packageName = GenUtils.packageNameOf(element)
            imports.add(makeImports())
            createClass(name: GenUtils.generatedSimpleName(element),
                        modifiers: [Modifier.PUBLIC]) {
                def steps = [this.&genAccessField,
                             this.&genMirror,
                             this.&genIFace,
                             this.&genImpl,
                             this.&genInstanceField]

                for (step in steps) {
                    methods.each(step)
                    emitEmptyLine()
                }

                emitEmptyLine()

                constructor(modifiers: [Modifier.PUBLIC],
                            params: [x: element.simpleName])
                        {
                            for (me in methods) {
                                stmnt "${me.simpleName}=new ${me.simpleName}_impl(x)"
                            }
                        }
            }

        }
    }

    static def extraImports = ['field.bytecode.mirror.impl.*',
                               'field.launch.IUpdateable',
                               'field.math.abstraction.IAcceptor',
                               'field.namespace.generic.IFunction',
                               'field.namespace.generic.ReflectionTools',
                               'java.lang.reflect.Method'
    ]

    def makeImports() {
        def imports = CollectImportsScanner.getVisibleDependencies(element)
        def name = element.qualifiedName.toString()
        imports.removeAll { it ==~ /java.lang.\w+/ || it == name }
        imports.addAll(extraImports)
        return imports
    }


    def genInstanceField(MethodElement me) {
        field([name     : me.simpleName,
               type     : me.simpleName + '_interface',
               modifiers: PF])
    }

    def reflectUtilCall(MethodElement me) {
        def paramStr = ''
        if (!me.parameters.isEmpty()) {
            paramStr = ', ' + me.paramClassesCSV()
        }
        return "ReflectionUtils.methodOf(\"$me.simpleName\",${element.qualifiedName}.class$paramStr)".toString()
    }

    def genAccessField(MethodElement me) {
        field([name       : me.reflectionFieldName + '_m',
               type       : 'java.lang.reflect.Method',
               modifiers  : PSF,
               initializer: reflectUtilCall(me)]);
    }

    static enum MirrorKind {
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

    String typeParams(MethodElement me, MirrorKind kind) {
        switch (kind) {
            case MirrorMethod:
                return '<' + me.enclosingElement.toString() + ', ' + name(me.returnType) + ', ' + genArgs(me) + '>'
            case MirrorNoArgsMethod:
                return '<' + me.enclosingElement.toString() + ', ' + name(me.returnType) + '>'
            case MirrorNoReturnMethod:
                return '<' + me.enclosingElement.toString() + ', ' + genArgs(me) + '>'
            case MirrorNoReturnNoArgsMethod:
                return '<' + me.enclosingElement.toString() + '>'
        }
    }

    def genArgs(MethodElement me) {
        def nParams = me.parameters.size();
        if (nParams == 1) {
            def param = me.parameters[0].asType()
            return name(param)
        } else {
            return 'Object[]'
        }
    }

    def name(TypeMirror mirror) {
        return TypeNameVisitor.INSTANCE.visit(mirror, env.typeUtils)
    }

    def genMirror(MethodElement me) {
        def mirrorKind = forMethod(me)

        def typeParams = typeParams(me, mirrorKind)
        def type = mirrorKind.name() + typeParams

        field([name       : me.reflectionFieldName + '_s',
               type       : type,
               initializer: "new $type(${me.reflectionFieldName}_m)",
               modifiers  : PSF])
    }

    def genIFace(MethodElement me) {
        def ext = ["IAcceptor<${element.qualifiedName}>",
                   "IFunction<$TRAVERSAL_HINT,${element.qualifiedName}>"]

        createInterface([name      : me.simpleName + '_interface',
                         modifiers : PS,
                         implements: ext]) {

            method([name      : me.simpleName,
                    returnType: TRAVERSAL_HINT,
                    params    : [p0: VISUAL_ELEMENT]])
            method([name      : 'updateable',
                    returnType: 'IUpdateable',
                    params    : [p0: VISUAL_ELEMENT]])
            method([name      : 'bind',
                    returnType: "IProvider<$TRAVERSAL_HINT>",
                    params    : [p0: VISUAL_ELEMENT]])

        }
    }

    static final Set<Modifier> PS = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
    static final Set<Modifier> PSF = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    static final Set<Modifier> PF = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    static final Set<Modifier> F = EnumSet.of(Modifier.FINAL)

    def genImpl(MethodElement me) {

        createClass([name      : me.simpleName + '_impl',
                     modifiers : [Modifier.STATIC],
                     implements: [me.simpleName + '_interface']]) {
            field([name     : 'x',
                   type     : element.simpleName,
                   modifiers: F])
            field([name     : 'a',
                   type     : 'IAcceptor',
                   modifiers: F])
            field([name     : 'f',
                   type     : 'IFunction',
                   modifiers: F])
            constructor(params: [x: element.simpleName]) {
                stmnt 'this.x=x'
                stmnt "this.a=${me.simpleName}_s.acceptor(x)"
                stmnt "this.f=${me.simpleName}_s.function(x)"
            }

            method([name       : me.simpleName,
                    returnType : TRAVERSAL_HINT,
                    params     : [p0: VISUAL_ELEMENT],
                    modifiers  : [Modifier.PUBLIC],
                    annotations: [Override]]) {
                "return x.${me.simpleName}(p0)"
            }

            method([name       : 'set',
                    returnType : 'IAcceptor<field.core.dispatch.IVisualElement>',
                    params     : [p: VISUAL_ELEMENT],
                    modifiers  : [Modifier.PUBLIC],
                    annotations: [Override]]) {
                stmnt 'a.set(p)'
                stmnt 'return this'
            }
            method([name       : 'apply',
                    returnType : TRAVERSAL_HINT,
                    params     : [p: VISUAL_ELEMENT],
                    modifiers  : [Modifier.PUBLIC],
                    annotations: [Override]]) {
                'return (field.math.graph.visitors.hint.TraversalHint) f.apply(p)'
            }
            method([name       : 'updateable',
                    returnType : 'IUpdateable',
                    params     : [p: VISUAL_ELEMENT],
                    modifiers  : [Modifier.PUBLIC],
                    annotations: [Override]]) {
                """ |return new IUpdateable(){
                    |   public void update(){
                    |       ${me.simpleName}(p);
                    |   }
                    |}""".stripMargin()
            }
            method([name       : 'bind',
                    returnType : 'IProvider<field.math.graph.visitors.hint.TraversalHint>',
                    params     : [p: VISUAL_ELEMENT],
                    modifiers  : [Modifier.PUBLIC],
                    annotations: [Override]]) {
                """ |return new IProvider(){
                    |    public Object get(){
                    |        return added(p0);
                    |        }
                    |}""".stripMargin()
            }


        }

    }


}
