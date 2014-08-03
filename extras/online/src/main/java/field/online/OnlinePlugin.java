package field.online;

import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Mixins;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.Ref;
import field.core.execution.IExecutesPromise;
import field.core.execution.PythonInterface;
import field.core.execution.PythonScriptingSystem.Promise;
import field.core.plugins.BaseSimplePlugin;
import field.core.plugins.drawing.SplineComputingOverride;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.plugins.help.NanoHTTPD;
import field.core.plugins.help.NanoHTTPD.Response;
import field.core.plugins.help.ReaderInputStream;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPluginEditor;
import field.core.ui.text.BaseTextEditor2;
import field.core.ui.text.BaseTextEditor2.Completion;
import field.core.ui.text.PythonTextEditor.EditorExecutionInterface;
import field.core.ui.text.PythonTextEditor.PickledCompletionInformation;
import field.core.util.LocalFuture;
import field.core.windowing.GLComponentWindow;
import field.graphics.core.BasicGeometry.VertexBuffer;
import field.graphics.core.scene.IGeometry;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IFloatProvider;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.linalg.Vector4;
import field.online.EmbeddedServer.Handler;
import field.online.org.json.JSONArray;
import field.online.org.json.JSONException;
import field.online.org.json.JSONObject;
import org.java_websocket.WebSocket;

import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.*;

// this (and all subelements) of the node that this is installed on will become "in a web browser"

public class OnlinePlugin extends BaseSimplePlugin {

	static public final VisualElementProperty<String> javascript = new VisualElementProperty<String>("javascript_v");
	static public final VisualElementProperty<OnlinePlugin> online = new VisualElementProperty<OnlinePlugin>("online");
	static public final VisualElementProperty<Boolean> needsOnline = new VisualElementProperty<Boolean>("bridgedToOnline");

	static public EmbeddedServer server = new EmbeddedServer(8080) {
		protected java.io.InputStream findAndRun(String replace, Properties parms) {
			IVisualElement found = StandardFluidSheet.findVisualElementWithName(root, replace);
			if (found != null) {

				Iterator<String> ss = ((Set) parms.keySet()).iterator();

				Map<String, Object> was = new HashMap<String, Object>();
				while (ss.hasNext()) {
					String name = ss.next();
					Object var = PythonInterface.getPythonInterface().getVariable(name);
					was.put(name, var);
					PythonInterface.getPythonInterface().setVariable(name, parseObject(parms.get(name)));
				}

				try {
					SplineComputingOverride.executeMain(found);

					PythonPlugin ed = PythonPluginEditor.python_plugin.get(root);

					String x = ((PythonPluginEditor) ed).getEditor().getOutput().toString();

					return new ReaderInputStream(new StringReader(x));
				} finally {
					Iterator<String> w = was.keySet().iterator();
					while (w.hasNext()) {
						String name = w.next();
						Object val = was.get(name);
						if (val != null)
							PythonInterface.getPythonInterface().setVariable(name, val);
					}
				}
			} else {
				return null;
			}
		}

		private Object parseObject(Object object) {
			try {
				String s = ((String) object);
				try {
					return Long.parseLong(s);
				} catch (NumberFormatException e) {
				}
				try {
					return Double.parseDouble(s);
				} catch (NumberFormatException e) {
				}
				return s;
			} catch (Exception e) {
				return "" + object;
			}
		};
	};

	static {

		PythonPluginEditor.knownPythonProperties.put("javascript", javascript);

		Launcher.getLauncher().registerUpdateable(new IUpdateable() {

			@Override
			public void update() {
				List<String> e = server.getError();
				for (String ee : e)
					PythonInterface.getPythonInterface().printError(ee);
				e = server.getPrint();
				for (String ee : e)
					PythonInterface.getPythonInterface().print(ee);
			}
		});

	}

	@Override
	public void registeredWith(IVisualElement root) {
		super.registeredWith(root);
		this.root = root;

		online.set(root, root, this);

	}

	static int uniq = 0;

	public class BrowserHosted extends DefaultOverride {

		public <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
			if (prop.equals(IExecutesPromise.promiseExecution)) {
				if (needsOnline(source))
					ref.set((T) getExecutesPromise(source, (IExecutesPromise) ref.get()));
			} else if (prop.equals(PythonPluginEditor.editorExecutionInterface)) {
				if (needsOnline(source))
					ref.set((T) getEditorExecutionInterface(source, (EditorExecutionInterface) ref.get()));
			}

			return super.getProperty(source, prop, ref);
		}

		private EditorExecutionInterface getEditorExecutionInterface(IVisualElement source, EditorExecutionInterface editorExecutionInterface) {
			return new EditorExecutionInterface() {

				@Override
				public void executeFragment(String fragment) {
					server.root = root;

					PythonInterface.getPythonInterface().print(fragment);

					if (fragment.startsWith("print ")) {
						fragment = "_field.fancyLog(" + fragment.substring("print ".length()) + ")";
					}

					addCommand(fragment + "\n");
				}

				@Override
				public boolean globalCompletionHook(String leftText, boolean publicOnly, ArrayList<Completion> comp, BaseTextEditor2 inside) {
					return false;
				}

				@Override
				public Object executeReturningValue(String string) {
					server.root = root;

					PythonInterface.getPythonInterface().print(string);

					final LocalFuture<PickledCompletionInformation> laf = new LocalFuture<PickledCompletionInformation>();
					uniq++;

					;// ;//System.out.println(" -- at completion --"
						// + uniq);

					server.addHandler("/field/completion_" + uniq + "_", new Handler() {

						@Override
						public Response serve(Response r, String uri, String metod, Properties header, Properties parms) {

							;// ;//System.out.println(" handling response <"
								// + (String)
								// parms.get("data")
								// + ">");

							try {
								JSONObject o = new JSONObject((String) parms.get("data"));
								laf.set(informationFromJSON(o));
							} catch (JSONException e) {
								e.printStackTrace();
							}

							return new Response(NanoHTTPD.HTTP_OK, null, "");
						}

						@Override
						public boolean isTransient() {
							return true;
						}
					});
					;// ;//System.out.println(" -- running completion --"
						// + uniq);
					server.addContent("_field.introspect(\"/field/completion_" + uniq + "_\"," + string + ")\n");

					return laf;
				}

			};
		}

		@Override
		public TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
			if (needsOnline(source)) {
				if (GLComponentWindow.currentContext != null && GLComponentWindow.draft) {
					CachedLine l = new CachedLine();
					l.getInput().moveTo((float) (bounds.x + bounds.w - 12), (float) (bounds.y + bounds.h - 12));
					l.getInput().setPointAttribute(iLinearGraphicsContext.text_v, " O ");
					l.getInput().setPointAttribute(iLinearGraphicsContext.textIsBlured_v, true);
					l.getInput().setPointAttribute(iLinearGraphicsContext.font_v, new java.awt.Font("Gill Sans", java.awt.Font.ITALIC, 25));
					l.getInput().setPointAttribute(iLinearGraphicsContext.fillColor_v, new Vector4(0, 0, 0, 0.4f));
					l.getProperties().put(iLinearGraphicsContext.containsText, true);
					GLComponentWindow.currentContext.submitLine(l, l.getProperties());
				}
			}
			return super.paintNow(source, bounds, visible);
		}

		@Override
		public
        TraversalHint menuItemsFor(final IVisualElement source, Map<String, IUpdateable> items) {
			if (source != null) {
				if (needsOnline(source)) {
					items.put("Online / Javascript", null);
					items.put("\u24c4 <b>Remove bridge</b> to Online environment", new IUpdateable() {
						public void update() {
							needsOnline.set(source, source, false);
							IVisualElement.dirty.set(source, source, true);
						}
					});
				} else {
					items.put("Online / Javascript", null);
					items.put("\u24c4 Bridge element to <b>Online environment</b>", new IUpdateable() {
						public void update() {
							needsOnline.set(source, source, true);
							IVisualElement.dirty.set(source, source, true);
						}
					});
				}
			}
			return super.menuItemsFor(source, items);
		}

		private
        IExecutesPromise getExecutesPromise(IVisualElement source, IExecutesPromise iExecutesPromise) {

			return new IExecutesPromise() {

				@Override
				public void stopAll(float t) {
				}

				@Override
				public void removeActive(Promise p) {
					EditorExecutionInterface e = getEditorExecutionInterface(null, null);
					int code = System.identityHashCode(p);
					e.executeFragment("if (taskQueue.i" + code + " && taskQueue.i" + code + ".exit){taskQueue.i" + code + ".exit();}\ndelete taskQueue.i" + code + ";\n");
					p.wontExecute();
				}

				@Override
				public void addActive(IFloatProvider timeProvider, Promise p) {
					String text = p.getText();

					int code = System.identityHashCode(p);

					String preamble = "_r = undefined;\n";

					String postamble = "\n					if (window._r)\n" + "					{\n" + "if (window._r.length==3){taskQueue.i" + code + "=_r[1]; _r[1].exit=_r[2]; _r[0]();} else" + "						taskQueue.i" + code + "=_r;\n" + "					}\n";

					EditorExecutionInterface e = getEditorExecutionInterface(null, null);
					p.willExecute();
					e.executeFragment(preamble + text + postamble);

				}
			};

		}
	}

	public void setVariable(String name, String jsonValue) {
		server.addContent(name + "=" + jsonValue);
	}

	public void setVariable(String name, Map jsonValue) {

		String v = new JSONObject(jsonValue).toString();

		server.addContent(name + "=" + v);
	}

	protected void addCommand(String string) {
		server.addContent(string);
	}

	static public void upgrade(IVisualElement root) {
		new Mixins().mixInOverride(BrowserHosted.class, root);
	}

	static private PickledCompletionInformation informationFromJSON(JSONObject o) throws JSONException {

		List<List<String>> info = new ArrayList<List<String>>();

		;// ;//System.out.println(" object is <" + o + ">");

		JSONArray contents = (JSONArray) o.getJSONArray("text");
		for (int i = 0; i < contents.length(); i++) {
			JSONArray con = contents.getJSONArray(i);

			String name = (String) con.get(0);
			String type = (String) con.get(1);
			String args = (String) con.get(2);

			ArrayList<String> ii = new ArrayList<String>();
			if (type.equals("function")) {
				ii.add("pythonmethod");
				ii.add(name);
				ii.add(args.replace("(", "").replace(")", ""));
				ii.add("");
			} else {
				ii.add("field");
				ii.add(type);
				ii.add(name);
				ii.add(args);
			}
			info.add(ii);
		}

		return new PickledCompletionInformation(info);
	}

	@Override
	protected String getPluginNameImpl() {
		return "online";
	}

	@Override
	protected DefaultOverride newVisualElementOverrides() {
		return new BrowserHosted();
	}

	static public boolean needsOnline(IVisualElement source) {
		Object o = needsOnline.get(source);
		if (o == null)
			return false;
		if (o instanceof Boolean)
			return ((Boolean) o).booleanValue();
		if (o instanceof Number)
			return ((Number) o).intValue() > 0;
		return false;
	}

	@Override
	public void update() {
		super.update();
		server.root = root;
	}

	public void sendBuffer(ByteBuffer b) {
		server.addContent(b);
	}

    public
    void sendBuffer(WebSocket arg0, IGeometry b, int a) {
        VertexBuffer vb = (VertexBuffer) b.auxBuffers().get(a);
		server.addContent(vb.bBuffer);
	}

}
