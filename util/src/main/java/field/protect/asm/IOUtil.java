package field.protect.asm;

import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jason on 7/26/14.
 */
public
class IOUtil {

    public static
    byte[] readFully(InputStream stream) throws IOException {
        return IOUtils.readFully(stream, -1, true);
    }
}
