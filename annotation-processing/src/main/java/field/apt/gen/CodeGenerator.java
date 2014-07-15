package field.apt.gen;

import com.squareup.javawriter.JavaWriter;
import field.apt.CodeGen;
import field.apt.util.DelegatingJavaWriter;
import field.apt.util.GenUtils;
import field.bytecode.protect.annotations.GenerateMethods;
import field.bytecode.protect.annotations.Mirror;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by jason on 7/10/14.
 */
@SuppressWarnings("JavaDoc")
public class CodeGenerator extends DelegatingJavaWriter implements CodeGen {


    private final TypeElement element;
    private final List<VariableElement> fields;
    private final List<ExecutableElement> methods;
    private final ProcessingEnvironment env;


    private final String defaultPrefix;
    private final boolean isInterface;

    // private String prefix = "";

    /**
     * @param out     the stream to which Java source will be written. This should be a buffered stream.
     * @param element
     * @param fields
     * @param methods
     */
    public CodeGenerator(ProcessingEnvironment env, Writer out, TypeElement element, List<VariableElement> fields, List<ExecutableElement> methods) {
        super(new JavaWriter(out));
        this.env = env;
        this.element = element;
        this.fields = fields;
        this.methods = methods;
        this.isInterface = element.getKind() == ElementKind.INTERFACE;
        this.defaultPrefix = element.getAnnotation(GenerateMethods.class).prefix();
    }

    @Override
    public void generate() throws IOException {
        emitPackage(getPackageName());

        emitImports(GenUtils.defaultImports());
        emitImports(element.getQualifiedName().toString());

        beginType(generatedSimpleName(), "class", PUBLIC);
        {
            handleFields();
            handleMethods();
            generateConstructor();
        }
        endType();
    }


    private void generateConstructor() {

    }

    private void handleFields() {
        for (VariableElement field : fields) {
            String prefix = getPrefix(field);


        }
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
     * static public final Mirroring.MirrorMethod<field.core.dispatch.iVisualElementOverrides, field.math.graph.GraphNodeSearching.VisitCode, field.core.dispatch.iVisualElement> added_s = new Mirroring.MirrorMethod<field.core.dispatch.iVisualElementOverrides, field.math.graph.GraphNodeSearching.VisitCode, field.core.dispatch.iVisualElement>(field.core.dispatch.iVisualElementOverrides.class, "added", new Class[]{field.core.dispatch.iVisualElement.class});
     * <p/>
     * public interface added_interface extends iAcceptor<field.core.dispatch.iVisualElement>, iFunction<field.math.graph.GraphNodeSearching.VisitCode ,field.core.dispatch.iVisualElement >
     * {
     * public field.math.graph.GraphNodeSearching.VisitCode added( final field.core.dispatch.iVisualElement p0);
     * public iUpdateable updateable(final field.core.dispatch.iVisualElement p0);
     * public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(final field.core.dispatch.iVisualElement p0);
     * }
     * <p/>
     * public final added_interface added;
     * <p/>
     * <p/>
     * and in the constructor...
     * <p/>
     * <p/>
     * added = new added_interface()
     * {
     * <p/>
     * iAcceptor a = added_s.acceptor(x);
     * iFunction f = added_s.function(x);
     * <p/>
     * <p/>
     * public field.math.graph.GraphNodeSearching.VisitCode added (final field.core.dispatch.iVisualElement p0)
     * {
     * return x.added(p0 );
     * }
     * <p/>
     * public iAcceptor<field.core.dispatch.iVisualElement> set(field.core.dispatch.iVisualElement p)
     * {
     * a.set(p);
     * return this;
     * }
     * <p/>
     * public field.math.graph.GraphNodeSearching.VisitCode f(field.core.dispatch.iVisualElement p)
     * {
     * return (field.math.graph.GraphNodeSearching.VisitCode) f.f(p);
     * }
     * <p/>
     * public iUpdateable updateable(final field.core.dispatch.iVisualElement p0)
     * {
     * return new iUpdateable()
     * {
     * public void update()
     * {
     * added(p0);
     * }
     * };
     * }
     * public iProvider<field.math.graph.GraphNodeSearching.VisitCode> bind(final field.core.dispatch.iVisualElement p0){
     * return new iProvider(){public Object get(){return added(p0);}};}};
     */
    private void handleMethods() {
        for (ExecutableElement method : methods) {
            Mirror mirror = method.getAnnotation(Mirror.class);
            if (mirror == null) continue;

            String prefix = mirror.prefix();

            List<String> paramTypeNames = simpleTypeParamNames(method);

            String typeForGenericDecl = null;
            String typesForConstructor = null;
            String typesForDeclaration = null;
            String typesForInvocation = null;
            int pnum = 0;

            for (VariableElement param : method.getParameters()) {
                TypeMirror paramType = param.asType();


            }
        }
    }

    void genReflectionMethod(ExecutableElement meth) throws IOException {
        emitField(Method.class.getName(),
                meth.getSimpleName().toString() + "_m",
                EnumSet.of(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL));
    }


    private static List<String> simpleTypeParamNames(ExecutableElement elem) {
        List<? extends TypeParameterElement> params = elem.getTypeParameters();
        List<String> simpleParamNames = new ArrayList<String>(params.size());
        for (TypeParameterElement typeParam : params) {
            simpleParamNames.add(typeParam.getSimpleName().toString());
        }
        return simpleParamNames;
    }

    private static String getPrefix(Element e) {
        Mirror mirror = e.getAnnotation(Mirror.class);
        return (mirror == null) ? "" : mirror.prefix();
    }

    private String getPackageName() {
        return GenUtils.packageNameOf(element);
    }



    private String generatedFQN() {
        return GenUtils.generatedFQN(element);
    }

    private String generatedSimpleName() {
        return GenUtils.generatedSimpleName(element);
    }

    private static List<String> param(CharSequence type, String name) {
        return Arrays.asList(type.toString(), name);
    }

    private static final Set<Modifier> PUBLIC = Collections.unmodifiableSet(EnumSet.of(Modifier.PUBLIC));

}
