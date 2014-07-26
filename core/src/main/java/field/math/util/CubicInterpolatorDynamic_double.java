package field.math.util;

import field.math.abstraction.iBlendable;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public
class CubicInterpolatorDynamic_double<T extends iBlendable<T>> implements Serializable {

    public
    class Sample implements Serializable {

        private static final long serialVersionUID = -4587582997695641410L;

        public T data;

        public double time;

        public
        Sample(T data, double time) {
            this.data = data;
            this.time = time;

            int index = Collections.binarySearch(samples, this, comparator);
            if (index < 0) index = -index - 1;
            samples.add(index, this);

            cacheInvalid = true;
        }

        protected
        Sample() {
        }

        @Override
        public
        String toString() {
            return data + "@" + time;
        }

    }

    private final
    class MComparator implements Comparator<Sample>, Serializable {
        public
        int compare(Sample s1, Sample s2) {

            return (s1.time < s2.time) ? -1 : ((s1.time > s2.time) ? 1 : 0);
        }
    }

    private boolean extrapolation = false;

    ArrayList<Sample> samples = new ArrayList<Sample>();

    Comparator<Sample> comparator = new MComparator();

    // getting values
    boolean cacheInvalid = true;

    double now = Double.POSITIVE_INFINITY;

    double next = Double.NEGATIVE_INFINITY;

    double before = Double.POSITIVE_INFINITY;

    double after = Double.NEGATIVE_INFINITY;

    int indexNow = -1;

    int indexBefore = 0;

    int indexAfter = 0;

    int indexNext = 0;

    double duration = 0;

    boolean linear = false;

    Sample temp = new Sample();

    public
    CubicInterpolatorDynamic_double() {
    }


    public
    CubicInterpolatorDynamic_double<T> copy() {
        CubicInterpolatorDynamic_double<T> ret = new CubicInterpolatorDynamic_double<T>();

        for (int i = 0; i < getNumSamples(); i++) {
            Sample s = getSample(i);
            ret.new Sample(s.data, s.time);
        }

        return ret;
    }


    public
    T debugGet(double alpha) {
        if (getNumSamples() == 0) return null;
        Sample sample = getSample(0);
        T data = sample.data;

        T t = data.blendRepresentation_newZero();
        debugGetValue(alpha, t);

        return t;
    }

    public
    boolean debugGetValue(double time, T value) {

        System.err.println(" num samples are <" + samples.size() + '>');

        if (samples.size() < 1) return false;

        if (samples.size() == 1) value.setValue(getSample(0).data);

        validateCache(time);

        double a = (time - now) / (duration);

        if (Double.isNaN(a) || Double.isInfinite(a)) a = 0.5f;

        System.err.println(" time <" + time + "> <" + now + "> <" + duration + '>');

        if (!extrapolation) {
            if (a > 1) a = 1;
            if (a < 0) a = 0;

            System.err.println(" clamping to <" + a + '>');

        }
        else {
            if (a < 0) {
                indexNow = 0;
                indexNext = 1;

            }
            else if (a > 1) {
                indexNow = samples.size() - 2;
                indexNext = samples.size() - 1;
            }

            if ((indexNow == indexNext) && (indexNow != 0)) {
                indexNow--;
                indexBefore--;
                if (indexBefore < 0) indexBefore = 0;
                a += 1;
            }

            System.err.println(" extrapolating <"
                               + getSample(indexNow).data
                               + "> <"
                               + getSample(indexNext).data
                               + "> <"
                               + indexNow
                               + "> <"
                               + indexNext
                               + "> <"
                               + a
                               + '>');


            value.lerp(getSample(indexNow).data, getSample(indexNext).data, (float) a);
            return true;

        }

        //		if (a>1)
        //		{
        //			System.err.println(" trying to extrapolate <"+time+"> <"+now+"> <"+duration+"> <"+a+"> <"+indexBefore+"> <"+indexNow+"> <"+indexNext+"> <"+indexAfter+">");
        //		}

        //System.err.println("          "+a+" "+getSample(indexBefore)+" "+getSample(indexNow)+" "+getSample(indexNext)+" "+getSample(indexAfter));

        if (linear) {
            System.err.println(" is linear <"
                               + indexNow
                               + "> <"
                               + indexNext
                               + "> <"
                               + getSample(indexNow)
                               + "> <"
                               + getSample(indexNext)
                               + '>');

            value.lerp(getSample(indexNow).data, getSample(indexNext).data, (float) a);

            return true;
        }

        System.err.println(" cerp <"
                           + getSample(indexBefore).data
                           + ' '
                           + getSample(indexBefore).time
                           + ' '
                           + getSample(indexNow).data
                           + ' '
                           + getSample(indexNow).time
                           + ' '
                           + getSample(indexNext).data
                           + ' '
                           + getSample(indexNext).time
                           + ' '
                           + getSample(indexAfter).data
                           + ' '
                           + getSample(indexAfter).time
                           + ' '
                           + a
                           + '>');

        value.cerp(getSample(indexBefore).data,
                   0,
                   getSample(indexNow).data,
                   (float) (getSample(indexNow).time - getSample(indexBefore).time),
                   getSample(indexNext).data,
                   (float) (getSample(indexNext).time - getSample(indexBefore).time),
                   getSample(indexAfter).data,
                   (float) (getSample(indexAfter).time - getSample(indexBefore).time),
                   (float) a);

        System.err.println(" value = " + value);
        return true;
    }

    public
    void dirty() {
        this.cacheInvalid = false;
        now = Double.POSITIVE_INFINITY;
        next = Double.NEGATIVE_INFINITY;
        before = Double.POSITIVE_INFINITY;
        after = Double.NEGATIVE_INFINITY;
    }

    public
    CubicInterpolatorDynamic_double<T> extrapolate() {
        extrapolation = true;
        return this;
    }

    public
    int findSampleIndexAfter(double from) {
        temp.time = from;
        int n = Collections.binarySearch(samples, temp, comparator);
        if (n < 0) n = -n - 1;
        else n++;
        return n;
    }

    public
    int findSampleIndexBefore(double from) {
        temp.time = from;
        int n = Collections.binarySearch(samples, temp, comparator);
        if (n < 0) n = -n - 2;
        else n--;
        return n;
    }

    public
    T get(double alpha) {
        if (getNumSamples() == 0) return null;
        Sample sample = getSample(0);
        T data = sample.data;

        T t = data.blendRepresentation_newZero();
        getValue(alpha, t);

        return t;
    }

    public
    double getDomainMax() {
        if (getNumSamples() == 0) return Double.NEGATIVE_INFINITY;
        return getSample(getNumSamples() - 1).time;
    }

    public
    double getDomainMin() {
        if (getNumSamples() == 0) return Double.POSITIVE_INFINITY;
        return getSample(0).time;
    }

    public
    double getDuration() {
        if (samples.size() < 2) return 0;
        return getSample(samples.size() - 1).time - getSample(0).time;
    }

    public
    double getEndTime() {
        if (samples.isEmpty()) return 0;
        return getSample(samples.size() - 1).time;
    }

    public
    int getNumSamples() {
        return samples.size();
    }

    public
    Sample getSample(int i) {
        if (i >= (samples.size() - 1)) i = samples.size() - 1;
        if (i < 0) i = 0;
        return samples.get(i);
    }

    public
    double getStartTime() {
        if (samples.isEmpty()) return 0;
        return getSample(0).time;
    }

    public
    boolean getValue(double time, T value) {
        if (samples.size() < 1) return false;

        if (samples.size() == 1) {
            value.setValue(getSample(0).data);
            return true;
        }

        validateCache(time);

        double a = (time - now) / (duration);
        if (duration == 0) a = 0;


        if (Double.isNaN(a) || Double.isInfinite(a)) a = 0.5f;

        if (!extrapolation) {
            if (a > 1) a = 1;
            if (a < 0) a = 0;
        }
        else {
            if (a < 0) {
                indexNow = 0;
                indexNext = 1;

            }
            else if (a > 1) {
                indexNow = samples.size() - 2;
                indexNext = samples.size() - 1;
            }

            if ((indexNow == indexNext) && (indexNow != 0)) {
                indexNow--;
                indexBefore--;
                if (indexBefore < 0) indexBefore = 0;
                a += 1;
            }

            //System.err.println(" extrapolating <"+getSample(indexNow).data+"> <"+getSample(indexNext).data+"> <"+indexNow+"> <"+indexNext+"> <"+a+">");

            value.lerp(getSample(indexNow).data, getSample(indexNext).data, (float) a);
            return true;

        }

        //		if (a>1)
        //		{
        //			System.err.println(" trying to extrapolate <"+time+"> <"+now+"> <"+duration+"> <"+a+"> <"+indexBefore+"> <"+indexNow+"> <"+indexNext+"> <"+indexAfter+">");
        //		}

        //System.err.println("          "+a+" "+getSample(indexBefore)+" "+getSample(indexNow)+" "+getSample(indexNext)+" "+getSample(indexAfter));

        if (linear) {
            value.lerp(getSample(indexNow).data, getSample(indexNext).data, (float) a);
            return true;
        }

        value.cerp(getSample(indexBefore).data,
                   0,
                   getSample(indexNow).data,
                   (float) (getSample(indexNow).time - getSample(indexBefore).time),
                   getSample(indexNext).data,
                   (float) (getSample(indexNext).time - getSample(indexBefore).time),
                   getSample(indexAfter).data,
                   (float) (getSample(indexAfter).time - getSample(indexBefore).time),
                   (float) a);
        return true;
    }

    public
    boolean isInDomain(double now) {
        return (now >= getDomainMin()) && (now <= getDomainMax());
    }

    public
    void mergeInto(CubicInterpolatorDynamic_double<T> from) {
        if ((from.getDomainMax() > this.getDomainMax()) && (from.getDomainMin() < this.getDomainMax()))
            System.err.println(" warning, overlapping merge of cubic interpolators <" + this + "> <" + from + '>');

        for (int i = 0; i < from.getNumSamples(); i++) {
            new Sample(from.getSample(i).data, from.getSample(i).time);
        }
    }

    public
    void printSamples(PrintStream p) {
        for (int i = 0; i < samples.size(); i++) {
            p.println(i + " " + samples.get(i));
        }
    }

    public
    int protect(int i) {
        if (i < 0) i = 0;
        if (i >= samples.size()) i = samples.size() - 1;
        return i;
    }

    //	public Vector3CubicInterpolatorDynamic downsampleWithError(int maxSamples, double minError) {
    //		Vector3CubicInterpolatorDynamic dynamic = new Vector3CubicInterpolatorDynamic();
    //		if (this.getNumSamples() == 0) return dynamic;
    //		dynamic.new Sample(this.getSample(0).data, this.getSample(0).time);
    //		if (this.getNumSamples() == 1) return dynamic;
    //		dynamic.new Sample(this.getSample(this.getNumSamples() - 1).data, this.getSample(this.getNumSamples() - 1).time);
    //		if (this.getNumSamples() == 2) return dynamic;
    //
    //		double min = Double.POSITIVE_INFINITY;
    //		while (min > minError && dynamic.getNumSamples() < maxSamples) {
    //			double e = Double.NEGATIVE_INFINITY;
    //			int eat = 0;
    //			Vector3 a1 = new Vector3();
    //			Vector3 a2 = new Vector3();
    //
    //			for (int i = 0; i < this.getNumSamples(); i++) {
    //				this.getValue(this.getSample(i).time, a1);
    //				dynamic.getValue(this.getSample(i).time, a2);
    //				double eHere = a1.distanceFrom(a2);
    //				if (eHere > e) {
    //					e = eHere;
    //					eat = i;
    //				}
    //			}
    //
    //			dynamic.new Sample(this.getSample(eat).data, this.getSample(eat).time);
    //			min = e;
    //		}
    //		return dynamic;
    //
    //	}

    public
    void removeSample(int i) {
        samples.remove(i);
        if ((indexAfter == i) || (indexBefore == i) || (indexNext == i) || (indexNow == i)) cacheInvalid = true;
    }

    public
    void removeSample(Sample sample) {
        int n = Collections.binarySearch(samples, sample, comparator);
        if (n >= 0) {
            removeSample(n);
        }
        else throw new ArrayIndexOutOfBoundsException(" couldn't find sample <" + sample + "> from <" + samples + '>');
    }

    public
    void resort() {
        cacheInvalid = true;
        Collections.sort(samples, comparator);
    }

    public
    CubicInterpolatorDynamic_double<T> setLinear(boolean linear) {
        this.linear = linear;
        return this;
    }

    public
    void startAtZero() {
        if (samples.size() < 1) return;
        double start = getSample(0).time;
        for (int i = 0; i < samples.size(); i++) {
            getSample(i).time -= start;
        }
        cacheInvalid = true;
    }


    @Override
    public
    String toString() {
        return "v3cubic:" + this.getNumSamples() + '(' + this.getStartTime() + " -> " + this.getEndTime() + ')';
    }

    public
    void trimStartTo(int maxSamples) {
        if (samples.size() > maxSamples)
            samples = new ArrayList(samples.subList(samples.size() - maxSamples, samples.size()));
    }

    private
    void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        comparator = new Comparator<Sample>() {

            public
            int compare(Sample s1, Sample s2) {
                return (s1.time < s2.time) ? -1 : ((s1.time > s2.time) ? 1 : 0);
            }
        };
    }

    private
    void validateCache(double time) {

        if (!cacheInvalid) {
            if ((time >= now) && (time <= next)) {
                return;
            }
            else {
                if ((time >= next) && (time <= after)) {
                    indexBefore = indexNow;
                    indexNow = indexNext;
                    indexNext = indexAfter;
                    indexAfter = protect(indexAfter + 1);

                    before = now;
                    now = next;
                    next = after;
                    after = getSample(indexAfter).time;
                    duration = next - now;
                    if (duration < 1e-10) duration = 1;

                    return;
                }
            }
        }

        // from scratch
        temp.time = time;
        int n = Collections.binarySearch(samples, temp, comparator);
        if (n < 0) n = -n - 2;


        indexBefore = protect(n - 1);
        indexNow = protect(n);
        indexNext = protect(n + 1);
        indexAfter = protect(n + 2);

        before = getSample(indexBefore).time;
        now = getSample(indexNow).time;
        next = getSample(indexNext).time;
        after = getSample(indexAfter).time;

        duration = next - now;
        if (indexNow == indexNext) {
            duration = Math.max(now - before, after - next);
        }

        cacheInvalid = false;
    }

}