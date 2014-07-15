package field.apt;

import sun.tools.java.ClassDeclaration;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

//import com.sun.mirror.apt.AnnotationProcessor;
//import com.sun.mirror.apt.AnnotationProcessorEnvironment;
//import com.sun.mirror.apt.AnnotationProcessorFactory;
//import com.sun.mirror.apt.AnnotationProcessors;
//import com.sun.mirror.declaration.AnnotationMirror;
//import com.sun.mirror.declaration.AnnotationTypeDeclaration;
//import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
//import com.sun.mirror.declaration.AnnotationValue;
//import com.sun.mirror.declaration.ClassDeclaration;
//import com.sun.mirror.declaration.Declaration;
//import com.sun.mirror.declaration.FieldDeclaration;
//import com.sun.mirror.declaration.InterfaceDeclaration;
//import com.sun.mirror.declaration.MethodDeclaration;
//import com.sun.mirror.declaration.ParameterDeclaration;
//import com.sun.mirror.declaration.TypeParameterDeclaration;
//import com.sun.mirror.type.InterfaceType;
//import com.sun.mirror.type.PrimitiveType;
//import com.sun.mirror.type.TypeMirror;
//import com.sun.mirror.type.PrimitiveType.Kind;
//import com.sun.mirror.util.SimpleTypeVisitor;

public class GenMethodsFactory  {

	static final List<String> sup = Collections.singletonList("field.bytecode.protect.annotations.GenerateMethods");

	public Collection<String> supportedOptions() {
		return Collections.EMPTY_LIST;
	}

	public Collection<String> supportedAnnotationTypes() {
		return sup;
	}

	String defaultPrefix = "";

	String prefix = "";

	public AnnotationProcessor getProcessorFor(final Set<AnnotationTypeDeclaration> annotations, final AnnotationProcessorEnvironment env) {
		if (annotations.size() == 0) return AnnotationProcessors.NO_OP;

		return new AnnotationProcessor(){
			public void process() {
				if (annotations.size() == 0) return;

				Collection<Declaration> decls = env.getDeclarationsAnnotatedWith(annotations.iterator().next());

				prefix = "";
				defaultPrefix = "";

				for (Declaration d : decls) {

					if (d instanceof ClassDeclaration) {

						Collection<AnnotationMirror> annotations = ((ClassDeclaration) d).getAnnotationMirrors();
						for (AnnotationMirror am : annotations) {
							if (am.getAnnotationType().getDeclaration().getSimpleName().equals("GenerateMethods")) {
								defaultPrefix = getPrefixFor(am);
								if (!defaultPrefix.equals("")) env.getMessager().printNotice("default prefix set to be <" + defaultPrefix + ">");
							}
						}

						try {
							PrintWriter out = env.getFiler().createSourceFile(((ClassDeclaration) d).getQualifiedName() + "_m");

							// preamble
							out.println("package " + ((ClassDeclaration) d).getPackage() + ";");
							out.println("import java.lang.reflect.Method;\n" + "import field.namespace.generic.ReflectionTools;\n" + "import field.bytecode.apt.*;\n");
							out.println("import field.namespace.generic.Bind.*;\n");
							out.println("import field.namespace.generic.Bind.iFunction;\n");
							out.println("import java.lang.reflect.*;\n");
							out.println("import java.util.*;\n");
							out.println("import field.math.abstraction.*;\n");
							out.println("import field.launch.*;\n");
							out.println("import " + d.toString() + ";\n");

							StringWriter defOut = new StringWriter();
							PrintWriter deferedOut = new PrintWriter(defOut);

							StringWriter conOut = new StringWriter();
							PrintWriter construtorOut = new PrintWriter(conOut);

							deferedOut.println("public class " + ((ClassDeclaration) d).getSimpleName() + "_m {");

							// for each member that is decorated with @Mirror

							Collection<FieldDeclaration> fields = ((ClassDeclaration) d).getFields();

							for (FieldDeclaration f : fields) {
								Collection<AnnotationMirror> am = f.getAnnotationMirrors();
								for (AnnotationMirror a : am) {
									if (a.getAnnotationType().getDeclaration().getSimpleName().equals("Mirror")) {
										prefix = getPrefixFor(a);
										emitForField(f, a, out, deferedOut, construtorOut, env);
									}
								}
							}

							Collection<MethodDeclaration> methods = ((ClassDeclaration) d).getMethods();
							for (MethodDeclaration m : methods) {
								Collection<AnnotationMirror> am = m.getAnnotationMirrors();
								for (AnnotationMirror a : am) {
									if (a.getAnnotationType().getDeclaration().getSimpleName().equals("Mirror")) {
										prefix = getPrefixFor(a);
										emitForMethod(m, a, out, deferedOut, construtorOut, env);
									}
								}
							}

							deferedOut.println("public " + ((ClassDeclaration) d).getSimpleName() + "_m(final " + ((ClassDeclaration) d).getSimpleName() + " x) {");
							deferedOut.println(conOut.getBuffer());
							deferedOut.println("}");

							// deferedOut.println("public " + ((InterfaceDeclaration) d).getSimpleName() + "_m(final Collection<" + ((InterfaceDeclaration) d).getSimpleName() + "> x) {");
							// deferedOut.println(conOut.getBuffer());
							// deferedOut.println("}");
							deferedOut.println("}");

							out.println(defOut.getBuffer());

							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					if (d instanceof InterfaceDeclaration) {

						Collection<AnnotationMirror> annotations = ((InterfaceDeclaration) d).getAnnotationMirrors();
						for (AnnotationMirror am : annotations) {
							if (am.getAnnotationType().getDeclaration().getSimpleName().equals("GenerateMethods")) {
								defaultPrefix = getPrefixFor(am);
								if (!defaultPrefix.equals("")) env.getMessager().printNotice("default prefix set to be <" + defaultPrefix + ">");
							}
						}

						try {
							PrintWriter out = env.getFiler().createSourceFile(((InterfaceDeclaration) d).getQualifiedName() + "_m");

							// preamble
							out.println("package " + ((InterfaceDeclaration) d).getPackage() + ";");
							out.println("import java.lang.reflect.Method;\n" + "import field.namespace.generic.ReflectionTools;\n" + "import field.bytecode.apt.*;\n");
							out.println("import field.namespace.generic.Bind.*;\n");
							out.println("import field.namespace.generic.Bind.iFunction;\n");
							out.println("import java.lang.reflect.*;\n");
							out.println("import java.util.*;\n");
							out.println("import field.math.abstraction.*;\n");
							out.println("import field.launch.*;\n");
							out.println("import " + d.toString() + ";\n");

							StringWriter defOut = new StringWriter();
							PrintWriter deferedOut = new PrintWriter(defOut);

							StringWriter conOut = new StringWriter();
							PrintWriter construtorOut = new PrintWriter(conOut);

							deferedOut.println("public class " + ((InterfaceDeclaration) d).getSimpleName() + "_m {");

							// for each member that is decorated with @Mirror

							Collection<FieldDeclaration> fields = ((InterfaceDeclaration) d).getFields();

							for (FieldDeclaration f : fields) {
								Collection<AnnotationMirror> am = f.getAnnotationMirrors();
								for (AnnotationMirror a : am) {
									if (a.getAnnotationType().getDeclaration().getSimpleName().equals("Mirror")) {
										prefix = getPrefixFor(a);
										emitForField(f, a, out, deferedOut, construtorOut, env);
									}
								}
							}

							Collection<MethodDeclaration> methods = (Collection<MethodDeclaration>) ((InterfaceDeclaration) d).getMethods();
							for (MethodDeclaration m : methods) {
								Collection<AnnotationMirror> am = m.getAnnotationMirrors();
								for (AnnotationMirror a : am) {
									if (a.getAnnotationType().getDeclaration().getSimpleName().equals("Mirror")) {
										prefix = getPrefixFor(a);
										emitForMethod(m, a, out, deferedOut, construtorOut, env);
									}
								}
							}

							deferedOut.println("public " + ((InterfaceDeclaration) d).getSimpleName() + "_m(final " + ((InterfaceDeclaration) d).getSimpleName() + " x) {");
							deferedOut.println(conOut.getBuffer());
							deferedOut.println("}");

							// deferedOut.println("public " + ((InterfaceDeclaration) d).getSimpleName() + "_m(final Collection<" + ((InterfaceDeclaration) d).getSimpleName() + "> x) {");
							// deferedOut.println(conOut.getBuffer());
							// deferedOut.println("}");

							deferedOut.println("}");

							out.println(defOut.getBuffer());

							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}
		};
	}

	protected String getPrefixFor(AnnotationMirror am) {
		for (Entry<AnnotationTypeElementDeclaration, AnnotationValue> e : am.getElementValues().entrySet()) {
			if (e.getKey().getSimpleName().equals("prefix")) { return (String) e.getValue().getValue(); }
		}
		return defaultPrefix;
	}

	protected void emitForField(FieldDeclaration f, AnnotationMirror a, PrintWriter out, PrintWriter deferedOut, PrintWriter construtorOut, final AnnotationProcessorEnvironment env) {

		String type = f.getType().toString();
		String name = f.getSimpleName();
		String inside = f.getDeclaringType().toString();

		final boolean isPrimative = isPrimative(f.getType());

		// assuming "Mirror" at the moment

		if (!isPrimative) out.println("import " + type + ";\n");
		out.println("import java.lang.reflect.*;\n");

		if (!isPrimative) {

			deferedOut.println("static public final " + prefix + "Mirroring.MirrorMember<" + inside + ", " + type + "> " + name + "_s = new " + prefix + "Mirroring.MirrorMember<" + inside + ", " + type + ">(" + inside + ".class,\"" + name + "\", " + type + ".class);");

			boolean isInterface = isInterface(f.getType());

			if (isInterface) {
				deferedOut.println("public interface Union_" + safe(type) + " extends Mirroring.iBoundMember<" + type + ">,  " + type + "\n" + "	{\n" + "	}\n" + "	");
				deferedOut.println("public final Union_" + safe(type) + " " + name + ";");

				construtorOut.println("{final Mirroring.iBoundMember<" + type + "> bound = " + name + "_s.boundMember(x);\n" + "		" + name + " = (Union_" + safe(type) + ") Proxy.newProxyInstance(x.getClass().getClassLoader(), new Class[] { Union_" + safe(stripGenerics(type)) + ".class}, new InvocationHandler(){\n"
					+ "			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {\n" + "\n" + "				if (method.getName().equals(\"get\") && args.length == 0) return bound.get();\n" + "				if (method.getName().equals(\"set\") && args.length == 1) return bound.set((" + type + ")args[0]);\n"
					+ "\n" + "				return method.invoke(bound.get(), args);\n" + "			}\n" + "		});}");
			} else {
				deferedOut.println("public final Mirroring.iBoundMember<" + type + "> " + name + ";");

				construtorOut.println(name + " = " + name + "_s.boundMember(x);");
			}
		} 
		else  if (isFloat(f.getType()))
		{

			deferedOut.println("static public final " + prefix + "Mirroring.MirrorFloatMember<" + inside + "> " + name + "_s = new " + prefix + "Mirroring.MirrorFloatMember<" + inside + ">(" + inside + ".class,\"" + name + "\");");
			deferedOut.println("public final Mirroring.iBoundFloatMember " + name + ";");

			construtorOut.println(name + " = " + name + "_s.boundMember(x);");
		}
		else  if (isDouble(f.getType()))
		{

			deferedOut.println("static public final " + prefix + "Mirroring.MirrorDoubleMember<" + inside + "> " + name + "_s = new " + prefix + "Mirroring.MirrorDoubleMember<" + inside + ">(" + inside + ".class,\"" + name + "\");");
			deferedOut.println("public final Mirroring.iBoundDoubleMember " + name + ";");

			construtorOut.println(name + " = " + name + "_s.boundMember(x);");
		}
	}

	private boolean isInterface(TypeMirror type) {

		final boolean[] isInterface = { false};
		type.accept(new SimpleTypeVisitor(){
			public void visitInterfaceType(InterfaceType arg0) {
				isInterface[0] = true;
			}
		});
		return isInterface[0];
	}

	private boolean isPrimative(TypeMirror type) {
		return isFloat(type) || isDouble(type);
	}

	private boolean isFloat(TypeMirror type) {
		final boolean[] isPrimative = { false};
		type.accept(new SimpleTypeVisitor(){
			public void visitPrimitiveType(PrimitiveType arg0) {
				isPrimative[0] = arg0.getKind() == Kind.FLOAT;

				//assert arg0.getKind() == Kind.FLOAT : "unhandled primative type <" + arg0.getKind() + "> not implemented";
			}
		});
		return isPrimative[0];
	}

	private boolean isDouble(TypeMirror type) {
		final boolean[] isPrimative = { false};
		type.accept(new SimpleTypeVisitor(){
			public void visitPrimitiveType(PrimitiveType arg0) {
				isPrimative[0] = arg0.getKind() == Kind.DOUBLE;

				//assert arg0.getKind() == Kind.FLOAT : "unhandled primative type <" + arg0.getKind() + "> not implemented";
			}
		});
		return isPrimative[0];
	}

	private String safe(String type) {
		return type.replaceAll("\\.", "_");
	}

	protected void emitForMethod(MethodDeclaration m, AnnotationMirror a, PrintWriter out, PrintWriter deferedOut, PrintWriter construtorOut, AnnotationProcessorEnvironment env) {

		String name = m.getSimpleName();
		String inside = m.getDeclaringType().toString();
		String returnType = m.getReturnType().toString();

		Collection<TypeParameterDeclaration> formalTypeParameters = m.getFormalTypeParameters();

		List<String> matchingNames = new ArrayList<String>();
		for (TypeParameterDeclaration d : formalTypeParameters) {
			matchingNames.add(d.getSimpleName());
		}

		String typeForGenericDecl = null;
		String typesForConstructor = null;
		String typesForDeclaration = null;
		String typesForInvocation = null;
		int pnum = 0;
		Collection<ParameterDeclaration> parameters = m.getParameters();
		for (ParameterDeclaration p : parameters) {
			String q = p.getType().toString();
			q = filterTypeForGenerics(q, matchingNames, env);
			env.getMessager().printNotice(" q now <" + q + ">");

			if (typeForGenericDecl == null)
				typeForGenericDecl = boxPrimative(p.getType());
			else if (typeForGenericDecl.equals(q))
				typeForGenericDecl = typeForGenericDecl + "[]";
			else
				typeForGenericDecl = "Object[]";

			if (typesForConstructor == null) {
				typesForConstructor = stripGenerics(q) + ".class";
				if (!isPrimative(p.getType()) && q.contains(".")) out.println("import " + stripGenerics(q) + ";");
			} else {
				typesForConstructor = typesForConstructor + ", " + stripGenerics(q) + ".class";
				if (!isPrimative(p.getType()) && q.contains(".")) out.println("import " + stripGenerics(q) + ";");
			}

			if (typesForDeclaration == null) {
				typesForDeclaration = "final " + stripGenerics(q) + " p" + (pnum);
			} else {
				typesForDeclaration = typesForDeclaration + ", final " + stripGenerics(q) + " p" + pnum;
			}

			if (typesForInvocation == null) {
				typesForInvocation = "p" + pnum;
			} else {
				typesForInvocation = typesForInvocation + ", p" + pnum;
			}

			env.getMessager().printNotice(" types for constructor now <" + typesForConstructor + ">");

			pnum++;
		}

		if (typesForConstructor == null) {
			deferedOut.println("static public final Method " + name + "_m = ReflectionTools.methodOf(\"" + name + "\", " + inside + ".class);");
		} else {
			deferedOut.println("static public final Method " + name + "_m = ReflectionTools.methodOf(\"" + name + "\", " + inside + ".class, " + typesForConstructor + ");");
		}

		if (typeForGenericDecl == null) {
			if (returnType.equals("void")) {
				deferedOut.println("static public final " + prefix + "Mirroring.MirrorNoReturnNoArgsMethod<" + inside + "> " + name + "_s = new " + prefix + "Mirroring.MirrorNoReturnNoArgsMethod<" + inside + ">(" + inside + ".class, \"" + name + "\");\n");

				deferedOut.println("public final iUpdateable " + name + ";");
				construtorOut.println(name + " = " + name + "_s.updateable(x);");

			} else {
				deferedOut.println("static public final " + prefix + "Mirroring.MirrorNoArgsMethod<" + inside + ", " + boxPrimative(m.getReturnType()) + "> " + name + "_s = new " + prefix + "Mirroring.MirrorNoArgsMethod<" + inside + ", " + boxPrimative(m.getReturnType()) + ">(" + inside + ".class, \"" + name + "\");\n");

				boolean isInterface = isInterface(m.getReturnType());
				if (!isInterface) {
					deferedOut.println("public final Mirroring.iBoundNoArgsMethod<" + boxPrimative(m.getReturnType()) + "> " + name + ";");
					construtorOut.println(name + " = " + name + "_s.bind(x);");

				} else {

					deferedOut.println("public interface " + name + "_interface extends " + returnType + ", Mirroring.iBoundNoArgsMethod<" + returnType + ">	{\n" + "		public " + returnType + " " + name + "( );\n" + "	}\n");

					deferedOut.println("public final " + name + "_interface " + name + ";\n");

					construtorOut.println("{final Mirroring.iBoundNoArgsMethod<" + returnType + "> bound = " + name + "_s.bind(x);\n" + "		" + name + " = (" + name + "_interface) Proxy.newProxyInstance(x.getClass().getClassLoader(), new Class[] { " + stripGenerics(name)
						+ "_interface.class}, new InvocationHandler(){\n" + "			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {\n" + "\n" + "				if (method.getName().equals(\"get\") && args.length == 0) return bound.get();\n" + "\n"
						+ "				return method.invoke(bound.get(), args);\n" + "			}\n" + "		});}");
				}

			}
		} else {
			if (returnType.equals("void")) {
				deferedOut.println("static public final " + prefix + "Mirroring.MirrorNoReturnMethod<" + inside + ", " + typeForGenericDecl + "> " + name + "_s = new " + prefix + "Mirroring.MirrorNoReturnMethod<" + inside + ", " + typeForGenericDecl + ">(" + inside + ".class, \"" + name + "\", new Class[]{" + typesForConstructor
					+ "});\n");

				deferedOut.println("public interface " + name + "_interface extends iAcceptor<" + typeForGenericDecl + ">, iFunction<" + (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + " ," + typeForGenericDecl + " >\n" + "	{\n" + "		public " + returnType + " " + name + "( " + typesForDeclaration
					+ ");\n" + "	public iUpdateable updateable(" + typesForDeclaration + ");}\n");

				deferedOut.println("public final " + name + "_interface " + name + ";\n");

				construtorOut.println("		" + name + " = new " + name + "_interface()\n" + "		{\n" + "			\n" + "			iAcceptor a = " + name + "_s.acceptor(x);\n" + "			iFunction f = " + name + "_s.function(x);\n" + "\n" + "			\n" + "			public " + returnType + " " + name + " (" + typesForDeclaration + ")\n" + "			{\n" + "				"
					+ (returnType.equals("void") ? "" : "return") + " x." + name + "(" + typesForInvocation + " );\n" + "			}\n" + "			\n" + "			public iAcceptor<" + typeForGenericDecl + "> set(" + typeForGenericDecl + " p)\n" + "			{\n" + "				a.set(p);\n" + "				return this;\n" + "			}\n" + "			\n" + "			public "
					+ (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + " f(" + typeForGenericDecl + " p)\n" + "			{\n" + "				return (" + (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + ") f.f(p);\n" + "			}\n" + "			\n" + "		public iUpdateable updateable("
					+ typesForDeclaration + ")\n" + "	{\n" + "		return new iUpdateable()\n" + "		{\n" + "			public void update()\n" + "			{\n" + "				" + name + "(" + typesForInvocation + ");\n" + "			}\n" + "		};\n" + "	}\n" + "		};\n");

			} else {
				deferedOut.println("static public final " + prefix + "Mirroring.MirrorMethod<" + inside + ", " + boxPrimative(m.getReturnType()) + ", " + typeForGenericDecl + "> " + name + "_s = new " + prefix + "Mirroring.MirrorMethod<" + inside + ", " + boxPrimative(m.getReturnType()) + ", " + typeForGenericDecl + ">(" + inside
					+ ".class, \"" + name + "\", new Class[]{" + typesForConstructor + "});\n");

				deferedOut.println("public interface " + name + "_interface extends iAcceptor<" + typeForGenericDecl + ">, iFunction<" + (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + " ," + typeForGenericDecl + " >\n" + "	{\n" + "		public " + returnType + " " + name + "( " + typesForDeclaration
					+ ");\n" + "	public iUpdateable updateable(" + typesForDeclaration + ");\n" +
							"public iProvider<"+boxPrimative(m.getReturnType())+"> bind("+typesForDeclaration+");\n"+
							"}\n");

				deferedOut.println("public final " + name + "_interface " + name + ";\n");

				construtorOut.println("		" + name + " = new " + name + "_interface()\n" + "		{\n" + "			\n" + "			iAcceptor a = " + name + "_s.acceptor(x);\n" + "			iFunction f = " + name + "_s.function(x);\n" + "\n" + "			\n" + "			public " + returnType + " " + name + " (" + typesForDeclaration + ")\n" + "			{\n" + "				"
					+ (returnType.equals("void") ? "" : "return") + " x." + name + "(" + typesForInvocation + " );\n" + "			}\n" + "			\n" + "			public iAcceptor<" + typeForGenericDecl + "> set(" + typeForGenericDecl + " p)\n" + "			{\n" + "				a.set(p);\n" + "				return this;\n" + "			}\n" + "			\n" + "			public "
					+ (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + " f(" + typeForGenericDecl + " p)\n" + "			{\n" + "				return (" + (returnType.equals("void") ? "Object" : boxPrimative(m.getReturnType())) + ") f.f(p);\n" + "			}\n" + "			\n" + "		public iUpdateable updateable("
					+ typesForDeclaration + ")\n" + "	{\n" + "		return new iUpdateable()\n" + "		{\n" + "			public void update()\n" + "			{\n" + "				" + name + "(" + typesForInvocation + ");\n" + "			}\n" + "		};\n" + "	}\n" + "	" +
							"public iProvider<" +boxPrimative(m.getReturnType())+"> bind("+typesForDeclaration+"){\n" +
									"return new iProvider(){public Object get(){return "+name+"("+typesForInvocation+");}};}};\n");

			}

		}

	}

	private String filterTypeForGenerics(String q, List<String> matchingNames, AnnotationProcessorEnvironment env) {

		env.getMessager().printWarning(" checking <" + q + "> against <" + matchingNames + ">");

		for (String s : matchingNames) {
			if (s.equals(q)) return "Object";
		}
		env.getMessager().printWarning(" found nothing");
		return q;
	}

	protected String stripGenerics(String q) {
		if (q.indexOf('<') == -1) return q;
		return q.substring(0, q.indexOf('<'));
	}

	private String boxPrimative(TypeMirror type) {
		final PrimitiveType[] isPrimative = { null};
		type.accept(new SimpleTypeVisitor(){
			public void visitPrimitiveType(PrimitiveType arg0) {
				isPrimative[0] = arg0;
			}
		});
		if (isPrimative[0] == null) {
			// we need to remove any (non terminal?) generic's description from this

			String name = type.toString();

			String[] elements = name.split("[\\<\\>]");

			if (elements.length == 0) return name;

			String n = "";

			for (int i = 0; i < (elements.length + 1) / 2; i++) {
				n += elements[i * 2 + 0];
			}
			return n;
		}

		switch (isPrimative[0].getKind()) {
		case BOOLEAN:
			return "Boolean";
		case BYTE:
			return "Byte";
		case CHAR:
			return "Character";
		case INT:
			return "Integer";
		case FLOAT:
			return "Float";
		case DOUBLE:
			return "Double";
		case LONG:
			return "Long";
		case SHORT:
			return "Short";
		}

		assert false : isPrimative[0].getKind();
		return "";
	}


}
