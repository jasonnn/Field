package field.launch;

import field.util.MiscNative;
import field.util.Omnioutliner3;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class SystemProperties {

    public static final boolean debug = false;

    static HashMap<String, String> additionalProperties = new HashMap<String, String>();

    static String hostname = null;

    static {
        String o = System.getProperty("options", null);
        if (o != null) {
            insertOo3Properties(o);
        }
        for (int i = 0; i < (Launcher.args.length - 1); i++) {
            String a = Launcher.args[i];
            if (a.startsWith("-") && (a.length() > 1)) {
                String n = Launcher.args[i + 1];
                additionalProperties.put(a.substring(1), n);
            }
        }
        //System.out.println("ap :"+additionalProperties);
    }

    public static
    boolean getBooleanProperty(String s, boolean def) {
        String p = getProperty(s);
        if (p == null) return def;
        return "true".equalsIgnoreCase(p);
    }

    /**
     * exactly the same as getProperty except this makes sure that the property it
     * returns has a trailing '/'. great for directory names, hence the name
     */

    public static
    String getDirProperty(String s) {
        String p = getProperty(s);

        if (p == null) return null;

        if (!p.endsWith("/")) return p + '/';
        return p;
    }

    /**
     * Get multiple directory values (guaranteed trailing "/") from a property
     * when items separated by ":".
     */

    public static
    String[] getDirProperties(String s) {
        String[] ps = getProperties(s);

        for (int i = 0; i < ps.length; i++) {
            if (!ps[i].endsWith("/")) { ps[i] = ps[i] + '/'; }
        }

        return ps;
    }


    public static
    String getDirProperty(String s, String def) {
        String p = getProperty(s, def);

        if (p == null) return null;

        if (!p.endsWith("/")) return p + '/';
        return p;
    }

    public static
    double getDoubleProperty(String s, double def) {
        String p = getProperty(s, String.valueOf(def));

        double ret = def;
        try {
            ret = Double.parseDouble(p);
        } catch (Exception ex) {
        }
        return ret;
    }

    public static
    int getIntProperty(String s, int def) {
        String p = getProperty(s, String.valueOf(def));

        int ret = def;
        try {
            ret = Integer.parseInt(p);
        } catch (Exception ex) {
        }
        return ret;
    }

    public static
    String[] getMaybeArrayProperty(String s, String def) {
        String d = getProperty(s, def);
        String[] x = d.split(";");
        List<String> r = new ArrayList<String>();
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i].trim();
        }
        return x;
    }

    public static
    List<String> getMaybeListProperty(String s, String def) {
        String d = getProperty(s, def);
        String[] x = d.split(";");
        List<String> r = new ArrayList<String>();
        for (int i = 0; i < x.length; i++) {
            r.add(x[i].trim());
        }
        return r;
    }

    public static
    String getProperty(String key) {
        if (debug) {
            StackTraceElement[] st = new Exception().getStackTrace();
            System.err.println(" property <" + key + "> from <" + st[st.length - 2] + '>');
        }
        return getProperty(key, "");
    }

    /**
     * Get multiple values from a property when separated by ":".
     */

    public static
    String[] getProperties(String key) {
        String s = getProperty(key);

        if ("".equals(s)) {
            return new String[]{};
        }
        else {
            return s.split(":");
        }
    }

    /**
     * tries to get a property called 'key' - if this is the first time
     * that the properties file has been accessed then this results in a
     * the properties file being loaded
     */
    public static
    String getProperty(String key, String def) {
        String ap = additionalProperties.get(key);

        if (ap != null) return ap;

        ap = new MiscNative().getDefaultsProperty_safe(key);

        if (ap != null) return ap;

        String p = System.getProperty(key, null);
        if (p != null) return p;
        return def;
    }

    public static
    String hostname() {

        try {
            return hostname != null ? hostname : (hostname = InetAddress.getByName("127.0.0.1").getCanonicalHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert false;
        return null;
    }

    public static
    void insertOo3Properties(String file) {
        Omnioutliner3 oo3 = new Omnioutliner3();
        oo3.read(new File(file));
        List<List<String>> items = oo3.getItems();
        for (List<String> list : items) {
            for (String string : list) {
                Map<String, String> prop = Omnioutliner3.stringToProperties(string);

                for (String s : prop.keySet()) {
                }

                additionalProperties.putAll(prop);
            }
        }
    }

    public static
    void setProperty(String key, String value) {
        System.setProperty(key, value);
    }

    public static
    HashMap<String, Object> getProperties() {
        HashMap<String, Object> a = new HashMap<String, Object>(additionalProperties);

        String k = new MiscNative().getPropertyKeys_safe();

        for (String kk : k.split(":")) {
            if (canBePropertyName(kk)) {
                String pp = getProperty(kk);
                try {
                    a.put(kk, Integer.parseInt(pp));
                } catch (NumberFormatException n) {
                    a.put(kk, pp);
                }
            }
        }

        //System.out.println(" additional are <"+a+">");

        return a;
    }

    private static
    boolean canBePropertyName(String kk) {
        if (kk.length() == 0) return false;
        if (!Character.isJavaIdentifierStart(kk.charAt(0))) return false;
        if (kk.startsWith("NS") || kk.startsWith("Apple") || kk.startsWith("NavPanel") || kk.startsWith("Key"))
            return false;

        for (int i = 1; i < kk.length(); i++) {
            if (!Character.isJavaIdentifierPart(kk.charAt(i))) return false;
        }

        return true;
    }

}
