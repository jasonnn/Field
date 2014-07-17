package field.bytecode.protect.analysis.model;

import field.Blank2;
import field.bytecode.BytecodeTestCase;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class SimpleModelBuilderTest extends BytecodeTestCase {

    @Test
    public void testBuildModel() throws Exception {
        Class<?> cls = Blank2.class;

        byte[] data = readClass(cls);

        SimpleClassModel model = SimpleModelBuilder.buildModel(data);
        assertNotNull(model);


        assertEquals("field/Blank2", model.name);
        assertEquals("java/lang/Object", model.superName);

        assertTrue(model.annotations.contains("Lfield/bytecode/protect/Woven;"));

        int expectedFields = 4;
        Field[] rFields = cls.getDeclaredFields();
        assertEquals(expectedFields, rFields.length);
        assertEquals(expectedFields, model.fields.size());

        int expectedMethods = 3;
        Method[] rMethods = cls.getDeclaredMethods();
        assertEquals(expectedMethods, rMethods.length);
        assertEquals(expectedMethods, model.methods.size());




    }
}