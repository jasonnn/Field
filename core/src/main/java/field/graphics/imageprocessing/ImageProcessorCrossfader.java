package field.graphics.imageprocessing;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.Yield;
import field.bytecode.protect.dispatch.Cont;
import field.bytecode.protect.dispatch.ReturnCode;
import field.bytecode.protect.dispatch.aRun;
import field.bytecode.protect.yield.YieldUtilities;
import field.graphics.windowing.FullScreenCanvasSWT;
import field.launch.IUpdateable;
import field.math.abstraction.IFloatProvider;
import field.math.abstraction.IProvider;

@Woven
public
class ImageProcessorCrossfader implements IUpdateable {

    private final IProvider<Integer> delayProvider;
    private final IUpdateable left;
    private final IUpdateable right;

    public
    ImageProcessorCrossfader(IProvider<Integer> delayProvider, IUpdateable left, IUpdateable right) {
        this.delayProvider = delayProvider;
        this.left = left;
        this.right = right;
    }

    // o means at right;
    float at = 0;
    int t = 0;
    int delay = 0;

    @Yield
    public
    void update() {
        at = 0;
        left.update();
        right.update();
        while (true) {
            left.update();
            delay = delayProvider.get();
            for (int i = 0; i < delay; i++) {
                at = i / (delay - 1f);
                YieldUtilities.yield(null);
                beat();
            }
            right.update();
            delay = delayProvider.get();
            for (int i = 0; i < delay; i++) {
                at = 1 - i / (delay - 1f);
                YieldUtilities.yield(null);
                beat();
            }
        }
    }

    protected
    void beat() {

    }

    public
    IFloatProvider getCrossfade() {
        return new IFloatProvider() {
            public
            float evaluate() {
                return at;
            }
        };
    }

    public
    void join(FullScreenCanvasSWT canvas) {
        aRun arun = new aRun() {
            @Override
            public
            ReturnCode head(Object calledOn, Object[] args) {
                update();
                return super.head(calledOn, args);
            }
        };
        Cont.linkWith(canvas, FullScreenCanvasSWT.method_beforeFlush, arun);

    }

}
