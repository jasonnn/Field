package field.apt.gen
import field.apt.util.FieldsAndMethods
import field.apt.util.GenUtils
import field.bytecode.protect.annotations.GenerateMethods
import field.bytecode.protect.annotations.Mirror
import javabuilder.JavaBuilder
import javabuilder.writer.JavaWriterEx

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.JavaFileObject
/**
 * Created by jason on 7/12/14.
 */
class GeneratorBase {
    public static final String DEFAULT_SUFFIX = "_m";


    TypeElement element
    List<VariableElement> fields
    List<MethodElement> methods
    ProcessingEnvironment env
    Types types
    Elements elements
    Messager log


    String defaultPrefix
    boolean isInterface

    @Delegate
    JavaBuilder javaBuilder
    JavaWriterEx javaWriter

    GeneratorBase(ProcessingEnvironment env,
                  TypeElement element) {
        this.env = env
        this.types=env.typeUtils
        this.elements=env.elementUtils
        this.log=env.messager

        this.element = element
        def fm = FieldsAndMethods.forElement(element)
        this.fields = fm.fields
        def fact = new MethodElement.Factory(env)
        this.methods = fm.methods.findAll { it.getAnnotation(Mirror) }.collect { fact.create(it) }
        this.isInterface = element.kind == ElementKind.INTERFACE
        this.defaultPrefix = element.getAnnotation(GenerateMethods).prefix()
    }

    public void init() {
        def jfo = createJFO()
        setJavaBuilder(new JavaBuilder(jfo.openWriter()))
    }

    public void setJavaBuilder(JavaBuilder jb) {
        this.javaBuilder = jb
        this.javaWriter = jb.javaWriter
    }


    protected JavaFileObject createJFO() {
        return env.filer.createSourceFile(generatedFQN, element)
    }

    PackageElement getPackageElement() {
        for (def parent = element; parent; parent = parent.enclosingElement) {
            if (parent.kind == ElementKind.PACKAGE) return (PackageElement) parent;
        }
        throw new RuntimeException("??!!");
    }

    String getPackageName() {
        return packageElement.qualifiedName
    }

    String getGeneratedSimpleName() {
        return element.simpleName + DEFAULT_SUFFIX
    }

    String getGeneratedFQN() {
        return element.qualifiedName + DEFAULT_SUFFIX
    }

    def hr() {
        char[] chars = new char[80]
        char fillChar = '-'
        Arrays.fill(chars, fillChar)
        javaWriter.emitSingleLineComment(new String(chars))
    }

    def emitEmptyLine() {
        javaWriter.emitEmptyLine()
    }
//TODO see if params share a common supertype other than Object
    def acceptorGenericArg(MethodElement me) {
        def nParams = me.parameters.size();
        if(nParams==0) return 'Void'
        if (nParams == 1) {
            def param = me.parameters[0].asType()
            return GenUtils.getRawTypeName(true, types, param)
        } else {
            return 'Object[]'
        }
    }

//    def boxedClassName(TypeMirror mirror) {
//        return RawTypeNameVisitor.BOXING.visit(mirror, env.typeUtils)
//    }
}
