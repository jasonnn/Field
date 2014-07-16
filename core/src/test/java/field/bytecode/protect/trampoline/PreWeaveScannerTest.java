package field.bytecode.protect.trampoline;

import field.Blank2;
import field.bytecode.protect.Woven;
import field.launch.iLaunchable;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;


public class PreWeaveScannerTest {
    static String classPath(Class cls) {
        return '/' + cls.getName().replace('.', '/') + ".class";
    }

    static byte[] getBytes2(Class cls) {
        try {
            return IOUtils.readFully(new BufferedInputStream(cls.getResourceAsStream(classPath(cls))), -1, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] getBytes(Class cls) {
        BufferedInputStream is = new BufferedInputStream(cls.getResourceAsStream(classPath(cls)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int read = is.read();
            while (read != -1) {
                baos.write(read);
                read = is.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return baos.toByteArray();

    }

    static String binaryName(Class cls) {
        return cls.getName().replace('.', '/');
    }

    @Test
    public void testScan() throws Exception {

        byte[] bytes = getBytes2(Blank2.class);
        PreWeaveScanner.ScanResult result = PreWeaveScanner.scan(bytes);
        assertTrue(result.isWoven);
        assertFalse(result.isInner);
        assertEquals(binaryName(Object.class), result.extendsClass);
        assertEquals(Arrays.asList(binaryName(iLaunchable.class)), result.implementsClasses);


    }
}