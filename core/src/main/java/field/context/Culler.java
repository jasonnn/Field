package field.context;

import field.context.Generator.Channel;
import field.launch.IUpdateable;
import field.math.abstraction.IDoubleProvider;

public
class Culler<X> implements IUpdateable {


    private final Channel<X> c;
    private final IDoubleProvider time;
    private final double history;

    public
    Culler(Channel<X> c, IDoubleProvider time, double historyLength) {
        this.c = c;
        this.time = time;
        this.history = historyLength;
    }

    @Override
    public
    void update() {
        c.range(Float.NEGATIVE_INFINITY, time.evaluate() - history).clear();
    }

}
