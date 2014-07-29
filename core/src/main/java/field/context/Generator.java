package field.context;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.ObservableElementList.Connector;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.Matcher;
import field.launch.IUpdateable;
import field.launch.Launcher;
import field.namespace.generic.IFunction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public
class Generator {

    public
    interface iTimeFor<T> {
        public
        double timeFor(T t);
    }

    public
    interface iChanged extends EventListener {
        public
        void update();
    }

    public
    interface iChangeInterface {
        public
        void register(iChanged c);

        public
        void unregister(iChanged c);
    }

    public static
    class Channel<X> implements List<X>, iTimeFor<X> {

        EventList<X> e;
        private final iTimeFor<? super X> t;

        List<EventList> needingDisposal = new ArrayList<EventList>();

        public
        Channel(iTimeFor<? super X> t, X... x) {
            this(t, Arrays.asList(x));
        }

        public
        Channel(iTimeFor<? super X> t, Collection<X> x) {
            this.t = t;
            EventList xe;
            if (x instanceof EventList) {
                xe = (EventList) x;
            }
            else if (x instanceof Channel) {
                xe = ((Channel<X>) x).e;
            }
            else {
                xe = GlazedLists.eventList(x);
                needingDisposal.add(xe);
            }

            ObservableElementList<X> oel = new ObservableElementList<X>(xe, new ChannelConnector<X>());
            needingDisposal.add(oel);

            e = new SortedList<X>(oel, comparatorFor(t));
            needingDisposal.add(e);
        }

        protected
        void finalize() throws Throwable {
            finalizeOnMainThread(needingDisposal);
        }

        public
        Channel<X> range(final double start, final double end) {
            FilterList<X> fl = new FilterList<X>(e, new Matcher<X>() {

                @Override
                public
                boolean matches(X item) {
                    return t.timeFor(item) < end && t.timeFor(item) >= start;
                }
            });
            Channel<X> m = new Channel<X>(t, fl);
            m.needingDisposal.add(fl);
            return m;
        }

        // public Channel<X> filter(Matcher<X> mm) {
        // FilterList<X> fl = new FilterList<X>(e, mm);
        // Channel<X> m = new Channel<X>(t, fl);
        // m.needingDisposal.add(fl);
        // return m;
        // }

        public
        Channel<X> filter(final IFunction<X, Boolean> mm) {
            FilterList<X> fl = new FilterList<X>(e, new Matcher<X>() {

                @Override
                public
                boolean matches(X item) {
                    return mm.apply(item);
                }
            });
            Channel<X> m = new Channel<X>(t, fl);
            m.needingDisposal.add(fl);
            return m;
        }

        public
        void add(int arg0, X arg1) {
            e.add(arg0, arg1);
        }

        public
        boolean add(X arg0) {
            return e.add(arg0);
        }

        public
        boolean addAll(Collection<? extends X> arg0) {
            return e.addAll(arg0);
        }

        public
        boolean addAll(int arg0, Collection<? extends X> arg1) {
            return e.addAll(arg0, arg1);
        }

        public
        void addListEventListener(ListEventListener<? super X> arg0) {
            e.addListEventListener(arg0);
        }

        public
        void clear() {
            e.clear();
        }

        public
        boolean contains(Object arg0) {
            return e.contains(arg0);
        }

        public
        boolean containsAll(Collection<?> arg0) {
            return e.containsAll(arg0);
        }

        public
        boolean equals(Object arg0) {
            return e.equals(arg0);
        }

        public
        X get(int arg0) {
            return e.get(arg0);
        }

        public
        int indexOf(Object arg0) {
            return e.indexOf(arg0);
        }

        public
        boolean isEmpty() {
            return e.isEmpty();
        }

        @NotNull
        public
        Iterator<X> iterator() {
            return e.iterator();
        }

        public
        int lastIndexOf(Object arg0) {
            return e.lastIndexOf(arg0);
        }

        @NotNull
        public
        ListIterator<X> listIterator() {
            return e.listIterator();
        }

        @NotNull
        public
        ListIterator<X> listIterator(int arg0) {
            return e.listIterator(arg0);
        }

        public
        X remove(int arg0) {
            return e.remove(arg0);
        }

        public
        boolean remove(Object arg0) {
            return e.remove(arg0);
        }

        public
        boolean removeAll(Collection<?> arg0) {
            return e.removeAll(arg0);
        }

        public
        boolean retainAll(Collection<?> arg0) {
            return e.retainAll(arg0);
        }

        public
        X set(int arg0, X arg1) {
            return e.set(arg0, arg1);
        }

        public
        int size() {
            return e.size();
        }

        @NotNull
        public
        List<X> subList(int arg0, int arg1) {
            return e.subList(arg0, arg1);
        }

        @NotNull
        public
        Object[] toArray() {
            return e.toArray();
        }

        @NotNull
        public
        <T> T[] toArray(T[] arg0) {
            return e.toArray(arg0);
        }

        public
        double timeFor(X t) {
            return this.t.timeFor(t);
        }

        public
        void dispose() {
            for (EventList n : needingDisposal) {
                n.dispose();
            }
            needingDisposal.clear();
            e = null;
        }

        public
        X head() {
            return get(0);
        }

        public
        X tail() {
            return get(this.size() - 1);
        }

        @Override
        public
        String toString() {
            return e.toString();
        }

        protected static final
        class ChannelConnector<X> implements Connector<X> {
            private ObservableElementList<X> inside;

            @Override
            public
            EventListener installListener(final X element) {
                if (element instanceof iChangeInterface) {
                    iChanged c = new iChanged() {

                        @Override
                        public
                        void update() {
                            inside.elementChanged(element);
                        }
                    };
                    ((iChangeInterface) element).register(c);
                    return c;
                }
                return null;
            }

            @Override
            public
            void uninstallListener(X element, EventListener listener) {
                if (element instanceof iChangeInterface) {
                    ((iChangeInterface) element).unregister((iChanged) listener);
                }
            }

            @Override
            public
            void setObservableElementList(ObservableElementList<? extends X> list) {
                this.inside = (ObservableElementList<X>) list;
            }
        }

    }

    static public
    <X> EventList<X> wrap(Collection<X> x, iTimeFor<? super X> t) {
        return new SortedList<X>(new ObservableElementList<X>(GlazedLists.eventList(x), new Connector<X>() {

            private ObservableElementList<X> inside;

            @Override
            public
            EventListener installListener(final X element) {
                if (element instanceof iChangeInterface) {
                    iChanged c = new iChanged() {

                        @Override
                        public
                        void update() {
                            inside.elementChanged(element);
                        }
                    };
                    ((iChangeInterface) element).register(c);
                    return c;
                }
                return null;
            }

            @Override
            public
            void uninstallListener(X element, EventListener listener) {
                if (element instanceof iChangeInterface) {
                    ((iChangeInterface) element).unregister((iChanged) listener);
                }
            }

            @Override
            public
            void setObservableElementList(ObservableElementList<? extends X> list) {
                this.inside = (ObservableElementList<X>) list;
            }
        }), comparatorFor(t));
    }

    static protected
    void finalizeOnMainThread(final List<EventList> needingDisposal) {
        Launcher.getLauncher().registerUpdateable(new IUpdateable() {

            @Override
            public
            void update() {
                for (EventList e : needingDisposal)
                    e.dispose();
                needingDisposal.clear();
                Launcher.getLauncher().deregisterUpdateable(this);
            }
        });
    }

    static public
    <X> EventList<X> subset(final double start, final double end, EventList<X> x, final iTimeFor<? super X> t) {
        return new FilterList<X>(x, new Matcher<X>() {

            @Override
            public
            boolean matches(X item) {
                return t.timeFor(item) < end && t.timeFor(item) >= start;
            }
        });
    }

    static public
    <T, X extends iTimeFor<T>> Comparator<T> comparatorFor(final X x) {
        return new Comparator<T>() {
            @Override
            public
            int compare(T o1, T o2) {

                double t1 = x.timeFor(o1);
                double t2 = x.timeFor(o2);

                return Double.compare(t1, t2);
            }
        };

    }
}
