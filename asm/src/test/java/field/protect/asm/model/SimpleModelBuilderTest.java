package field.protect.asm.model;

import asm.BytecodeTestCase;
import model.Impl;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class SimpleModelBuilderTest extends BytecodeTestCase {


    @Test
    public void testBuildModel() throws Exception {
        Class<?> cls = Impl.class;

        byte[] data = readClass(cls);

        SimpleClassModel model = SimpleModelBuilder.buildModel(data);
        assertNotNull(model);


        assertEquals("model/Impl", model.name);
        assertEquals("java/lang/Object", model.superName);

        assertTrue(model.annotations.contains("Lmodel/Ann;"));
        assertFalse(model.isWoven());
        assertFalse(model.isInner());
        assertFalse(model.isInterface());

        int expectedFields = 1;
        Field[] rFields = cls.getDeclaredFields();
        assertEquals(expectedFields, rFields.length);
        assertEquals(expectedFields, model.fields.size());

        int expectedMethods = 2;
        Method[] rMethods = cls.getDeclaredMethods();
        assertEquals(expectedMethods, rMethods.length);
        assertEquals(expectedMethods, model.methods.size());
    }

}