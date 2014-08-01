package field.namespace.change;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author marc Created on May 6, 2003
 */
public
class SimpleChangable implements IChangable, Serializable {
//TODO move up
    private
    class ModCount implements IModCount, Serializable {

        transient int dirty = -1;

        transient Object data = null;

        transient IRecompute default_recompute;

        IModCount[] localChain = new IModCount[0];

        public
        IModCount clear(Object data) {
            this.data = data;
            dirty = count;
            for (int i = 0; i < localChain.length; i++) {
                localChain[i].clear(null);
            }
            return this;
        }

        public
        Object data() {
            if (default_recompute != null) return data(default_recompute);

            return ((dirty != count) | checkChain() | checkLocalChain(localChain)) ? null : data;
        }

        public
        Object data(IRecompute recomp) {
            if ((dirty != count) | checkChain() | checkLocalChain(localChain)) {
                Object r = recomp.recompute();
                clear(r);
                return r;
            }
            else return data;
        }

        public
        boolean hasChanged() {
            boolean d = (dirty != count) | checkChain() | checkLocalChain(localChain);
            return d;
        }

        public
        IModCount localChainWith(IModCount[] localChain) {
            this.localChain = localChain;
            return this;
        }

        public
        IModCount setRecompute(IRecompute r) {
            this.default_recompute = r;
            return this;
        }

        @Override
        public
        String toString() {
            return "child of <" + SimpleChangable.this.toString() + '>';
        }

        private
        boolean checkLocalChain(IModCount[] localChain2) {

            for (int i = 0; i < localChain2.length; i++) {
//				System.err.println("localChain <"+i+"> is <"+localChain2[i]+"> is <"+localChain2[i].hasChanged()+">");
//				if (localChain2[i].hasChanged()) new Exception().printStackTrace();
                if (localChain2[i].hasChanged()) return true;
            }
            return false;
        }
    }

    transient Map map = new WeakHashMap();

    int count = 0;

    IModCount[] chainCount;

    public
    SimpleChangable chainWith(IModCount[] chainCount) {
        this.chainCount = chainCount;
        return this;
    }

    public
    void dirty() {
        count++;
    }

    public
    IModCount getModCount(Object withRespectTo) {
        IModCount count = (IModCount) map.get(withRespectTo);
        if (count == null) {
            map.put(withRespectTo, count = new ModCount());
        }
        if (map.isEmpty()) throw new IllegalArgumentException();

        return count;
    }

    private
    void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        map = new WeakHashMap((Map) stream.readObject());
    }

    private
    void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(new HashMap(map));
    }

    protected
    boolean checkChain() {
        if (chainCount == null) return false;
        boolean ret = false;
        for (int i = 0; i < chainCount.length; i++) {
            if (chainCount[i].hasChanged()) {
                dirty();

                //?? why was this here? I'm sure it has something to do with diagram
                //chainCount[i].clear(null);

                // I'm going to try this instead
                chainCount[i].data();
                ret = true;
            }
        }
        return ret;
    }

}