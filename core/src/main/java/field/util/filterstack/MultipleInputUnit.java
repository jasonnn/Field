package field.util.filterstack;

import field.math.abstraction.IFloatProvider;
import field.math.abstraction.IProvider;
import field.util.collect.tuple.Pair;

import java.util.*;
import java.util.Map.Entry;


public
class MultipleInputUnit<T> extends Unit<T> {

    public
    MultipleInputUnit(String name) {
        super(name);
    }

    protected IFloatProvider main;

    Map<String, Pair<IProvider<T>, IFloatProvider>> others =
            new LinkedHashMap<String, Pair<IProvider<T>, IFloatProvider>>();

    public
    MultipleInputUnit<T> setMainWeight(IFloatProvider main) {
        this.main = main;
        return this;
    }

    public
    MultipleInputUnit<T> setAdditional(String name, IProvider<T> in, IFloatProvider amount) {
        if (in == null) others.remove(name);
        else {
            others.put(name, new Pair<IProvider<T>, IFloatProvider>(in, amount));
        }
        return this;
    }

    @Override
    protected
    T filter(T input) {
        List<Float> am = new ArrayList<Float>();
        List<T> val = new java.util.ArrayList<T>();

        float tot = 0;
        val.add(input);
        if (main != null) am.add(tot = main.evaluate());
        else am.add(tot = 1f);

        Set<Entry<String, Pair<IProvider<T>, IFloatProvider>>> es = others.entrySet();
        for (Entry<String, Pair<IProvider<T>, IFloatProvider>> e : es) {
            T v = e.getValue().left.get();
            if (v != null) {
                val.add(v);
                float q = e.getValue().right.evaluate();
                tot += q;
                am.add(q);
            }
        }

        if (tot == 0) {
            if (lastOutput != null) return lastOutput;
            else return input;
        }

        T o = null;
        for (int i = 0; i < val.size(); i++) {
            o = filterStack.addImpl(val.get(i), am.get(i), o);
        }
        return o;
    }
}
