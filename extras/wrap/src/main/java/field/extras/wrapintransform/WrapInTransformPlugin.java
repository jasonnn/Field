package field.extras.wrapintransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.Ref;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.generic.IFunction;
import field.util.collect.tuple.Pair;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyModule;
import org.python.core.PyObject;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElementProperty;
import field.core.execution.BasicRunner;
import field.core.execution.PythonInterface;
import field.core.execution.IExecutesPromise;
import field.core.plugins.BaseSimplePlugin;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.ui.SmallMenu;
import field.core.ui.text.BaseTextEditor2;
import field.core.ui.text.BaseTextEditor2.Completion;
import field.core.ui.text.PythonTextEditor;
import field.core.ui.text.PythonTextEditor.EditorExecutionInterface;
import field.core.ui.text.embedded.MinimalTextField_blockMenu;
import field.launch.Launcher;
import field.launch.IUpdateable;


public class WrapInTransformPlugin extends BaseSimplePlugin {

	static public final VisualElementProperty<String> wrapInTransform = new VisualElementProperty<String>("wrapInTransform");

	public class LocalOver extends DefaultOverride {

		@Override
		public <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
			if (prop.equals(PythonPluginEditor.python_customToolbar)) {
				ArrayList<Pair<String, IUpdateable>> r = (ArrayList<Pair<String, IUpdateable>>) ref.get();
				if (r == null) {
					r = new ArrayList<Pair<String, IUpdateable>>();
				}
				addElementsTo(source, r);
				ref.set((T) r);
			} else if (prop.equals(PythonPluginEditor.editorExecutionInterface)) {
				if (needsWrap(source)) {
					ref.set((T) getEditorExecutionInterface(source, (EditorExecutionInterface) ref.get()));
				}
			} else if (prop.equals(PythonPlugin.python_sourceFilter)) {
				ref.set((T) sourceFilter(source));
			}

			return super.getProperty(source, prop, ref);
		}

	}

	private BasicRunner runner;

	@Override
	protected String getPluginNameImpl() {
		return "wrapintransformplugin";
	}

	public
    IFunction<String, String> sourceFilter(final IVisualElement source) {
		return new IFunction<String, String>() {

			public String apply(String in) {

				String wrap = source.getProperty(wrapInTransform);
				if (wrap == null)
					return in;
				if (in.startsWith(wrap) && in.endsWith("globals())"))
					return in;

				if (wrap == null)
					return in;
				else {
					in = wrap + "(_self,r\"\"\"\n" + in + "\n\"\"\", globals())";
				}
				return in;
			}
		};
	}

	public IFunction<String,Collection<Completion>> completionHook(final IVisualElement source, final BaseTextEditor2 inside) {
		return new IFunction<String,Collection<Completion>>() {

			public Collection<Completion> apply(final String in) {

				String wrap = source.getProperty(wrapInTransform);
				if (wrap == null)
					return null;

				Object w = PythonInterface.getPythonInterface().getVariables().get(wrap);
				if (w == null)
					return null;

				if (w instanceof PyObject) {
					PyObject c = ((PyObject) w).__findattr__("completions");
					System.out.println("stage 1 :" + c);

					if (c instanceof PyFunction) {
						PyObject c2 = ((PyFunction) c).__call__(Py.java2py(in));
						System.out.println("stage 2 :" + c2);

						if (c2 instanceof PyList) {
							List<Completion> ccc = new ArrayList<Completion>();
							for (int i = 0; i < ((PyList) c2).size(); i++) {
								final String m = ((PyList) c2).get(i).toString();

								Completion comp =new BaseTextEditor2.Completion() {
									@Override
									public void update() {
										System.out.println(" inserting <" + m + ">");
										String mm = m;
										if (mm.contains(" "))
											mm = mm.substring(0, mm.indexOf(" "));
										String r = in;
										int overlap = 0;
										for (int i = 1; i < mm.length(); i++) {
											if (r.endsWith(mm.substring(0, i))) {
												overlap = i;
											}
										}
										mm = mm.substring(overlap);
										inside.getInputEditor().insert(mm);
										inside.getInputEditor().setCaretOffset(inside.getInputEditor().getCaretOffset() + mm.length());
									};
								};

								if (m.indexOf("\n") != -1) {
									comp.text = m;
									comp.isDocumentation = true;
								} else {
									comp.text = m;
									comp.enabled = true;
								}
								ccc.add(comp);
							}
							return ccc;
						}

					}

				}
				return null;
			}
		};
	}

	public boolean needsWrap(IVisualElement source) {
		return source.getProperty(wrapInTransform) != null;
	}

	private IExecutesPromise ep;

	public void addElementsTo(final IVisualElement source, ArrayList<Pair<String, IUpdateable>> r) {
		String wrapIn = wrapInTransform.get(source);
		if (wrapIn == null) {
			wrapIn = "No transformation";
		}
		if (wrapIn.equals("defaultTransform"))
			wrapIn = "No transformation";

		wrapIn = "Transform '" + wrapIn + "'";
		r.add(new Pair<String, IUpdateable>(wrapIn, new IUpdateable() {
			public void update() {

				LinkedHashMap<String, IUpdateable> items = new LinkedHashMap<String, IUpdateable>();
				items.put("Available transforms, from TextTransforms.*", null);

				PyModule q = (PyModule) PythonInterface.getPythonInterface().getVariable("TextTransforms");
				List l = (List) q.__dir__();
				for (int i = 0; i < l.size(); i++) {
					final String name = (String) l.get(i);
					if (!name.startsWith("__")) {

						try {
							PyObject doc = (PyObject) PythonInterface.getPythonInterface().eval("TextTransforms." + name);
							String d = (String) doc.__getattr__("__doc__").__tojava__(String.class);

							if (d.length() > 0)

								d = "" + PythonTextEditor.limitDocumentation(d);

							String trimmed = d.replace("\n", "").replace("\t", " ").trim();
							if (trimmed.length() > 0 && !trimmed.equals("The most base type"))
								items.put("\u223d <b>" + name + "</b> \u2014 <font size=-2>" + trimmed + "</font>", new IUpdateable() {

									public void update() {
										setTransformation(source, name.equals("defaultTransform") ? null : name);
									}
								});
						} catch (Exception ex) {
						}
					}
				}

				if (MinimalTextField_blockMenu.knownTextTransforms.size() > 0) {
					items.put("Available transforms, from knownTextTransforms", null);
					for (final Pair<String, String> s : MinimalTextField_blockMenu.knownTextTransforms) {
						items.put("\u223d <b>" + s.left + "</b> \u2014 <i>" + s.right + "</i>", new IUpdateable() {

							public void update() {
								setTransformation(source, s.left);
							}
						});
					}
				}

				PythonPlugin plugin = PythonPlugin.python_plugin.get(source);
				Shell frame = ((PythonPluginEditor) plugin).getEditor().getFrame();
				Point screen = Launcher.display.map(null, frame, Launcher.display.getCursorLocation());

				new SmallMenu().createMenu(items, frame, null).show(screen);
			}
		}));
	}

	@Override
	public void registeredWith(IVisualElement root) {
		super.registeredWith(root);
	}

	public EditorExecutionInterface getEditorExecutionInterface(final IVisualElement source, final EditorExecutionInterface delegateTo) {
		return new EditorExecutionInterface() {
			public void executeFragment(String fragment) {

				EditorExecutionInterface delegateTo2 = delegateTo;
				if (delegateTo == null) {
					fragment = sourceFilter(source).apply(fragment);
					PythonInterface.getPythonInterface().execString(fragment);
				} else {
					fragment = sourceFilter(source).apply(fragment);
					delegateTo2.executeFragment(fragment);
				}
			}

			public Object executeReturningValue(String fragment) {
				fragment = sourceFilter(source).apply(fragment);

				EditorExecutionInterface delegateTo2 = delegateTo;
				if (delegateTo == null) {
					delegateTo2 = ((PythonPluginEditor) PythonPluginEditor.python_plugin.get(source)).getEditor().getInterface();
				}

				return delegateTo2.executeReturningValue(fragment);
			}

			@Override
			public boolean globalCompletionHook(String leftText, boolean publicOnly, ArrayList<Completion> comp, BaseTextEditor2 inside) {

				IFunction<String,Collection<Completion>> h = completionHook(source, inside);
				if (h != null) {
					Collection<Completion> coll = h.apply(leftText);
					if (coll != null) {
						System.out.println(" completion hook returns <" + coll + ">");
						comp.addAll(coll);
						return true;
					}
				}
				return false;
			}

		};
	}

	@Override
	public void update() {

	}

	@Override
	protected DefaultOverride newVisualElementOverrides() {
		return new LocalOver();
	}

	public void setTransformation(IVisualElement source, String left) {
		if (left == null)
			wrapInTransform.delete(source, source);
		else
			wrapInTransform.set(source, source, left);
		PythonPlugin plugin = PythonPlugin.python_plugin.get(source);
		((PythonPluginEditor) plugin).swapInCustomToolbar();
	}

}
