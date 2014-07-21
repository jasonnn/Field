package asm;

import sun.misc.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by jason on 7/16/14.
 */
public class BytecodeTestCase {
    public static String pathToClassFile(Class cls) {
        return '/' + cls.getName().replace('.', '/') + ".class";
    }

    public static String binaryName(Class cls) {
        return cls.getName().replace('.', '/');
    }

    public static byte[] readClass(Class cls) {
        try {
            return IOUtils.readFully(new BufferedInputStream(cls.getResourceAsStream(pathToClassFile(cls))), -1, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    static byte[] getBytes(Class cls) {
//        BufferedInputStream is = new BufferedInputStream(cls.getResourceAsStream(getPathToClassFile(cls)));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            int read = is.read();
//            while (read != -1) {
//                baos.write(read);
//                read = is.read();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return baos.toByteArray();
//
//    }
}
