package field.context;

import field.context.Generator.Channel;
import field.context.Generator.iTimeFor;
import field.launch.ILaunchable;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.math.abstraction.IDoubleProvider;

public
class GlazedGCTest implements ILaunchable {

    public static
    class Banana {
        double t;

        public
        Banana(double t) {
            this.t = t;
        }
    }

    @Override
    public
    void launch() {

        final Channel<Banana> b = new Channel<Banana>(new iTimeFor<Banana>() {

            @Override
            public
            double timeFor(Banana t) {
                return t.t;
            }
        });


        final Culler<Banana> h = new Culler<GlazedGCTest.Banana>(b, new IDoubleProvider() {

            @Override
            public
            double evaluate() {
                return System.currentTimeMillis();
            }
        }, 500);

        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            @Override
            public
            void update() {

                b.add(new Banana(System.currentTimeMillis()));
                h.update();
                //System.out.println(" channel size :"+b.size()+"   "+Runtime.getRuntime().freeMemory());

                if (Math.random() < 0.05) {
                    System.gc();
                }

            }
        });

    }
}
