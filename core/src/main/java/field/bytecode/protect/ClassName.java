package field.bytecode.protect;

/**
 * Created by jason on 7/15/14.
 */
public interface ClassName extends CharSequence{
    String getName();
    String getSimpleName();
    String getBinaryName();
    String getPathToClassFile();
    String getDescriptor();
}
