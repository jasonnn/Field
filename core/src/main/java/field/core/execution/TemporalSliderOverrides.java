package field.core.execution;

import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.dispatch.mRun;
import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.VisualElement;
import field.core.dispatch.Rect;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.plugins.drawing.OfferedAlignment;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.plugins.python.PythonPlugin;
import field.core.plugins.python.PythonPlugin.CapturedEnvironment;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.PlainDraggableComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IProvider;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;
import field.util.PythonUtils;
import org.eclipse.swt.widgets.Event;
import org.python.core.PyFunction;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * tools for creating temporal slider
 *
 * @author marc
 */
public
class TemporalSliderOverrides extends DefaultOverride {
    public static final VisualElementProperty<TimeSystem> currentTimeSystem =
            new VisualElementProperty<TimeSystem>("currentTimeSystem_");

    public static final VisualElementProperty<Float> maximumTime = new VisualElementProperty<Float>("maximumX_");
    public static final VisualElementProperty<Float> minimumTime = new VisualElementProperty<Float>("minimumX_");

    public static
    Pair<VisualElement, BasicRunner> newLocalTemporalSlider(String token,
                                                            IVisualElement root,
                                                            float x,
                                                            PyFunction function) {
        return newLocalTemporalSlider(token,
                                      root,
                                      x,
                                      (IProvider) new PythonUtils().providerObject(function,
                                                                                   (CapturedEnvironment) PythonInterface
                                                                                                                 .getPythonInterface()
                                                                                                                 .getVariable("_environment")));
    }

    public static
    Pair<VisualElement, BasicRunner> newLocalTemporalSliderFromFunction(String token,
                                                                        IVisualElement root,
                                                                        float x,
                                                                        PyFunction function) {
        return newLocalTemporalSlider(token,
                                      root,
                                      x,
                                      (IProvider) new PythonUtils().providerObject(function,
                                                                                   (CapturedEnvironment) PythonInterface
                                                                                                                 .getPythonInterface()
                                                                                                                 .getVariable("_environment")));
    }

    protected transient IProvider<Collection<IVisualElement>> subsets;

    public static
    Pair<VisualElement, BasicRunner> newLocalTemporalSlider(String token,
                                                            IVisualElement root,
                                                            float x,
                                                            final IProvider<Collection<IVisualElement>> include) {
        //System.out.println(" new local temporal slider <" + include + " " + Arrays.asList(include.getClass().getInterfaces()) + ">");
        new Exception().printStackTrace();

        final Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> created =
                VisualElement.createWithToken(token,
                                              root,
                                              new Rect(10, 0, 0, 0),
                                              VisualElement.class,
                                              PlainDraggableComponent.class,
                                              TemporalSliderOverrides.class);
        created.right.forwardsToTimeSystem = false;
        created.left.setFrame(new Rect(x, 0, 0, 0));

        created.right.subsets = include;

        final PythonScriptingSystem parentPss = PythonScriptingSystem.pythonScriptingSystem.get(root);
        final PythonScriptingSystem pss = new PythonScriptingSystem() {
            @Override
            public
            Collection allPythonScriptingElements() {
                Collection allPromises = parentPss.allPythonScriptingElements();
                ArrayList a = new ArrayList();
                Collection<IVisualElement> included = include.get();
                for (Object o : allPromises) {
                    if (included.contains(parentPss.reversePromises.get(o))) {
                        a.add(o);
                    }
                }
                return a;
            }

            @Override
            public
            Promise promiseForKey(Object key) {
                if (include.get().contains(key)) return super.promiseForKey(key);
                else return null;
            }

            @Override
            public
            Promise revokePromise(Object key) {
                return super.revokePromise(key);
            }
        };

        PythonPlugin.python_source.set(created.left, created.left, "");
        IVisualElement.name.set(created.left, created.left, token);

        final BasicRunner runner = new BasicRunner(pss, x);

        return new Pair<VisualElement, BasicRunner>(created.left, runner);
    }

    public static
    Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> newTemporalSlider(String token,
                                                                                              IVisualElement root) {
        final Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> created =
                VisualElement.createWithToken(token,
                                              root,
                                              new Rect(10, 0, 0, 0),
                                              VisualElement.class,
                                              PlainDraggableComponent.class,
                                              TemporalSliderOverrides.class);
        created.left.setFrame(new Rect(-1, 0, 0, 0));

        IVisualElement.name.set(created.left, created.left, "timeSlider");
        root.setProperty(IVisualElement.timeSlider, created.left);

        return created;
    }

    public static
    Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> newTemporalSlider(String token,
                                                                                              StandardFluidSheet sheet) {
        final Triple<VisualElement, PlainDraggableComponent, TemporalSliderOverrides> created =
                VisualElement.createWithToken(token,
                                              sheet.getRoot(),
                                              new Rect(10, 0, 0, 0),
                                              VisualElement.class,
                                              PlainDraggableComponent.class,
                                              TemporalSliderOverrides.class);
        created.left.setFrame(new Rect(1, 0, 0, 0));
        final BasicRunner runner =
                new BasicRunner(sheet.getRoot().getProperty(PythonScriptingSystem.pythonScriptingSystem), 1);
        Cont.linkWith(sheet, IUpdateable.UPDATE_METHOD, new mRun<StandardFluidSheet>(IUpdateable.UPDATE_METHOD) {
            float last = 1;

            public
            void update() {

                float now = (float) created.left.getFrame(null).x;

                runner.update(now);
                last = now;
            }
        });

        sheet.getRoot().setProperty(IVisualElement.timeSlider, created.left);

        return created;
    }

    transient CachedLine cl;

    transient Rect lastFrame;

    boolean forwardsToTimeSystem = true;

    IUpdateable timeSliderToFrame;

    public
    void drivenByTimeSystem(final Ref<TimeSystem> r) {
        if (!forwardsToTimeSystem) return;
        forwardsToTimeSystem = false;

        if (r.get() != null) {
            float delta = (float) (forElement.getFrame(null).x - r.get().getExecutionTime());

            r.get().supplyTimeManipulation(0, delta);
        }

        Launcher.getLauncher().registerUpdateable(getTimeSliderToFrame());
    }

    public
    void driveTimeSystem(final Ref<TimeSystem> r) {
        if (forwardsToTimeSystem) return;
        if (r.get() != null) {
            float delta = (float) (r.get().getExecutionTime());

            Rect r2 = forElement.getFrame(null);

            r2.x = delta;

            forElement.setFrame(r2);
        }

        forwardsToTimeSystem = true;
        Launcher.getLauncher().deregisterUpdateable(getTimeSliderToFrame());
    }

    @Override
    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
        if (source == forElement) {
            Rect frame = forElement.getFrame(null);
            if (frame.x - 5 <= event.x && frame.x + frame.w + 5 >= event.x) is.set(true);
        }
        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
        IVisualElementOverrides dp = IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(forElement);
        final Ref<TimeSystem> r = new Ref<TimeSystem>(null);
        dp.getProperty(forElement, currentTimeSystem, r);
        if (r.get() != null) {

            items.put("Temporal Slider", null);
            if (forwardsToTimeSystem) {
                items.put(" make time system drive temporal slider", new IUpdateable() {
                    public
                    void update() {
                        forwardsToTimeSystem = false;
                        Launcher.getLauncher().registerUpdateable(getTimeSliderToFrame());
                    }
                });
                items.put(" synchronize time system, then make time system drive temporal slider", new IUpdateable() {
                    public
                    void update() {
                        drivenByTimeSystem(r);
                    }
                });
            }
            else {
                items.put(" make temporal slider drive time system", new IUpdateable() {
                    public
                    void update() {
                        forwardsToTimeSystem = true;
                        Launcher.getLauncher().deregisterUpdateable(getTimeSliderToFrame());
                    }
                });
                items.put(" synchronize temporal slider, make temporal slider drive time system", new IUpdateable() {
                    public
                    void update() {
                        driveTimeSystem(r);
                    }

                });
            }
        }

        return super.menuItemsFor(source, items);
    }

    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {

        if (source == forElement) {

            if (lastFrame == null || !lastFrame.equals(source.getFrame(null))) {
                lastFrame = source.getFrame(null);
                this.cl = null;
            }

            List<CachedLine> cl = getCachedLine();

            GLComponentWindow window = GLComponentWindow.getCurrentWindow(null);

            boolean selected = false;

            SelectionGroup<iComponent> selgroup = IVisualElement.selectionGroup.get(source);
            if (selgroup == null) {
                //System.out.println(" warning selgroup is null for <" + source + ">");
                return StandardTraversalHint.STOP;
            }
            Set<iComponent> selection = selgroup.getSelection();
            for (iComponent s : selection) {
                IVisualElement e = s.getVisualElement();
                if (e != null && e.equals(source)) selected = true;
            }
            if (selected) {
                cl.get(0).getProperties().put(iLinearGraphicsContext.thickness, 3f);

                CachedLine text = new CachedLine();
                Vector2 upper = window.transformWindowToCanvas(new Vector2(0, 0.0f));

                text.getInput().moveTo((float) bounds.x, upper.y - 35);
                NumberFormat f = NumberFormat.getNumberInstance();
                f.setMaximumFractionDigits(2);
                NumberFormatter ff = new NumberFormatter(f);

                try {
                    text.getInput()
                        .setPointAttribute(iLinearGraphicsContext.text_v,
                                           "  t=" + ff.valueToString(bounds.x) + (subsets != null ? ("  ("
                                                                                                     + forElement.getProperty(IVisualElement.name)
                                                                                                     + ')') : ""));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                text.getProperties().put(iLinearGraphicsContext.containsText, true);
                text.getProperties().put(iLinearGraphicsContext.pointed, true);
                text.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.5f));

                GLComponentWindow.currentContext.submitLine(text, text.getProperties());
            }
            for (CachedLine c : cl) {
                GLComponentWindow.currentContext.submitLine(c, c.getProperties());
            }

        }
        return StandardTraversalHint.CONTINUE;
    }

    @Override
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {

        Number mx = maximumTime.get(source);
        Number mi = minimumTime.get(source);

        if (mx != null && newFrame.x > mx.floatValue()) newFrame.x = mx.floatValue();
        if (mi != null && newFrame.x < mi.floatValue()) newFrame.x = mi.floatValue();

        if (source == forElement && !newFrame.equals(oldFrame) && forwardsToTimeSystem) {
            newFrame.w = 0;
            newFrame.h = 0;

            OfferedAlignment.alignment_doNotParticipate.set(source, source, true);
            IVisualElementOverrides dp =
                    IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(forElement);
            Ref<TimeSystem> r = new Ref<TimeSystem>(null);
            dp.getProperty(forElement, currentTimeSystem, r);

            if (r.get() != null) {
                float delta = (float) (newFrame.x - r.get().getExecutionTime());

                //System.out.println(" delta is <" + delta + ">");
                r.get().supplyTimeManipulation(0, delta);
            }

            cl = null;
        }

        if (source == forElement && !newFrame.equals(oldFrame)) {
            forElement.setProperty(IVisualElement.dirty, true);
            cl = null;
        }

        return super.shouldChangeFrame(source, newFrame, oldFrame, now);
    }

    private
    List<CachedLine> getCachedLine() {
        List<CachedLine> r = new ArrayList<CachedLine>();

        cl = new CachedLine();
        r.add(cl);

        GLComponentWindow window = GLComponentWindow.getCurrentWindow(null);
        Vector2 lower = window.transformWindowToCanvas(new Vector2(0, 0));
        Vector2 upper = window.transformWindowToCanvas(new Vector2(0, 1600));

        Rect bounds = forElement.getFrame(null);
        cl.getInput().moveTo((float) bounds.x, lower.y);
        cl.getInput().lineTo((float) bounds.x, upper.y);
        // cl.getInput().moveTo((float) bounds.x, 0);
        // cl.getInput().lineTo((float) bounds.x, 10);
        // cl.getInput().lineTo((float) bounds.x, 20);
        cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.5f));
        // cl.getProperties().put(iLinearGraphicsContext.notForExport,
        // true);

        if (subsets != null) {
            Collection<IVisualElement> contents = subsets.get();

            Rect x = null;
            for (IVisualElement v : contents) {
                if (v != forElement && v.getProperty(IVisualElement.name) != null) {
                    x = Rect.union(v.getFrame(null), x);
                }
            }
            if (x != null) {
                x.insetAbsolute(-10);
                x = x.includePoint((float) bounds.x, x.midPoint().y);

                cl = new CachedLine();
                r.add(cl);

                cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.05f));
                cl.getProperties().put(iLinearGraphicsContext.filled, true);
                cl.getProperties().put(iLinearGraphicsContext.stroked, false);

                cl.getInput().moveTo((float) x.x, (float) x.y);
                cl.getInput().lineTo((float) bounds.x, (float) x.y - 30f);
                cl.getInput().lineTo((float) (x.x + x.w), (float) (x.y));
                cl.getInput().lineTo((float) (x.x + x.w), (float) (x.y + x.h));
                cl.getInput().lineTo((float) bounds.x, (float) (x.y + x.h + 30f));
                cl.getInput().lineTo((float) (x.x), (float) (x.y + x.h));
                cl.getInput().lineTo((float) (x.x), (float) (x.y));

                cl = new CachedLine();
                r.add(cl);

                cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.15f));
                cl.getProperties().put(iLinearGraphicsContext.thickness, 5f);

                cl.getInput().moveTo((float) bounds.x, (float) x.y - 30);
                cl.getInput().lineTo((float) (bounds.x), (float) (x.y + x.h + 30));

                cl = new CachedLine();
                r.add(cl);

                cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.15f));
                cl.getProperties().put(iLinearGraphicsContext.thickness, 1f);

                cl.getInput().moveTo((float) x.x, (float) x.y);
                cl.getInput().lineTo((float) (bounds.x), (float) (x.y - 30));
                cl.getInput().lineTo((float) (x.x + x.w), (float) x.y);

                cl = new CachedLine();
                r.add(cl);

                cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0.5f, 0.3f, 0, 0.15f));
                cl.getProperties().put(iLinearGraphicsContext.thickness, 1f);

                cl.getInput().moveTo((float) x.x, (float) (x.y + x.h));
                cl.getInput().lineTo((float) (bounds.x), (float) (x.y + x.h + 30));
                cl.getInput().lineTo((float) (x.x + x.w), (float) (x.y + x.h));

            }
        }

        return r;

    }

    protected
    IUpdateable getTimeSliderToFrame() {
        return timeSliderToFrame == null ? timeSliderToFrame = new IUpdateable() {
            IVisualElementOverrides dp =
                    IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(forElement);

            public
            void update() {
                Ref<TimeSystem> r = new Ref<TimeSystem>(null);
                dp.getProperty(forElement, currentTimeSystem, r);
                if (r.get() != null) {
                    timeSystemToTimeSlider(r.get());
                }
            }
        } : timeSliderToFrame;
    }

    protected
    void timeSystemToTimeSlider(TimeSystem system) {
        double ex = system.getExecutionTime();
        Rect r = forElement.getFrame(null);
        if (r.x != (float) ex) {
            r.x = (float) ex;
            forElement.setFrame(r);
        }
    }
}
