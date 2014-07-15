package field.apt.gen

import field.apt.CodeGen
import field.apt.util.GenUtils
import groovy.transform.InheritConstructors
import javabuilder.JavaBuilder
import javabuilder.config.FieldSpec
import javabuilder.writer.JavaWriterEx

import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

/**
 * Created by jason on 7/11/14.
 */
@InheritConstructors
class GCodeGenerator extends GeneratorBase implements CodeGen {

    JavaBuilder javaBuilder
    JavaWriterEx javaWriter

//    TypeElement element
//    List<VariableElement> fields
//    List<ExecutableElement> methods
//    ProcessingEnvironment env
//
//
//    String defaultPrefix
//    boolean isInterface
//
//    JavaBuilder javaBuilder
//
//    public CodeGenerator(ProcessingEnvironment env,
//                         Writer out,
//                         TypeElement element,
//                         List<VariableElement> fields,
//                         List<ExecutableElement> methods) {
//        this.env = env
//        this.element = element
//        this.fields = fields
//        this.methods = methods
//        this.javaBuilder = new JavaBuilder(out)
//        this.isInterface = element.kind == ElementKind.INTERFACE
//        this.defaultPrefix = element.getAnnotation(GenerateMethods).prefix()
//
//    }

    @Override
    void generate() throws IOException {

        javaBuilder.compilationUnit {
            packageName = GenUtils.packageNameOf(element)
            imports.add([
                    'java.lang.reflect.*',
                    'java.util.*',
                    'field.bytecode.apt.*',
                    'field.namespace.generic.Bind.*',
                    'field.math.abstraction.*',
                    'field.launch.*',
                    'field.namespace.generic.ReflectionTools'])
            createClass(name: GenUtils.generatedSimpleName(element),
                    modifiers: [Modifier.PUBLIC]) {

                for (meth in methods) {
                    genAccessField(meth)
                }

            }

        }
    }


    def genAccessField(MethodElement me) {
        def paramStr = ''
        if (!me.parameters.isEmpty()) {
            paramStr = ', ' + me.paramClassNames().collect { it + '.class' }.join(', ')
        }
        javaWriter.emitField(new FieldSpec(
                name: me.reflectionFieldName + '_m',
                type: 'java.lang.reflect.Method',
                modifiers: [Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL],
                initializer: "ReflectionUtils.methodOf\"$me.simpleName\",${element.qualifiedName}.class$paramStr)"
        ))
    }

    def genMirror(MethodElement me) {
        def typeParams = "<${element.qualifiedName}, field.math.graph.GraphNodeSearching.VisitCode, ${element.qualifiedName}>"
        def type = 'Mirroring.MirrorMethod' + typeParams
        def paramTypesArr = 'new Class[]{' + me.paramClassNames().collect { it + '.class' }.join(', ') + '}'
        def init = "new $type(${element.qualifiedName}.class,$me.simpleName,$paramTypesArr)"

        javaWriter.emitField(new FieldSpec(
                name: me.reflectionFieldName + '_s',
                type: type,
                modifiers: [Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL],
                initializer: init

        ))
    }
    //TODO generated API isnt actually SAM compatible :(
    def genSAMType(MethodElement me) {

    }

    def genSAMField(MethodElement me) {

    }

    def genSAMInitializer(MethodElement me) {

    }
    /**
     * (from iVisualElementOverrides...)
     * given:
     *
     * @Mirror public VisitCode added(iVisualElement newSource);
     * <p/>
     * emit:
     * <p/>
     * static public final Method added_m = ReflectionTools.methodOf("added", field.core.dispatch.iVisualElementOverrides.class, field.core.dispatch.iVisualElement.class);
     * static public final Mirroring.MirrorMethod<field.core.dispatch.iVisualElementOverrides, field.math.graph.GraphNodeSearching.VisitCode, field.core.dispatch.iVisualElement>
     *     added_s = new Mirroring.MirrorMethod<field.core.dispatch.iVisualElementOverrides, field.math.graph.GraphNodeSearching.VisitCode, field.core.dispatch.iVisualElement>
     *         (field.core.dispatch.iVisualElementOverrides.class, "added", new Class[]{field.core.dispatch.iVisualElement.class});
     * <p/>
     * public interface added_interface
     * extends iAcceptor<field.core.dispatch.iVisualElement>,
     * iFunction<field.math.graph.GraphNodeSearching.VisitCode ,field.core.dispatch.iVisualElement >
     *{
     * public field.math.graph.GraphNodeSearching.VisitCode added( final field.core.dispatch.iVisualElement p0);
     * public iUpdateable updateable(final field.core.dispatch.iVisualElement p0);
     * public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(final field.core.dispatch.iVisualElement p0);
     *}* <p/>
     * public final added_interface added;
     * <p/>
     * <p/>
     * and in the constructor...
     * <p/>
     * <p/>
     * added = new added_interface()
     *{
     * <p/>
     * iAcceptor a = added_s.acceptor(x);
     * iFunction f = added_s.function(x);
     * <p/>
     * <p/>
     * public field.math.graph.GraphNodeSearching.VisitCode added (final field.core.dispatch.iVisualElement p0)
     *{
     * return x.added(p0 );
     *}* <p/>
     * public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p)
     *{
     * a.set(p);
     * return this;
     *}* <p/>
     * public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p)
     *{
     * return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
     *}* <p/>
     * public iUpdateable updateable(final field.core.dispatch.iVisualElement p0)
     *{
     * return new iUpdateable()
     *{
     * public void update()
     *{
     * added(p0);
     *}*};
     *}* public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(final field.core.dispatch.iVisualElement p0){
     * return new iProvider(){public Object get(){return added(p0);}};}};
     */


}
