package field.namespace.diagram;

import field.launch.IUpdateable;
import field.math.abstraction.IDoubleProvider;

import java.util.List;


/**
 * also needed
 * <p/>
 * connections that autoreconnect a la geometry
 * connections that, upon disconnecting from one, delees the other
 * union of many iChannels
 * <p/>
 * we've promissed a meta-grbf-9new windows)-marker
 * how does slicing  work with that? Well, we need to know if the grbf needs resorting or not after a grbf time update
 * we'll need iMutableMarker
 * perhaps grbf should be implemented in terms fo a channel... this makes sense.
 * <p/>
 * for he mutable case, a linked list structure (inside the, meta list)
 * <p/>
 * slices could be handled by having slice markers in there, or having slices keep access to their first and last and search out on modification
 * <p/>
 * so we need a mutable channel then a meta-grbf-window thing then
 * <p/>
 * batching is good, but we could do with the the production dispatch stuff as well if we wanted to go there. However, we could make maore use of batching
 * <p/>
 * <p/>
 * if this class is on a mutable channel, it needs to register for mmarker modified events, and check formarkers moving accross the threshold.
 *
 * @author marc
 *         Created on Dec 20, 2004 \u2014 bus to boston
 */
public abstract
class Horizon implements IUpdateable {

    protected float alpha = 0.95f;

    protected double clockLastTick = 0;

    protected double clockUpdateEstimation = 0;

    protected boolean firstUpdate = true;

    protected double lastSliceEndsAt = 0;

    protected IDoubleProvider nowClock;

    protected DiagramZero.iChannel outputStream;

    protected float standoff = 1;

    protected DiagramZero.iChannel slice;

    boolean noReadAhead = false;

    public
    Horizon(IDoubleProvider nowClock, DiagramZero.iChannel outputStream) {
        this.nowClock = nowClock;
        this.outputStream = outputStream;

    }

    public
    IDoubleProvider getClockSource() {
        return this.nowClock;
    }

    public
    Horizon setClockSource(IDoubleProvider clockSource) {
        this.nowClock = clockSource;
        return this;
    }

    public
    void setNoReadAhead() {
        noReadAhead = true;
    }

    public
    Horizon setStandoff(float standoff) {
        this.standoff = standoff;
        return this;
    }

    public
    void update() {

        if (noReadAhead) {

            double now = nowClock.evaluate();
            if (now < clockLastTick) firstUpdate = true;

            if (firstUpdate) {
                clockLastTick = now;
                lastSliceEndsAt = clockLastTick;
                firstUpdate = false;
            }

            double updateFrom = clockLastTick;
            double updateTo = now;
            update(now, updateFrom, updateTo);

            clockLastTick = now;
            lastSliceEndsAt = updateTo;

        }
        else {

            double now = nowClock.evaluate();
            if (now < clockLastTick) firstUpdate = true;

            if (firstUpdate) {
                clockLastTick = nowClock.evaluate();
                lastSliceEndsAt = clockLastTick;
                firstUpdate = false;
            }

            double currentClockUpdate = now - clockLastTick;

            clockUpdateEstimation = clockUpdateEstimation * alpha + (1 - alpha) * currentClockUpdate;

            double updateFrom = now + (clockUpdateEstimation * standoff);
            double updateTo = now + (clockUpdateEstimation * (standoff + 1));

            updateFrom = lastSliceEndsAt;
            if (updateTo < lastSliceEndsAt) updateTo = lastSliceEndsAt;

            update(now, updateFrom, updateTo);

            lastSliceEndsAt = updateTo;
            clockLastTick = now;
        }
    }

    protected
    void update(double now, double from, double to) {
        if (outputStream instanceof Channel) ((Channel) outputStream).setSliceIsGreedy(false);
        List l = outputStream.getSlice((float) from, (float) to).getIterator().remaining();
        updateWithList(now, l);
    }

    protected abstract
    void updateWithList(double now, List l);

}
