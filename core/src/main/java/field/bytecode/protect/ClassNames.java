package field.bytecode.protect;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

/**
 * Created by jason on 7/15/14.
 */
public enum ClassNames implements ClassName {
    FIELD_BLANK2("field.Blank2");


  @NotNull  final Type type;

    ClassNames(Class<?> cls) {
        this(Type.getInternalName(cls));
    }

    ClassNames(String internalName) {
        this.type = Type.getObjectType(internalName);
    }

    @Override
    public String getName() {
        return type.getClassName();
    }

    @Override
    public String getSimpleName() {
        String full = getName();
        int start = full.indexOf('$');
        if (start < 0) {
            start = full.lastIndexOf('.');
        }
        return full.substring(start + 1);
    }

    @Override
    public String getBinaryName() {
        return type.getClassName();
    }

    public String getInternalName() {
        return type.getInternalName();
    }

    @Override
    public String getPathToClassFile() {
        return '/' + getName().replace('.', '/') + ".class";
    }

    @Override
    public String getDescriptor() {
        return type.getDescriptor();
    }

    @Override
    public int length() {
        return getName().length();
    }

    @Override
    public char charAt(int index) {
        return getName().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getName().subSequence(start, end);
    }

    @Override
    @NotNull
    public String toString() {
        return getName();
    }
    }
