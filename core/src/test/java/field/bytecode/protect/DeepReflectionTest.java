package field.bytecode.protect;

import org.junit.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class DeepReflectionTest {
    static Set<Class> set(Class... classes) {
        return new LinkedHashSet<Class>(Arrays.asList(classes));
    }


    @Test
    public void testGetSuperClasses() throws Exception {
        Set<Class> supers = DeepReflection.getSuperClasses(ArrayList.class);
        Set<Class> expect = set(ArrayList.class, AbstractList.class, AbstractCollection.class);
        assertEquals(expect, supers);
    }

    @Test
    public void testGetInterfaces_depthFirst() throws Exception {
        Set<Class> interfaces = DeepReflection.getInterfacesDF(ArrayList.class);
        Set<Class> expect = set(List.class, Collection.class, Iterable.class,
                RandomAccess.class, Cloneable.class, Serializable.class);

        assertEquals(expect, interfaces);

    }

    @Test
    public void testGetInterfaces_breadthFirst() throws Exception {
        Set<Class> interfaces = DeepReflection.getInterfacesBF(ArrayList.class);
        Set<Class> expect = set(List.class, RandomAccess.class, Cloneable.class, Serializable.class,
                Collection.class,
                Iterable.class);

        assertEquals(expect, interfaces);

    }

    @Test
    public void testGetHeirarchy() throws Exception {
        //breadth-first: [class java.util.ArrayList, class java.util.AbstractList, interface java.util.List, interface java.util.RandomAccess, interface java.lang.Cloneable, interface java.io.Serializable, class java.util.AbstractCollection]
        //depth-first: [class java.util.ArrayList, class java.util.AbstractList, class java.util.AbstractCollection, interface java.util.List, interface java.util.RandomAccess, interface java.lang.Cloneable, interface java.io.Serializable]
        //H: [class java.util.ArrayList, class java.util.AbstractList, class java.util.AbstractCollection, interface java.util.List, interface java.util.RandomAccess, interface java.lang.Cloneable, interface java.io.Serializable, interface java.util.Collection, interface java.lang.Iterable]

        Set<Class> hi = DeepReflection.getH(ArrayList.class);
        System.out.println(hi);

    }
}