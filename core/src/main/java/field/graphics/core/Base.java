package field.graphics.core;

import field.namespace.context.SimpleContextTopology;

/**
 * base interfaces for new graphics system, nothing too specific here, just the interfaces and new principles that do not exist somewhere in the older graphics system for implementations see subpackages
 * <p/>
 * work in progress need phantomqueue for natively allocated buffers - otherwise we have a leak. nnn
 */
public
class Base {

    //static public final iContextTree context = new LocalContextTree();
    public static final SimpleContextTopology context = SimpleContextTopology.newInstance();

    public static final boolean trace = false;


}
