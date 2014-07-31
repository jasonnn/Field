package field.apt.gen

import field.apt.CodeGen
import field.apt.util.CollectImportsScanner
import field.apt.util.GenUtils
import groovy.transform.InheritConstructors
import javabuilder.JavaBuilder

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

import static field.apt.gen.MirrorKind.*

/**
 * Created by jason on 7/11/14.
 */
@InheritConstructors
class GCodeGenerator extends GeneratorBase implements CodeGen {

    GCodeGenerator(ProcessingEnvironment env, TypeElement element) {
        super(env, element)
        wrapperTypesGen = new WrapperTypesGen(env, element)
    }
    WrapperTypesGen wrapperTypesGen

    @Override
    void setJavaBuilder(JavaBuilder jb) {
        super.setJavaBuilder(jb)
        wrapperTypesGen.setJavaBuilder(jb)
        wrapperTypesGen.setJavaWriter(jb.javaWriter)
        assert javaBuilder != null
        assert wrapperTypesGen != null
        assert wrapperTypesGen.javaBuilder != null
        assert wrapperTypesGen.javaWriter != null
    }


    @Override
    void generate() throws IOException {

        compilationUnit {
            packageName = GenUtils.packageNameOf(element)
            imports.add(makeImports())
            createClass(name: generatedSimpleName,
                        modifiers: [Modifier.PUBLIC]) {
                def steps = [this.&genAccessField,
                             this.&genMirrorField,
                             this.wrapperTypesGen.&generate,
                             this.&genInstanceField]

                for (step in steps) {
                    methods.each(step)
                    hr()
                }

                emitEmptyLine()

                constructor(modifiers: [Modifier.PUBLIC],
                            params: [x: element.simpleName])
                        {
                            for (me in methods) {
                                stmnt "${me.generatedName}=new ${me.generatedName}_impl(x)"
                            }
                        }
            }

        }
    }

    static def extraImports = ['field.bytecode.mirror.impl.*',
                               'field.launch.IUpdateable',
                               'field.math.abstraction.IAcceptor',
                               'field.math.abstraction.IProvider',
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
        field([name     : me.generatedName,
               type     : me.generatedName + '_interface',
               modifiers: [Modifier.PUBLIC, Modifier.FINAL]])
    }

    def reflectUtilCall(MethodElement me) {
        def paramStr = ''
        if (me.hasParams()) {
            paramStr = ', ' + me.paramClassesCSV()
        }
        return "ReflectionTools.methodOf(\"$me.simpleName\",${element.qualifiedName}.class$paramStr)".toString()
    }

    def genAccessField(MethodElement me) {
        field([name       : me.generatedName + '_m',
               type       : 'java.lang.reflect.Method',
               modifiers  : [Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL],
               initializer: reflectUtilCall(me)]);
    }


//    String typeParams(MethodElement me) {
//        switch (me.mirrorKind) {
//            case MirrorMethod:
//                return '<' + me.parent.toString() + ', ' + boxedClassName(me.returnType) + ', ' + acceptorGenericArg(me) + '>'
//            case MirrorNoArgsMethod:
//                return '<' + me.parent.toString() + ', ' + boxedClassName(me.returnType) + '>'
//            case MirrorNoReturnMethod:
//                return '<' + me.parent.toString() + ', ' + acceptorGenericArg(me) + '>'
//            case MirrorNoReturnNoArgsMethod:
//                return '<' + me.parent.toString() + '>'
//            default:
//                throw new IllegalArgumentException(me.toString())
//        }
//    }

    String typeParams2(MethodElement me) {
        def owner = me.parent.toString()
        def I = acceptorGenericArg(me)
        def O = me.boxedReturnType
        "<$owner,$I,$O>"
    }


    def genMirrorField(MethodElement me) {

        def parameterizedPart = typeParams2(me)
        def mirrorType = 'MirrorMethod' + parameterizedPart

        field([name       : me.generatedName + '_s',
               type       : mirrorType,
               initializer: "new $mirrorType(${me.generatedName}_m)",
               modifiers  : [Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL]])
    }

}
