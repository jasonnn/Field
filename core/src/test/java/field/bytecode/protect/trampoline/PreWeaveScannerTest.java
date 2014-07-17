package field.bytecode.protect.trampoline;

import field.Blank2;
import field.bytecode.BytecodeTestCase;
import field.launch.iLaunchable;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


public class PreWeaveScannerTest extends BytecodeTestCase {


    @Test
    public void testScan() throws Exception {

        byte[] bytes = readClass(Blank2.class);
        PreWeaveScanner.ScanResult result = PreWeaveScanner.scan(bytes);
        assertTrue(result.isWoven);
        assertFalse(result.isInner);
        assertEquals(binaryName(Object.class), result.extendsClass);
        assertEquals(Arrays.asList(binaryName(iLaunchable.class)), result.implementsClasses);


    }
}