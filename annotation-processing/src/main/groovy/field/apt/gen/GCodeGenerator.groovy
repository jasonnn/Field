package field.apt.gen

import field.apt.CodeGen
import field.apt.util.GenUtils
import groovy.transform.InheritConstructors
import javabuilder.JavaBuilder
import javabuilder.config.FieldSpec
import javabuilder.writer.JavaWriterEx

import javax.lang.model.element.Modifier

/**
 * Created by jason on 7/11/14.
 */
@InheritConstructors
class GCodeGenerator extends GeneratorBase implements CodeGen {

    JavaBuilder javaBuilder
    JavaWriterEx javaWriter

    static final String VISUAL_ELEMENT = 'field.core.dispatch.iVisualElement';
    static final String VISIT_CODE = 'field.math.graph.GraphNodeSearching.VisitCode'
//TODO this doesnt handle methods with arguments?

    @Override
    void generate() throws IOException {

        javaBuilder.compilationUnit {
            packageName = GenUtils.packageNameOf(element)
            imports.add([
                    'java.lang.reflect.*',
                    'java.util.*',
                    'field.bytecode.mirror.*',
                    'field.namespace.generic.Bind.*',
                    'field.math.abstraction.*',
                    'field.launch.*',
                    'field.namespace.generic.ReflectionTools'])
            createClass(name: GenUtils.generatedSimpleName(element),
                        modifiers: [Modifier.PUBLIC]) {

                for (meth in methods) {
                    javaWriter.emitEmptyLine()
                    genAccessField(meth)
                    genMirror(meth)
                    genIFace(meth)
                    genImpl(meth)
                }
                javaWriter.emitEmptyLine()
                methods.each(this.&genInstanceField)
                javaWriter.emitEmptyLine()

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


    def genInstanceField(MethodElement me) {
        javaWriter.emitField(new FieldSpec(
                name: me.simpleName,
                type: me.simpleName + '_interface',
                modifiers: PF))
    }

    def genAccessField(MethodElement me) {
        def paramStr = ''
        if (!me.parameters.isEmpty()) {
            paramStr = ', ' + me.paramClassesCSV()
        }
        javaWriter.emitField(new FieldSpec(
                name: me.reflectionFieldName + '_m',
                type: 'java.lang.reflect.Method',
                modifiers: PSF,
                initializer: "ReflectionUtils.methodOf(\"$me.simpleName\",${element.qualifiedName}.class$paramStr)"))
    }

    def genMirror(MethodElement me) {
        def typeParams = "<${element.qualifiedName}, $VISIT_CODE, ${element.qualifiedName}>"
        def type = 'Mirroring.MirrorMethod' + typeParams
        def paramTypesArr = 'new Class[]{' + me.paramClassesCSV() + '}'
        def init = "new $type(${element.qualifiedName}.class,\"$me.simpleName\",$paramTypesArr)"

        javaWriter.emitField(new FieldSpec(
                name: me.reflectionFieldName + '_s',
                type: type,
                initializer: init,
                modifiers: PSF))
    }

    def genIFace(MethodElement me) {
        def ext = ["iAcceptor<${element.qualifiedName}>",
                   "iFunction<$VISIT_CODE,${element.qualifiedName}>"]

        javaBuilder.createInterface(name: me.simpleName + '_interface',
                                    modifiers: PS,
                                    implements: ext) {

            method(name: me.simpleName,
                   returnType: VISIT_CODE,
                   params: [p0: VISUAL_ELEMENT])
            method(name: 'updateable',
                   returnType: 'iUpdateable',
                   params: [p0: VISUAL_ELEMENT])
            method(name: 'bind',
                   returnType: "iProvider<$VISIT_CODE>",
                   params: [p0: VISUAL_ELEMENT])

        }
    }

    static final Set<Modifier> PS = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
    static final Set<Modifier> PSF = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    static final Set<Modifier> PF = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    static final Set<Modifier> F = EnumSet.of(Modifier.FINAL)

    def genImpl(MethodElement me) {

        javaBuilder.createClass(name: me.simpleName + '_impl',
                                modifiers: [Modifier.STATIC],
                                implements: [me.simpleName + '_interface']) {
            field(name: 'x',
                  type: element.simpleName,
                  modifiers: F)
            field(name: 'a',
                  type: 'iAcceptor',
                  modifiers: F)
            field(name: 'f',
                  type: 'iFunction',
                  modifiers: F)
            constructor(params: [x: element.simpleName]) {
                stmnt 'this.x=x'
                stmnt "this.a=${me.simpleName}_s.acceptor(x)"
                stmnt "this.f=${me.simpleName}_s.function(x)"
            }

            method(name: me.simpleName,
                   returnType: VISIT_CODE,
                   params: [p0: VISUAL_ELEMENT],
                   modifiers: [Modifier.PUBLIC],
                   annotations: [Override])
                    {
                        stmnt "return x.${me.simpleName}(p0)"
                    }

            method(name: 'set',
                   returnType: 'iAcceptor<field.core.dispatch.iVisualElement>',
                   params: [p: VISUAL_ELEMENT],
                   modifiers: [Modifier.PUBLIC],
                   annotations: [Override])
                    {
                        stmnt 'a.set(p)'
                        stmnt 'return this'
                    }
            method(name: 'f',
                   returnType: VISIT_CODE,
                   params: [p: VISUAL_ELEMENT],
                   modifiers: [Modifier.PUBLIC],
                   annotations: [Override])
                    {
                        stmnt 'return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p)'
                    }
            method(name: 'updateable',
                   returnType: 'iUpdateable',
                   params: [p: VISUAL_ELEMENT],
                   modifiers: [Modifier.PUBLIC],
                   annotations: [Override]) {

                stmnt """
                |return new iUpdateable(){
                |   public void update(){
                |       ${me.simpleName}(p);
                |   }
                |}""".stripMargin()
            }
            method(name: 'bind',
                   returnType: 'iProvider<field.math.graph.GraphNodeSearching.VisitCode>',
                   params: [p: VISUAL_ELEMENT],
                   modifiers: [Modifier.PUBLIC],
                   annotations: [Override]) {

                stmnt """   |return new iProvider(){
                            |    public Object get(){
                            |        return added(p0);
                            |        }
                            |}""".stripMargin()

            }


        }

    }


}
