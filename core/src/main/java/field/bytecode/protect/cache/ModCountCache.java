package field.bytecode.protect.cache;

import field.namespace.generic.Bind.iFunction;

import java.util.List;
import java.util.WeakHashMap;


/**
 * there are more efficient ways of doing this if the cache rarely changes. In this case, I should push the dirty flags into this structure
 */
public class ModCountCache<T, V> {

	private final iGetModCount<T> modCount;

    WeakHashMap<T, CacheRecord<V>> cache = new WeakHashMap<T, CacheRecord<V>>();

	private final iFunction<V, T> defaultOr;

	public ModCountCache(iGetModCount<T> modCount) {
		this.modCount = modCount;
		this.defaultOr = null;
	}

	public ModCountCache(iGetModCount<T> modCount, iFunction<V, T> defaultOr) {
		this.modCount = modCount;
		this.defaultOr = defaultOr;
	}

	static public <T extends iGetModCount<T>> iGetModCount<T> askKey(Class<T> f) {
		return new iGetModCount<T>(){
			public int countFor(T t) {
				return t.countFor(t);
			}
		};
	}
	
	static public <T extends iGetModCount<T>> iGetModCount<List<T>> askListKey(Class<T> f) {
		return new iGetModCount<List<T>>(){
			public int countFor(List<T> t) {
				int z = 0;
                for (T tt : t) {
                    z += tt.countFor(tt);
                }
				return z;
			}
		};
	}
	

	public V get(T t, iFunction<V, T> or) {
		CacheRecord<V> r = cache.get(t);
		if (r == null) {
			V v = or.f(t);
			r = new CacheRecord<V>();
			r.value = v;
			r.atCount = modCount.countFor(t);
			cache.put(t, r);
			return v;
		}

		int nmc = modCount.countFor(t);
		if (r.atCount != nmc) {
			r.atCount = nmc;
			r.value = or.f(t);
		}

		return r.value;
	}

	public V get(T t) {
		CacheRecord<V> r = cache.get(t);
		if (r == null) {
			V v = defaultOr.f(t);
			r = new CacheRecord<V>();
			r.value = v;
			r.atCount = modCount.countFor(t);
			cache.put(t, r);
			return v;
		}

		int nmc = modCount.countFor(t);
		if (r.atCount != nmc) {
			r.atCount = nmc;
			r.value = defaultOr.f(t);
		}

		return r.value;
	}

}
