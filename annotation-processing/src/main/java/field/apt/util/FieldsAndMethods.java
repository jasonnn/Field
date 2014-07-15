package field.apt.util;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner6;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 7/11/14.
 */
public class FieldsAndMethods extends ElementScanner6<FieldsAndMethods, Void> {
    public static FieldsAndMethods forElement(TypeElement e) {
        return e.accept(new FieldsAndMethods(), null);
    }


    public final List<VariableElement> fields = new ArrayList<VariableElement>();
    public final List<ExecutableElement> methods = new ArrayList<ExecutableElement>();

    /**
     * This implementation scans the enclosed elements.
     *
     * @param e     {@inheritDoc}
     * @param aVoid {@inheritDoc}
     * @return the result of scanning
     */
    @Override
    public FieldsAndMethods visitVariable(@NotNull VariableElement e, Void aVoid) {
        if (e.getKind() == ElementKind.FIELD) fields.add(e);
        return this;
    }

    /**
     * Processes an element by calling {@code e.accept(this, p)};
     * this method may be overridden by subclasses.
     *
     * @param e     the element to scan
     * @param aVoid a scanner-specified parameter
     * @return the result of visiting {@code e}.
     */
    @Override
    public FieldsAndMethods scan(Element e, Void aVoid) {
        super.scan(e, aVoid);
        return this;
    }

    /**
     * {@inheritDoc} This implementation scans the parameters.
     *
     * @param e     {@inheritDoc}
     * @param aVoid {@inheritDoc}
     * @return the result of scanning
     */
    @Override
    public FieldsAndMethods visitExecutable(@NotNull ExecutableElement e, Void aVoid) {
        if (e.getKind() == ElementKind.METHOD) methods.add(e);
        return this;
    }
}
