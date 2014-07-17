package field.bytecode.protect.trampoline;

import field.bytecode.protect.analysis.model.SimpleClassModel;
import field.bytecode.protect.analysis.model.SimpleModelBuilder;
import field.bytecode.protect.instrumentation.BasicInstrumentation2;
import field.namespace.generic.ReflectionTools;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class StandardTrampoline extends Trampoline2 {
    private static final Logger log = Logger.getLogger(StandardTrampoline.class.getName());

    public static boolean debug = true;


    HashSet<String> alreadyLoaded = new HashSet<String>();

    Map<String, HandlesAnnontatedMethod> annotatedMethodHandlers = AnnotatedMethodHandlers.getHandlers();

    int inside = 0;


    public StandardTrampoline() {

        BasicInstrumentation2.knownAliasingParameters.add("Lfield/bytecode/protect/annotations/AliasingParameter;");
        BasicInstrumentation2.knownAliasingParameters.add("Lfield/bytecode/protect/annotations/Value;");

    }

    public Annotation[] getAllAnotationsForSuperMethodsOf(String name, String desc, Type[] at, String super_name, String[] interfaces) throws ClassNotFoundException {

        Class[] parameterClasses = new Class[at.length];

        for (int i = 0; i < parameterClasses.length; i++) {
            parameterClasses[i] = getClassFor(at[i].getClassName());
        }

        java.lang.reflect.Method javamethod = ReflectionTools.findMethodWithParametersUpwards(name, parameterClasses, checkedLoadClass(super_name));

        if (javamethod == null) {
            for (String anInterface : interfaces) {
                javamethod = ReflectionTools.findMethodWithParametersUpwards(name, parameterClasses, checkedLoadClass(anInterface));
                if (javamethod != null)
                    break;
            }
            assert javamethod != null : " couldn't find method to inherit from in <" + name + "> with parameters <" + Arrays.asList(parameterClasses) + ">";
        }

        try {
            return javamethod.getAnnotations();
        } catch (Throwable t) {
            t.printStackTrace();
            // System.exit(1);
        }
        return null;

    }

    public Class getClassFor(String className) throws ClassNotFoundException {
        if (className.equals("int")) {
            return Integer.TYPE;
        }
        if (className.equals("float")) {
            return Float.TYPE;
        }
        if (className.equals("double")) {
            return Double.TYPE;
        } else {

            return checkedLoadClass(className);
        }
    }

    /**
     * this is going to get a little intense
     *
     * @throws ClassNotFoundException
     */
    private Class checkedLoadClass(String className) throws ClassNotFoundException {

        className = className.replace('/', '.');
        // if (debug)
        // System.out.println(" looking for <" + className +
        // "> in <" + alreadyLoaded + ">");
        if (alreadyLoaded.contains(className) || !shouldLoadLocal(className)) {
            return loader.loadClass(className);
        }

        // got to make
        // sure that we
        // actually
        // instrument
        // it.
        // URLClassPath path = (URLClassPath) ReflectionTools.illegalGetObject(deferTo, "ucp");
        // Resource resource = path.getResource(className.replace('.', '/').concat(".class"), false);

        InputStream stream = loader.getResourceAsStream(className.replace('.', '/').concat(".class"));
        byte[] bb = new byte[100];
        int cursor = 0;
        try {
            while (stream.available() > 0) {
                int c = stream.read(bb, cursor, bb.length - cursor);
                if (c <= 0)
                    break;
                cursor += c;
                if (cursor > bb.length - 2) {
                    byte[] b2 = new byte[bb.length * 2];
                    System.arraycopy(bb, 0, b2, 0, bb.length);
                    bb = b2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[cursor];
        System.arraycopy(bb, 0, bytes, 0, cursor);

        // if (resource == null)
        // System.err.println(" warning, couldn't find superclass ? :" +
        // className + " " + path + " " + stream);
        // byte[] bytes = null;
        // try {
        // bytes = resource.getBytes();
        // } catch (IOException e1) {
        // e1.printStackTrace();
        // }

        //if (debug)
        // System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  about to renter code with <"
        // + className + "> and <" + bytes.length +
        // ">");
        byte[] o = bytes;

        check();
        bytes = this.instrumentBytecodes(bytes, className, loader);
        check();
        if (debug) {
            // System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< and out again <"
            // + className + "> and <" + bytes.length +
            // "> from <" + o.length + ">");
            FileOutputStream os;
            try {
                os = new FileOutputStream(new File("/var/tmp/old_" + className.replace('.', 'X') + ".class"));
                os.write(o);
                os.close();

                FileOutputStream os2 = new FileOutputStream(new File("/var/tmp/new_" + className.replace('.', 'X') + ".class"));
                os2.write(bytes);
                os2.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // we
            // need
            // to
            // cache
            // annotations
            // for
            // all
            // of
            // the
            // methods
            check();
            Class cc = loader._defineClass(className, bytes, 0, bytes.length);
            check();
            return cc;
            // return
            // cc;
            // return
            // loader.loadClass(className);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        // catch
        // (IllegalAccessException
        // e) {
        // e.printStackTrace();
        // } catch
        // (InvocationTargetException
        // e) {
        // e.printStackTrace();
        // }
        assert false : "failure of grand plan for <" + className + ">";
        return null;
    }

    @Override
    protected byte[] instrumentBytecodes(byte[] a, final String class_name, ClassLoader deferTo) {
        assert !alreadyLoaded.contains(class_name) : " class name is <" + class_name + "> aready";
        alreadyLoaded.add(class_name);
        check();

        long modAt = cache.modificationForURL(deferTo.getResource(resourceNameForClassName(class_name)));
        if (!cache.is(class_name, true, modAt))
            return a;

        // ;//System.out.println(class_name);
        if (class_name.startsWith("java")
                || class_name.startsWith("com/sun")
                || class_name.startsWith("sun")
                || class_name.startsWith("apple")
                || class_name.contains("protect")
                || class_name.startsWith("org.python")
                || class_name.startsWith("ch.")
                || class_name.startsWith("com.")) return a;
        try {
            log.log(Level.INFO, "begin instrumentation of: {0}", class_name);


            SimpleClassModel classModel = SimpleModelBuilder.buildModel(a);


            if (!classModel.isWoven()) {
                cache.state(class_name, false, modAt);
                return a;
            } else {
                cache.state(class_name, true, modAt);
            }

            check();
            a = weave(a, class_name, deferTo, classModel.superName, classModel.interfaces);
            check();

//            if (debug) {
//                FileOutputStream os2 = new FileOutputStream(new File("/var/tmp/woven_" + class_name.replace('.', 'X') + ".class"));
//                os2.write(a);
//                os2.close();
//            }

            return a;
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        return a;
    }

    protected byte[] weave(byte[] oa, final String class_name, ClassLoader deferTo, final String superName, final String[] interfaces) {
        inside++;
        check();

        //if (StandardTrampoline.debug)
        // System.out.println(" -- weaving <" + class_name +
        // "> <" + inside + ">");
        try {
            final boolean[] isInterface = {false};
            {
                check();
                // phase
                // one
                // \u2014
                // deal
                // with
                // the
                // inheritwoven
                // tag
                ClassReader reader = new ClassReader(oa);
                ClassWriter writer = new MyClassWriter();
                // final byte[] fa = oa;
                ClassVisitor adaptor = new ClassVisitor(Opcodes.ASM5, writer) {
                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        super.visit(version, access, name, signature, superName, interfaces);
                        isInterface[0] = (access & Opcodes.ACC_INTERFACE) != 0;
                    }

                    @Override
                    public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature, String[] exceptions) {
                        //  final MethodVisitor m = cv.visitMethod(access, name, desc, signature, exceptions);
                        return new InheritWovenMethodAdaptor(StandardTrampoline.this, name, desc, superName, interfaces);//.setDelegate(m);
                    }

                };

                check();
                // System.out.println(" A ");
                reader.accept(adaptor, 0);
                // System.out.println(" B ");
                check();
                oa = writer.toByteArray();
                check();

            }
            if (!isInterface[0]) {
                // phase
                // two
                // \u2014
                // deal
                // with
                // other
                // tages
                check();
                ClassReader reader = new ClassReader(oa);


                if (debug)
                    log.log(Level.INFO, " ------- before instrumentation -----------------------------------------------------------------------------------");
                try {
                    if (debug)
                        checkClass(oa);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final byte[] fa = oa;
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                CheckClassAdapter check = new CheckClassAdapter(writer);
                // final List<TraceMethodVisitor> traceMethods = new ArrayList<TraceMethodVisitor>();
                ClassVisitor adaptor = new ClassVisitor(Opcodes.ASM5, debug ? check : writer) {

                    @Override
                    public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature, String[] exceptions) {
                        final MethodVisitor m = super.visitMethod(access, name, desc, signature, exceptions);
                        return new AnnotationMethodAdaptor(annotatedMethodHandlers, access, name, desc, signature, cv, m, superName, fa, class_name);
                    }
                };
                // check();
                reader.accept(adaptor, ClassReader.EXPAND_FRAMES);
                //check();
                oa = writer.toByteArray();
                // check();


                if (debug)
                    log.log(Level.INFO, " ------- after instrumentation -----------------------------------------------------------------------------------");
                try {
                    checkClass(oa);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // check();
            }
            if (debug) {
                check();
                ClassReader reader = new ClassReader(oa);
                StringWriter sw = new StringWriter();
                reader.accept(new TraceClassVisitor(new PrintWriter(sw)), ClassReader.EXPAND_FRAMES);
                log.info(sw.toString());
                check();
            }
        } catch (Throwable r) {
            r.printStackTrace();
        }
        inside--;
        return oa;
    }

    public static void checkClass(byte[] aa) throws Exception {
        StringWriter sw = new StringWriter();
        CheckClassAdapter.verify(new ClassReader(aa), false, new PrintWriter(sw));
        if (!sw.toString().isEmpty()) {
            log.log(Level.SEVERE, sw.toString());
            throw new RuntimeException(sw.toString());
        }
//		if (true)
//			return;
        // ClassReader cr = new ClassReader(aa);
        //
        // ClassNode cn = new ClassNode();
        // cr.accept(new CheckClassAdapter(cn), true);
        //
        // List methods = cn.methods;
        // for (int i = 0; i < methods.size(); ++i) {
        // MethodNode method = (MethodNode) methods.get(i);
        // if (method.instructions.size() > 0) {
        // Analyzer a = new Analyzer(new SimpleVerifier(Type.getType("L"
        // + cn.name + ";"), Type.getType("L" + cn.superName + ";"),
        // (cn.access & Opcodes.ACC_INTERFACE) != 0));
        // try {
        // a.analyze(cn.name, method);
        // // continue;
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // final Frame[] frames = a.getFrames();
        //
        // if (StandardTrampoline.debug)
        // ;//System.out.println(method.name + method.desc);
        // TraceMethodVisitor mv = new TraceMethodVisitor() {
        // @Override
        // public void visitLocalVariable(String name, String desc,
        // String signature, Label start, Label end, int index) {
        // super.visitLocalVariable(name, desc, signature, start, end,
        // index);
        // }
        //
        // @Override
        // public void visitMaxs(final int maxStack, final int
        // maxLocals) {
        // for (int i = 0; i < text.size(); ++i) {
        // String s = frames[i] == null ? "null" : frames[i].toString();
        // while (s.length() < maxStack + maxLocals + 1) {
        // s += " ";
        // }
        // System.out.print(Integer.toString(i + 100000).substring(1));
        // System.out.print(" " + s + " : " + text.get(i));
        // }
        // if (StandardTrampoline.debug)
        // ;//System.out.println();
        // }
        // };
        // for (int j = 0; j < method.instructions.size(); ++j) {
        // ((AbstractInsnNode) method.instructions.get(j)).accept(mv);
        // }
        // mv.visitMaxs(method.maxStack, method.maxLocals);
        // }
        // }
    }

    private class MyClassWriter extends ClassWriter {
        public MyClassWriter() {
            super(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }

        protected String getCommonSuperClass(final String type1, final String type2) {
            ClassInfo c, d;
            try {
                c = new ClassInfo(type1, Trampoline2.trampoline.getClassLoader());
                d = new ClassInfo(type2, Trampoline2.trampoline.getClassLoader());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            if (c.isAssignableFrom(d)) {
                return type1;
            }
            if (d.isAssignableFrom(c)) {
                return type2;
            }
            if (c.isInterface() || d.isInterface()) {
                return "java/lang/Object";
            } else {
                do {
                    c = c.getSuperclass();
                } while (!c.isAssignableFrom(d));
                return c.getType().getInternalName();
            }
        }

        class ClassInfo {

            private Type type;

            private ClassLoader loader;

            int access;

            String superClass;

            String[] interfaces;

            public ClassInfo(final String type, final ClassLoader loader) {
                this.loader = loader;
                this.type = Type.getObjectType(type);
                String s = type.replace('.', '/') + ".class";
                InputStream is = null;
                ClassReader cr;
                try {
                    is = loader.getResourceAsStream(s);
                    cr = new ClassReader(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ignored) {
                        }
                    }
                }

                // optimized version
                int h = cr.header;
                ClassInfo.this.access = cr.readUnsignedShort(h);
                char[] buf = new char[2048];
                // String name =
                // cr.readClass(
                // cr.header + 2, buf);

                try {
                    int v = cr.getItem(cr.readUnsignedShort(h + 4));
                    ClassInfo.this.superClass = v == 0 ? null : cr.readUTF8(v, buf);
                    ClassInfo.this.interfaces = new String[cr.readUnsignedShort(h + 6)];
                    h += 8;
                    for (int i = 0; i < interfaces.length; ++i) {
                        interfaces[i] = cr.readClass(h, buf);
                        h += 2;
                    }
                } catch (ArrayIndexOutOfBoundsException a) {
                    System.out.println(" -- skipping interface description for <" + type + ">");
                    ClassInfo.this.superClass = null;
                    ClassInfo.this.interfaces = new String[0];
                }
            }

            String getName() {
                return type.getInternalName();
            }

            Type getType() {
                return type;
            }

            int getModifiers() {
                return access;
            }

            ClassInfo getSuperclass() {
                if (superClass == null) {
                    return null;
                }
                return new ClassInfo(superClass, loader);
            }

            ClassInfo[] getInterfaces() {
                if (interfaces == null) {
                    return new ClassInfo[0];
                }
                ClassInfo[] result = new ClassInfo[interfaces.length];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = new ClassInfo(interfaces[i], loader);
                }
                return result;
            }

            boolean isInterface() {
                return (getModifiers() & Opcodes.ACC_INTERFACE) > 0;
            }

            private boolean implementsInterface(final ClassInfo that) {
                for (ClassInfo c = this; c != null; c = c.getSuperclass()) {
                    ClassInfo[] tis = c.getInterfaces();
                    for (ClassInfo ti : tis) {
                        if (ti.type.equals(that.type) || ti.implementsInterface(that)) return true;
                    }
                }
                return false;
            }

            private boolean isSubclassOf(final ClassInfo that) {
                for (ClassInfo c = this; c != null; c = c.getSuperclass()) {
                    if (c.getSuperclass() != null && c.getSuperclass().type.equals(that.type)) {
                        return true;
                    }
                }
                return false;
            }

            public boolean isAssignableFrom(final ClassInfo that) {
                return this == that
                        || that.isSubclassOf(this)
                        || that.implementsInterface(this)
                        || (that.isInterface() && getType().getDescriptor().equals("Ljava/lang/Object;"));

            }
        }

    }
}
