package field.bytecode.protect;

import field.bytecode.protect.util.DeepReflection;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;

public
class DeepReflectionTest {
    static
    Set<Class> set(Class... classes) {
        return new LinkedHashSet<Class>(Arrays.asList(classes));
    }


    @Test
    public
    void testGetSuperClasses() throws Exception {
        Set<Class> supers = DeepReflection.getSuperClasses(ArrayList.class);
        Set<Class> expect = set(ArrayList.class, AbstractList.class, AbstractCollection.class);
        assertEquals(expect, supers);
    }

    @Test
    public
    void testGetInterfaces_depthFirst() throws Exception {
        Set<Class> interfaces = DeepReflection.getInterfacesDF(ArrayList.class);
        Set<Class> expect = set(List.class,
                                Collection.class,
                                Iterable.class,
                                RandomAccess.class,
                                Cloneable.class,
                                Serializable.class);

        assertEquals(expect, interfaces);

    }

    @Test
    public
    void testGetInterfaces_breadthFirst() throws Exception {
        Set<Class> interfaces = DeepReflection.getInterfacesBF(ArrayList.class);
        Set<Class> expect = set(List.class,
                                RandomAccess.class,
                                Cloneable.class,
                                Serializable.class,
                                Collection.class,
                                Iterable.class);

        assertEquals(expect, interfaces);

    }

    @Test
    public
    void testGetHeirarchy_depthFirst() throws Exception {

        Set<Class> expect = set(ArrayList.class,
                                AbstractList.class,
                                AbstractCollection.class,
                                List.class,
                                Collection.class,
                                Iterable.class,
                                RandomAccess.class,
                                Cloneable.class,
                                Serializable.class);

        Set<Class> hi = DeepReflection.getHeirarchyDF(ArrayList.class);
        assertEquals(expect, hi);

    }

    @Test
    public
    void testGetHeirarchy_breadthFirst() throws Exception {
        Set<Class> expect = set(ArrayList.class,
                                AbstractList.class,
                                List.class,
                                RandomAccess.class,
                                Cloneable.class,
                                Serializable.class,
                                AbstractList.class,
                                AbstractCollection.class,
                                List.class,
                                Collection.class,
                                Iterable.class);

        Set<Class> actual = DeepReflection.getHeirarchyBF(ArrayList.class);
        assertEquals(expect, actual);

    }
}