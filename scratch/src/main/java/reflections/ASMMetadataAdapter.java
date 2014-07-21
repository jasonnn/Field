package reflections;

import field.bytecode.protect.analysis.model.*;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.vfs.Vfs;
import sun.misc.IOUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jason on 7/17/14.
 */
public class ASMMetadataAdapter implements MetadataAdapter<SimpleClassModel, SimpleFieldModel, SimpleMethodModel> {
    @Override
    public String getClassName(SimpleClassModel cls) {
        return cls.name;
    }

    @Override
    public String getSuperclassName(SimpleClassModel cls) {
        return cls.superName;
    }

    @Override
    public List<String> getInterfacesNames(SimpleClassModel cls) {
        return Arrays.asList(cls.interfaces);
    }

    @Override
    public List<SimpleFieldModel> getFields(SimpleClassModel cls) {
        return new ArrayList<SimpleFieldModel>(cls.fields);
    }

    @Override
    public List<SimpleMethodModel> getMethods(SimpleClassModel cls) {
        return new ArrayList<SimpleMethodModel>(cls.methods);
    }

    @Override
    public String getMethodName(SimpleMethodModel method) {
        return method.name;
    }

    @Override
    public List<String> getParameterNames(SimpleMethodModel method) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getClassAnnotationNames(SimpleClassModel aClass) {
        return new ArrayList<String>(aClass.annotations);
    }

    @Override
    public List<String> getFieldAnnotationNames(SimpleFieldModel field) {
        return new ArrayList<String>(field.annotations);
    }

    @Override
    public List<String> getMethodAnnotationNames(SimpleMethodModel method) {
        return new ArrayList<String>(method.annotations);
    }

    @Override
    public List<String> getParameterAnnotationNames(SimpleMethodModel method, int parameterIndex) {
        return Collections.emptyList();
    }

    @Override
    public String getReturnTypeName(SimpleMethodModel method) {
        return "??";
    }

    @Override
    public String getFieldName(SimpleFieldModel field) {
        return field.name;
    }

    @Override
    public SimpleClassModel getOfCreateClassObject(Vfs.File file) throws Exception {
        return SimpleModelBuilder.buildModel(IOUtils.readFully(file.openInputStream(), -1, true));

    }

    @Override
    public String getMethodModifier(SimpleMethodModel method) {
        return Modifier.toString(method.access);
    }

    @Override
    public String getMethodKey(SimpleClassModel cls, SimpleMethodModel method) {
        return null;
    }

    @Override
    public String getMethodFullKey(SimpleClassModel cls, SimpleMethodModel method) {
        return null;
    }

    @Override
    public boolean isPublic(Object o) {
        return Modifier.isPublic(((AbstractSimpleModel)o).access);

    }

    @Override
    public boolean acceptsInput(String file) {
        return file.endsWith(".class");
    }
}
