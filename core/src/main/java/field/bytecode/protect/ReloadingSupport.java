package field.bytecode.protect;

import field.bytecode.protect.trampoline.Trampoline2;
import field.namespace.generic.IFunction;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public
class ReloadingSupport {

    public static
    class ChildClassLoader extends ClassLoader {
        public Map<String, byte[]> map;

        public
        ChildClassLoader(ClassLoader parent, Map<String, byte[]> map) {
            super(parent);
            this.map = map;
        }

        public
        ChildClassLoader(ClassLoader parent) {
            super(parent);
            this.map = new HashMap<String, byte[]>();
        }

        public
        void add(String name, byte[] bytes) {
            this.map.put(name, bytes);
        }

        @Override
        protected
        Class<?> findClass(String name) throws ClassNotFoundException {
            if (should(name)) {
                byte[] b = map.get(name);
                return defineClass(name, b, 0, b.length);
            }
            else throw new ClassNotFoundException(name);
        }

        @Override
        public
        Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class cls = findLoadedClass(name);
            if (cls == null) {
                if (should(name)) cls = findClass(name);
                else cls = super.loadClass(name, false);
            }
            if (resolve) this.resolveClass(cls);
            return cls;
        }

        protected
        boolean should(String name) {
            return map.containsKey(name);
        }
    }

    public
    class ReloadingDomain {
        public IFunction<String, Boolean> matcher;

        public LinkedHashMap<String, Class> loaded = new LinkedHashMap<String, Class>();

        ChildClassLoader loader;

        public int priority;

        public
        void reload(IFunction<Class, Object> reloadHook) throws ClassNotFoundException {
            newLoader(this);
            Set<Entry<String, Class>> es = loaded.entrySet();
            for (Entry<String, Class> e : es) {
                String k = e.getKey();
                byte[] bytes = Trampoline2.bytesForClass(Trampoline2.trampoline.getClassLoader(), k);

                loader.add(k, bytes);
            }
            for (Entry<String, Class> e : es) {
                Class<?> c = loader.loadClass(e.getKey(), true);
                reloadHook.apply(c);
            }
        }

    }

    List<ReloadingDomain> domains = new ArrayList<ReloadingDomain>();

    public
    void addDomain(final String regex) {
        ReloadingDomain dom = new ReloadingDomain();

        final Pattern p = Pattern.compile(regex);
        dom.matcher = new IFunction<String, Boolean>() {

            @Override
            public
            Boolean apply(String in) {
                return p.matcher(in).matches();
            }

            public
            String toString() {
                return regex;
            }
        };

        dom.priority = regex.length();

        synchronized (domains) {
            domains.add(dom);

            Collections.sort(domains, new Comparator<ReloadingDomain>() {

                @Override
                public
                int compare(ReloadingDomain arg0, ReloadingDomain arg1) {
                    return (arg0.priority < arg1.priority) ? -1 : 1;
                }
            });

        }
    }

    public
    Class delegate(String name, byte[] code) {

        synchronized (domains) {
            for (ReloadingDomain d : domains) {
                Boolean nn = d.matcher.apply(name);

                if (nn) {
                    Class cc = d.loaded.get(name);
                    if (cc != null) {
                        return cc;
                    }

                    if (d.loader == null) {
                        newLoader(d);
                    }
                    d.loader.add(name, code);
                    try {
                        cc = d.loader.loadClass(name, true);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    d.loaded.put(name, cc);
                    return cc;

                }
            }
        }
        return null;
    }

    private
    void newLoader(final ReloadingDomain d) {
        d.loader = new ChildClassLoader(Trampoline2.trampoline.loader) {
            protected
            boolean should(final String name) {

                //System.out.println(" -- checking to see if we should be able to reload:" + name);

                if (super.should(name)) return true;

                Boolean m = d.matcher.apply(name);
                if (m) {
                    byte[] b = Trampoline2.bytesForClass(Trampoline2.trampoline.getClassLoader(), name);
                    map.put(name, b);
                }
                return false;
            }
        };

    }

    public
    List<ReloadingDomain> getDomains() {
        return domains;
    }

}
