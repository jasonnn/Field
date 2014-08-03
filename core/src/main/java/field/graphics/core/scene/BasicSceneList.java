package field.graphics.core.scene;

import field.bytecode.protect.annotations.HiddenInAutocomplete;
import field.core.util.PythonCallableMap;
import field.graphics.core.When;
import field.graphics.core.pass.IPass;
import field.graphics.core.pass.LocalPass;
import field.graphics.core.pass.StandardPass;
import field.launch.IUpdateable;
import field.math.graph.NodeImpl;

import java.util.*;

/**
 * children are scenelist elements /** use the addChild(...) etc to add things
 * to this class
 */
public
class BasicSceneList extends NodeImpl<ISceneListElement> implements ISceneListElement,
                                                                    IUpdateable,
                                                                    IAcceptsSceneListElement {


    public static
    interface iGlobalEarly {
        public
        void early();
    }

    private static final long serialVersionUID = 1L;

    /* implementation of scenelist */
    protected List<IPass> passList = new ArrayList<IPass>();

    /**
     * use the addChild(...) etc to add things to this class
     */

    protected boolean passListIsUnsorted = false;

    protected Map standards = new HashMap();

    protected Comparator<IPass> passComparator = new Comparator<IPass>() {
        public
        int compare(IPass o1, IPass o2) {
            if (o1.getValue() > o2.getValue()) return 1;
            return -1;
        }
    };

    public
    void addChildOnce(ISceneListElement newChild) {
        if (!isChild(newChild)) super.addChild(newChild);
    }

    public
    void addChild(ISceneListElement newChild) {
        super.addChild(newChild);
    }


    public
    void removeChild(ISceneListElement newChild) {
        super.removeChild(newChild);
    }

    int xxx = 0;

    public
    BasicSceneList() {
    }

    @HiddenInAutocomplete
    public
    void listElements(String xx) {

        for (ISceneListElement element : getChildren()) {
//            String nn = element.getClass().toString();
//            nn = nn.substring(nn.lastIndexOf('.'));
            if (element instanceof BasicSceneList) ((BasicSceneList) element).listElements(xx + xx);
        }
    }

    @HiddenInAutocomplete
    public
    void performPass(IPass p) {
        // update();

        // if (passList.contains(p)) for (iSceneListElement element :
        // getChildren())
        // element.performPass(p);
    }

    @HiddenInAutocomplete
    public
    IPass requestPass(IPass pass) {
        if (!passList.contains(pass)) passList.add(pass);
        Collections.sort(passList, passComparator);
        return pass;
    }

    @HiddenInAutocomplete
    public
    IPass requestPassAfter(IPass pass) {
        // bracket this pass
        int index = passList.indexOf(pass);
        if (index == -1) throw new IllegalArgumentException(" ( couldn't find pass <" + pass + "> ) ");
        float above;
        float below;
        if (index == (passList.size() - 1)) above = 1 + (below = passList.get(passList.size() - 1).getValue());
        else {
            above = passList.get(index).getValue();
            below = passList.get(index + 1).getValue();
        }
        LocalPass ret;
        passList.add(index + 1, ret = new LocalPass((above + below) / 2));
        return ret;
    }

    @HiddenInAutocomplete
    public
    IPass requestPassAfterAndBefore(IPass after, IPass before) {
        return requestPassBefore(before);
    }

    @HiddenInAutocomplete
    public
    IPass requestPassBefore(IPass pass) {
        // bracket this pass
        int index = passList.indexOf(pass);
        if (index == -1) throw new IllegalArgumentException(" ( couldn't find pass <" + pass + "> ) ");
        index -= 1;

        float above;
        float below;

        if (index == (passList.size() - 1)) above = 1 + (below = passList.get(passList.size() - 1).getValue());
        else {
            above = passList.get(index).getValue();
            below = passList.get(index + 1).getValue();
        }
        LocalPass ret;
        passList.add(index + 1, ret = new LocalPass((above + below) / 2));
        return ret;
    }

    @HiddenInAutocomplete
    public
    void update() {
        for (IPass pass : passList) {
            for (ISceneListElement element : getChildren()) {
                element.performPass(pass);
            }
        }
    }

    @HiddenInAutocomplete
    public
    void updateFromButNotIncluding(IPass from) {
        for (IPass pass : passList) {
            if (pass.isLaterThan(from)) {
                // System.err.println("from pass <" + pass +
                // "> <" + System.identityHashCode(this) + "> <"
                // + this.getClass() + ">");
                for (ISceneListElement element : new ArrayList<ISceneListElement>(getChildren())) {
                    element.performPass(pass);
                }
            }
        }
    }

    @HiddenInAutocomplete
    public
    void updateFromButNotIncluding(IPass from, IPass upToAndIncluding) {
        for (IPass pass : passList) {
            if (pass.isLaterThan(from)) {
                if (!pass.isEarlierThan(upToAndIncluding) && !pass.equals(upToAndIncluding)) return;

                // System.err.println("from pass <" + pass +
                // "> to <"+upToAndIncluding+"> <" +
                // System.identityHashCode(this) + "> <" +
                // this.getClass() + ">");
                for (ISceneListElement element : getChildren())
                    element.performPass(pass);
            }
            if (!pass.isEarlierThan(upToAndIncluding)) return;
        }
    }

    // these are useful for subclasses of this thing that want to do
    // something cleaver
    @HiddenInAutocomplete
    public
    void updateUpToAndIncluding(IPass to) {
        // System.err.println(" ordered pass list is <"+passList+">");
        for (IPass pass : passList) {
            if (!pass.isEarlierThan(to) && !pass.equals(to)) return;

            // System.err.println("upto pass <" + pass + "> <" +
            // System.identityHashCode(this) + "> <" +
            // this.getClass() + ">");
            for (ISceneListElement element : new ArrayList<ISceneListElement>(getChildren())) {
                // System.err.println("       on <" + element +
                // "> <" + element.getClass() + "> in ....");
                element.performPass(pass);
                // System.err.println("       on <" + element +
                // "> <" + element.getClass() + "> out ....");
            }
            if (!pass.isEarlierThan(to)) return;
        }
    }

    public
    boolean isChild(ISceneListElement e) {
        return getChildren().contains(e);
    }

    When when;

    public
    PythonCallableMap add(StandardPass pass) {
        return getWhen().getMap(pass);
    }

    public
    PythonCallableMap add(int pass) {
        return getWhen().getMap(pass);
    }

    @HiddenInAutocomplete
    public
    When getWhen() {
        if (when == null) when = new When(this);
        return when;
    }


    @HiddenInAutocomplete
    public
    void performGlobalEarly() {
        for (ISceneListElement e : getChildren()) {
            if (e instanceof iGlobalEarly) {
                ((iGlobalEarly) e).early();
            }
            if (e instanceof BasicSceneList) {
                ((BasicSceneList) e).performGlobalEarly();
            }
        }

    }

}
