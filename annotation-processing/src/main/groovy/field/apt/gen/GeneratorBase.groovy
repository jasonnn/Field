package field.apt.gen

import field.apt.util.FieldsAndMethods
import field.bytecode.protect.annotations.GenerateMethods

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
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


    String defaultPrefix
    boolean isInterface

    //  JavaBuilder javaBuilder

    GeneratorBase(ProcessingEnvironment env,
                  TypeElement element) {
        this.env = env
        this.element = element
        def fm = FieldsAndMethods.forElement(element)
        this.fields = fm.fields
        def fact=new MethodElement.Factory(env)
        this.methods = fm.methods.collect { fact.create(it) }
        this.isInterface = element.kind == ElementKind.INTERFACE
        this.defaultPrefix = element.getAnnotation(GenerateMethods).prefix()
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

}
