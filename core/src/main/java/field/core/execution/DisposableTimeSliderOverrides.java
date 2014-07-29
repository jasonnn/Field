package field.core.execution;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElementOverrides;
import field.core.dispatch.VisualElement;
import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.execution.PythonScriptingSystem.Promise;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.opengl.iLinearGraphicsContext;
import field.core.ui.ExtendedMenuMap;
import field.core.ui.MarkingMenuBuilder;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.PlainDraggableComponent;
import field.core.windowing.components.iComponent;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IFloatProvider;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.linalg.Vector2;
import field.math.linalg.Vector4;
import field.util.collect.tuple.Triple;
import org.eclipse.swt.widgets.Event;

import java.util.*;

public
class DisposableTimeSliderOverrides extends IVisualElementOverrides.DefaultOverride {

    public static final VisualElementProperty<Number> playDuration = new VisualElementProperty<Number>("playDuration");
    public static final VisualElementProperty<Number> skipbackDuration =
            new VisualElementProperty<Number>("skipbackDuration");

    List<IVisualElement> on = new ArrayList<IVisualElement>();

    private List<BasicRunner> dependantRunners = null;

    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        if (source == forElement) {

            List<CachedLine> cl = getCachedLine();

            GLComponentWindow window = GLComponentWindow.getCurrentWindow(null);

            boolean selected = false;

            Set<iComponent> selection = IVisualElement.selectionGroup.get(source).getSelection();
            for (iComponent s : selection) {
                IVisualElement e = s.getVisualElement();
                if ((e != null) && e.equals(source)) selected = true;
            }
            if (selected) {
                cl.get(0).getProperties().put(iLinearGraphicsContext.thickness, 4f);
            }

            for (CachedLine c : cl) {
                GLComponentWindow.currentContext.submitLine(c, c.getProperties());
            }

        }
        return super.paintNow(source, bounds, visible);
    }

    @Override
    public
    TraversalHint isHit(IVisualElement source, Event event, Ref<Boolean> is) {
        if (source == forElement) {
            Rect frame = forElement.getFrame(null);
            if (((frame.x - 5) <= event.x) && ((frame.x + frame.w + 5) >= event.x)) is.set(true);
        }
        return StandardTraversalHint.CONTINUE;
    }

    private
    List<CachedLine> getCachedLine() {

        List<CachedLine> r = new ArrayList<CachedLine>();

        {
            CachedLine cl;
            cl = new CachedLine();
            r.add(cl);

            GLComponentWindow window = GLComponentWindow.getCurrentWindow(null);

            float min = Float.POSITIVE_INFINITY;
            float max = Float.NEGATIVE_INFINITY;

            for (IVisualElement e : on) {
                Rect f = e.getFrame(null);
                min = (float) Math.min(min, f.y);
                max = (float) Math.max(max, f.y + f.h);
            }

            min -= 10;
            max += 10;

            Rect bounds = forElement.getFrame(null);
            cl.getInput().moveTo((float) bounds.x, max);
            cl.getInput().lineTo((float) bounds.x, min);
            cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0f, 0, 0.5f, 0.5f));
            cl.getProperties().put(iLinearGraphicsContext.notForExport, true);
        }

        {
            CachedLine cl;
            cl = new CachedLine();
            r.add(cl);

            GLComponentWindow window = GLComponentWindow.getCurrentWindow(null);
            Vector2 lower = window.transformWindowToCanvas(new Vector2(0, 0));
            Vector2 upper = window.transformWindowToCanvas(new Vector2(0, 1600));

            Rect bounds = forElement.getFrame(null);
            cl.getInput().moveTo((float) bounds.x, lower.y);
            cl.getInput().lineTo((float) bounds.x, upper.y);
            cl.getProperties().put(iLinearGraphicsContext.color, new Vector4(0f, 0, 0.5f, 0.05f));
            cl.getProperties().put(iLinearGraphicsContext.notForExport, true);
        }

        return r;
    }

    @Override
    public
    TraversalHint shouldChangeFrame(IVisualElement source, Rect newFrame, Rect oldFrame, boolean now) {

        if (source == forElement) {
            newFrame.w = 0;
            newFrame.h = 0;
            forElement.setProperty(IVisualElement.dirty, true);
            if (dependantRunners != null) {

                for (BasicRunner r : dependantRunners) {
                    r.update((float) newFrame.x);
                }
            }

            //System.out.println(" CSF :"+newFrame);

        }

        return super.shouldChangeFrame(source, newFrame, oldFrame, now);
    }

    static int uniq = 0;

    public static
    IVisualElement createDTSO(List<IVisualElement> on, IVisualElement root, float initialx) {
        final Triple<VisualElement, PlainDraggableComponent, DisposableTimeSliderOverrides> created =
                VisualElement.createWithToken("dtso+" + (uniq++),
                                              root,
                                              new Rect(10, 0, 0, 0),
                                              VisualElement.class,
                                              PlainDraggableComponent.class,
                                              DisposableTimeSliderOverrides.class);

        IVisualElement.name.set(created.left, created.left, "transient time slider");
        IVisualElement.doNotSave.set(created.left, created.left, true);

        created.left.setFrame(new Rect(initialx, 0, 0, 0));

        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        for (IVisualElement e : on) {
            Rect f = e.getFrame(null);
            min = (float) Math.min(min, f.x);
            max = (float) Math.max(max, f.x + f.w);
        }


        //System.out.println(" create DTSO on <"+on+"> range is <"+min+" -> "+max+">");


        created.right.playStart = min;
        created.right.playEnd = max;

        created.right.playDurationInSeconds = computePlayDurationForSelection(on);

        created.right.on = on;
        // 1. wire these things up to run @ this float provider

        for (final IVisualElement v : on) {
            PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(v);
            iExecutesPromise runner = iExecutesPromise.promiseExecution.get(v);

            Promise promise = pss.promiseForKey(v);

            runner.addActive(new IFloatProvider() {

                public
                float evaluate() {

                    double vv = created.left.getFrame(null).x;

                    Rect o = new Rect(0, 0, 0, 0);
                    v.getFrame(o);

                    return (float) vv;
                }

            }, promise);

        }

        // 2. if any of these things STOP running, delete this slider
        // and STOP the other ones

        // todo

        return created.left;

    }

    public static
    IVisualElement createDTSO2(List<IVisualElement> on, IVisualElement root, float initialx) {
        final Triple<VisualElement, PlainDraggableComponent, DisposableTimeSliderOverrides> created =
                VisualElement.createWithToken("dtso+" + (uniq++),
                                              root,
                                              new Rect(10, 0, 0, 0),
                                              VisualElement.class,
                                              PlainDraggableComponent.class,
                                              DisposableTimeSliderOverrides.class);

        IVisualElement.name.set(created.left, created.left, "transient time slider");
        IVisualElement.doNotSave.set(created.left, created.left, true);

        created.left.setFrame(new Rect(initialx, 0, 0, 100));

        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        for (IVisualElement e : on) {
            Rect f = e.getFrame(null);
            min = (float) Math.min(min, f.x);
            max = (float) Math.max(max, f.x + f.w);
        }

        created.right.playStart = (float) on.get(0).getFrame(null).x;
        created.right.playEnd = (float) (on.get(0).getFrame(null).w + created.right.playStart);

        created.right.playDurationInSeconds = computePlayDurationForSelection(on);

        created.right.on = on;

        List<BasicRunner> dep = new ArrayList<BasicRunner>();

        // 1. wire these things up to run @ this float provider
        for (final IVisualElement v : on) {
            final PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(v);

            PythonScriptingSystem sub = new PythonScriptingSystem() {
                @Override
                public
                Collection allPythonScriptingElements() {
                    return Collections.singletonList(pss.promises.get(v));
                }

                public
                Promise promiseForKey(Object key) {
                    return pss.promiseForKey(key);
                }
            };

            BasicRunner rr = new BasicRunner(sub, created.right.playStart - 1);
            dep.add(rr);
            rr.update(initialx);
        }

        created.right.dependantRunners = dep;

        // 2. if any of these things STOP running, delete this slider
        // and STOP the other ones

        // todo

        return created.left;

    }

    private static
    float computePlayDurationForSelection(List<IVisualElement> on) {

        float playDuration = defaultPlayDuration;

        for (IVisualElement e : on) {
            Number n = DisposableTimeSliderOverrides.playDuration.get(e);
            if (n != null) return n.floatValue();
        }
        return playDuration;
    }

    public static float defaultPlayDuration = 60f;

    boolean playing = false;

    float playStart = 0;
    float playEnd = 0;
    float playDurationInSeconds = 0;
    boolean loop = false;

    @Override
    public
    TraversalHint menuItemsFor(IVisualElement source, Map<String, IUpdateable> items) {
        if (source == forElement) {

            //System.out.println(" items is <" + items + "> <" + items.getClass() + ">");

            if (items instanceof ExtendedMenuMap) {
                ExtendedMenuMap map = (ExtendedMenuMap) items;
                if (playing) {
                    MarkingMenuBuilder b = map.getBuilder();

                    b.newMenu("Pause", "SW");
                    b.call(new IUpdateable() {
                        public
                        void update() {
                            playing = false;
                        }
                    });
                }
                else {

                    //System.out.println(" building a marking menu builder ");

                    MarkingMenuBuilder b = map.getBuilder();

                    b.newMenu("Play", "E");
                    b.call(new IUpdateable() {
                        public
                        void update() {
                            playing = true;
                            loop = false;
                            startPlayer();
                        }
                    });

                    b.newMenu("Play from beginning", "W");
                    b.call(new IUpdateable() {
                        public
                        void update() {
                            playing = true;
                            Rect f = forElement.getFrame(null);
                            f.x = playStart;
                            forElement.setFrame(f);
                            loop = false;
                            startPlayer();
                        }
                    });
                    b.newMenu("Loop from beginning", "NE2");
                    b.call(new IUpdateable() {
                        public
                        void update() {
                            //System.out.println(" will loop from the beginning ");
                            playing = true;
                            Rect f = forElement.getFrame(null);
                            f.x = playStart;
                            forElement.setFrame(f);
                            loop = true;
                            startPlayer();
                        }
                    });
                }

                MarkingMenuBuilder b = map.getBuilder();
                b.newMenu("Delete", "S");
                b.call(new IUpdateable() {
                    public
                    void update() {
                        stopAndDelete();

                    }
                });

            }
        }
        return super.menuItemsFor(source, items);
    }

    IUpdateable player;

    protected
    void startPlayer() {
        startPlayer(null);
    }

    float skipBy = 0;

    protected
    void startPlayer(final IUpdateable continuation) {
        if (player != null) return;

        //System.out.println(" starting player ");
        player = new IUpdateable() {

            long startedAt = System.currentTimeMillis();

            float ox = (float) forElement.getFrame(null).x;
            float d = playDurationInSeconds * (playEnd - ox) / (playEnd - playStart);

            public
            void update() {

                long now = System.currentTimeMillis();

                double in = (now - startedAt) / 1000f;


                //System.out.println(" in at <"+in+"> <"+ startedAt+"> <"+skipBy+">");

                startedAt += ((double) skipBy) * 1000L;
                skipBy = 0;

                double alpha = in / d;

                //System.out.println("         alpha <"+alpha+">");

                if (alpha > 1 && !loop) {
                    alpha = 1;
                    Launcher.getLauncher().deregisterUpdateable(player);
                    if (continuation != null) continuation.update();
                    player = null;
                    playing = false;
                }
                else if (alpha > 1 && loop) {
                    //System.out.println(" loop point is now");
                    alpha = 1;
                    startedAt = System.currentTimeMillis();
                    ox = playStart;
                }

                Rect f = forElement.getFrame(null);
                f.x = ox * (1 - alpha) + playEnd * alpha;

                //forElement.setFrame(f);

                IVisualElementOverrides.topology.begin(forElement);
                IVisualElementOverrides.forward.shouldChangeFrame.shouldChangeFrame(forElement,
                                                                                    f,
                                                                                    forElement.getFrame(null),
                                                                                    true);
                IVisualElementOverrides.topology.end(forElement);

            }
        };

        Launcher.getLauncher().registerUpdateable(player);
    }

    public static
    void playFromBeginning(IVisualElement e, IVisualElement root) {
        final IVisualElement dtso = createDTSO(Collections.singletonList(e), root, (float) e.getFrame(null).x);

        final DisposableTimeSliderOverrides o = (DisposableTimeSliderOverrides) IVisualElement.overrides.get(dtso);

        o.playing = true;
        o.loop = false;
        o.startPlayer(new IUpdateable() {

            public
            void update() {
                o.stopAndDelete();
            }
        });
    }

    public static
    DisposableTimeSliderOverrides playFromBeginning(List<IVisualElement> e, IVisualElement root) {
        if (e.size() == 0) return null;

        final IVisualElement dtso = createDTSO2(e, root, (float) e.get(0).getFrame(null).x - 1);

        final DisposableTimeSliderOverrides o = (DisposableTimeSliderOverrides) IVisualElement.overrides.get(dtso);

        o.playing = true;
        o.loop = false;
        o.startPlayer(new IUpdateable() {

            public
            void update() {
                o.stopAndDelete();
            }
        });

        return o;
    }

    public static
    DisposableTimeSliderOverrides loopFromBeginning(IVisualElement e, IVisualElement root) {
        final IVisualElement dtso = createDTSO(Collections.singletonList(e), root, (float) e.getFrame(null).x);

        final DisposableTimeSliderOverrides o = (DisposableTimeSliderOverrides) IVisualElement.overrides.get(dtso);

        o.playing = true;
        o.loop = true;
        o.startPlayer(new IUpdateable() {

            public
            void update() {
                o.stopAndDelete();
            }
        });

        return o;
    }

    public
    void stopAndDelete() {
        for (final IVisualElement v : on) {
            PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(v);
            iExecutesPromise runner = iExecutesPromise.promiseExecution.get(v);

            Promise promise = pss.promiseForKey(v);
            runner.removeActive(promise);
        }

        if (dependantRunners != null) {
            for (BasicRunner r : dependantRunners) {
                r.stopAll((float) forElement.getFrame(null).x);
            }
        }

        VisualElement.delete(forElement);
        if (player != null) Launcher.getLauncher().deregisterUpdateable(player);
        player = null;
    }

    public
    void stop() {
        if (player == null) return;

        for (final IVisualElement v : on) {
            PythonScriptingSystem pss = PythonScriptingSystem.pythonScriptingSystem.get(v);
            iExecutesPromise runner = iExecutesPromise.promiseExecution.get(v);

            Promise promise = pss.promiseForKey(v);
            runner.removeActive(promise);
        }

        Launcher.getLauncher().deregisterUpdateable(player);
    }

    public
    void skipBack() {
        if (player == null) return;
        skipBy += 5;
    }

    public
    void skipForward() {
        if (player == null) return;
        skipBy -= 5;
    }

}
