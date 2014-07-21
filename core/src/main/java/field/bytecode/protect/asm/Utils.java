package field.bytecode.protect.asm;

import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jason on 7/20/14.
 */
public class Utils {

    public static byte[] readFully(InputStream stream) throws IOException {
        return IOUtils.readFully(stream, -1, true);
    }
}
