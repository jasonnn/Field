package field.extras.scrubber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.persistance.VisualElementReference;
import field.core.plugins.python.PythonPlugin;
import field.extras.scrubber.ScrubberPlugin.Connection;
import field.launch.IUpdateable;
import field.namespace.generic.IFunction;
import field.util.HashMapOfLists;
import org.python.core.PyObject;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;

@Woven
public class ConnexionDeviceHack implements iConnexionSource {

	static public ConnexionDeviceHack globalDevice;

	float[] axes = new float[6];
	int buttons = 0;
	int lastButton = 0;

	private final ScrubberPlugin registerWith;

	public ConnexionDeviceHack(ScrubberPlugin registerWith) throws IOException {

		this.registerWith = registerWith;
		if (globalDevice == null) {

			ProcessBuilder k = new ProcessBuilder("killall", "-9", "3DxClientTest");
			try {
				k.start().waitFor();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			ProcessBuilder b = new ProcessBuilder("3DxClientTest.app/Contents/MacOS/3DxClientTest");
			Process proc = b.start();
			final InputStream stream = proc.getErrorStream();
			final BufferedReader buffer = new BufferedReader(new InputStreamReader(stream), 10);
			new Thread(new Runnable() {
				int n = 0;

				public void run() {
					while (true) {
						try {
							String r = buffer.readLine();
							if (r != null) {
								String[] s = r.split(" ");
								for (int i = 0; i < 6; i++) {
									axes[i] = map(i, Integer.parseInt(s[i]) / 255f);
								}
								buttons = Integer.parseInt(s[6]);
								if (buttons != lastButton)
									firecallbacks(buttons, lastButton);
								lastButton = buttons;
								n++;
								if (n % 40 == 0)
									;//;//System.out.println(" (( connexion alive ))");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();

			globalDevice = this;
		}

		if (registerWith!=null)
			registerFactories(registerWith);
	}

	protected void registerFactories(ScrubberPlugin registerWith) {

		registerWith.addFactory("scrub <b>horizontally</b> based on <b>z-axis</b> rotation", new IFunction<IVisualElement,Connection>() {
			public
            Connection apply(IVisualElement in) {
				return new GeneralConnection(in, "horizontal(" + in.getProperty(IVisualElement.name) + ")", 5, 10, 0);
			}
		});
		registerWith.addFactory("scrub <b>horizontally</b> based on <b>z-axis</b> translation", new IFunction<IVisualElement,Connection>() {
			public Connection apply(IVisualElement in) {
				return new GeneralConnection(in, "horizontal(" + in.getProperty(IVisualElement.name) + ")", 2, 10, 0);
			}
		});
		registerWith.addFactory("<b> execute <i>while</i></b> left button down", new IFunction<IVisualElement,Connection>() {
			public Connection apply(IVisualElement in) {
				return new PressExecution(in, "pressLeft(" + in.getProperty(IVisualElement.name) + ")", 1);
			}
		});
		registerWith.addFactory("<b> <i>toggle</i> execution</b> with left button", new IFunction<IVisualElement,Connection>() {
			public Connection apply(IVisualElement in) {
				return new ToggleExecution(in, "toggleLeft(" + in.getProperty(IVisualElement.name) + ")", 1);
			}
		});
		registerWith.addFactory("<b> execute <i>while</i></b> right button down", new IFunction<IVisualElement,Connection>() {
			public
            Connection apply(IVisualElement in) {
				return new PressExecution(in, "pressRight(" + in.getProperty(IVisualElement.name) + ")", 2);
			}
		});
		registerWith.addFactory("<b> <i>toggle</i> execution</b> with right button", new IFunction<IVisualElement,Connection>() {
			public
            Connection apply(IVisualElement in) {
				return new ToggleExecution(in, "toggleRight(" + in.getProperty(IVisualElement.name) + ")", 2);
			}
		});
	}

	static public class GeneralConnection extends Connection {

		public GeneralConnection(IVisualElement to, String name, int axis, float scale, int aspect) {
			this.outputTo = new VisualElementReference(to);
			this.name = name;
			this.axis = axis;
			this.scale = scale;
			this.targetAspect = aspect;
			enabled = true;
		}

		int axis = 0;
		int targetAspect = 0;
		float scale = 0;

		@Override
		public void update(IVisualElement root) {

			;//;//System.out.println(" update generale conncetion");

			if (ConnexionDeviceHack.globalDevice == null)
				return;
			float f = ConnexionDeviceHack.globalDevice.axes[axis];
			;//;//System.out.println(f);

			f *= scale;
			Rect fr = outputTo.get(root).getFrame(null);
			if (targetAspect == 0)
				fr.x += f;
			else if (targetAspect == 1)
				fr.y += f;

			;//;//System.out.println("updating frame to <" + fr + ">");
			IVisualElement old = IVisualElementOverrides.topology.setAt(outputTo.get(root));
			IVisualElementOverrides.forward.shouldChangeFrame.shouldChangeFrame(outputTo.get(root), fr, outputTo.get(root).getFrame(null), true);
			outputTo.get(root).setProperty(IVisualElement.dirty, true);
			IVisualElementOverrides.topology.setAt(old);
		}
	}

	static public class ToggleExecution extends Connection {
		private final int button;

		public ToggleExecution(IVisualElement to, String name, int button) {
			this.outputTo = new VisualElementReference(to);
			this.name = name;
			this.button = button;
		}

		boolean lastWasDown = false;
		boolean on = false;

		@Override
		public void update(IVisualElement root) {
			if (ConnexionDeviceHack.globalDevice == null)
				return;
			int b = ConnexionDeviceHack.globalDevice.buttons;
			boolean stat = (b & (1 << button)) != 0;
			if (stat && !lastWasDown) {
				toggle(root);
			}

			lastWasDown = stat;
		}

		private void toggle(IVisualElement root) {
			if (on) {
				IVisualElement old = IVisualElementOverrides.topology.setAt(outputTo.get(root));
				IVisualElementOverrides.forward.endExecution.endExecution(outputTo.get(root));
				IVisualElementOverrides.topology.setAt(old);
				on = false;
			} else {
				IVisualElement old = IVisualElementOverrides.topology.setAt(outputTo.get(root));
				IVisualElementOverrides.forward.beginExecution.beginExecution(outputTo.get(root));
				IVisualElementOverrides.topology.setAt(old);
				on = true;
			}
		}
	}

	static public class PressExecution extends Connection {
		private final int button;

		public PressExecution(IVisualElement to, String name, int button) {
			this.outputTo = new VisualElementReference(to);
			this.name = name;
			this.button = button;
		}

		boolean lastWasDown = false;

		@Override
		public void update(IVisualElement root) {
			if (ConnexionDeviceHack.globalDevice == null)
				return;
			int b = ConnexionDeviceHack.globalDevice.buttons;
			boolean stat = (b & (1 << button)) != 0;
			if (stat && !lastWasDown) {
				toggle(true, root);
			} else if (!stat && lastWasDown) {
				toggle(false, root);
			}

			lastWasDown = stat;
		}

		private void toggle(boolean on, IVisualElement root) {
			if (!on) {
				IVisualElement old = IVisualElementOverrides.topology.setAt(outputTo.get(root));
				IVisualElementOverrides.forward.endExecution.endExecution(outputTo.get(root));
				IVisualElementOverrides.topology.setAt(old);

			} else {
				IVisualElement old = IVisualElementOverrides.topology.setAt(outputTo.get(root));
				IVisualElementOverrides.forward.beginExecution.beginExecution(outputTo.get(root));
				IVisualElementOverrides.topology.setAt(old);
			}
		}
	}

	@NextUpdate
	protected void firecallbacks(int newButtons, int oldButtons) {
		int m = 1;
		for (int i = 0; i < 8; i++) {

			;//;//System.out.println(newButtons+" "+oldButtons+" "+m);

			if ((newButtons & m) !=0 && (oldButtons & m) == 0) {
				Collection<IUpdateable> c = buttonToggles.get(i);
				if (c != null)
					for (IUpdateable u : c)
						u.update();
			}
			m <<= 1;
		}
	}

	HashMapOfLists<Integer, IUpdateable> buttonToggles = new HashMapOfLists<Integer, IUpdateable>();

	public void addToogle(int button, IUpdateable u) {
		buttonToggles.addToList(button, u);
	}

	public void addToogle(int button, final PyObject u, final PythonPlugin.CapturedEnvironment env) {
		buttonToggles.addToList(button, new IUpdateable() {

			public void update() {
				env.enter();
				try {
					u.__call__();
				} finally {
					env.exit();
				}
			}
		});
	}

	protected float map(int i, float f) {
		return Math.signum(f) * f * f;
	}

	public float[] getAxes() {
		return axes;
	}

	public int getButtons() {
		return buttons;
	}
}