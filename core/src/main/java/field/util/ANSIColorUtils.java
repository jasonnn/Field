package field.util;

import field.core.Platform;

/**
 * old skhool.
 *
 * @author marc <I>Created on Mar 19, 2003</I>
 */
public
class ANSIColorUtils {

    public static final char esc = (char) 0x1b;

    public static
    String red(String s) {
        if (Platform.getOS() != Platform.OS.mac) return s;
        return esc + "[31m" + s + esc + "[0m";
    }

    public static
    String blue(String s) {
        if (Platform.getOS() != Platform.OS.mac) return s;
        return esc + "[34m" + s + esc + "[0m";
    }

    public static
    String green(String s) {
        if (Platform.getOS() != Platform.OS.mac) return s;
        return esc + "[32m" + s + esc + "[0m";
    }

    public static
    String yellow(String s) {
        if (Platform.getOS() != Platform.OS.mac) return s;
        return esc + "[33m" + s + esc + "[0m";
    }

    /**
     * for use with \r. for example
     * System.out.print(eraseLine()+" status = "+i+" \r");
     *
     * @return
     */
    public static
    String eraseLine() {
        if (Platform.getOS() != Platform.OS.mac) return "\n";
        return esc + "[K";
    }

    public static
    String eraseScreen() {
        if (Platform.getOS() != Platform.OS.mac) return "\n";
        return esc + "[2J";
    }
}
