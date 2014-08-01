package field.graphics.core;

import field.bytecode.protect.annotations.HiddenInAutocomplete;
import field.core.util.PythonCallableMap;
import field.graphics.core.Base.LocalPass;
import field.graphics.core.Base.iAcceptsSceneListElement;
import field.launch.IUpdateable;
import field.math.graph.NodeImpl;

import java.util.*;

/**
 * children are scenelist elements /** use the addChild(...) etc to add things
 * to this class
 */
public
class BasicSceneList extends NodeImpl<Base.ISceneListElement> implements Base.ISceneListElement, IUpdateable,
                                                                    iAcceptsSceneListElement {


    public
    interface iGlobalEarly {
        public
        void early();
    }

    private static final long serialVersionUID = 1L;

    /* implementation of scenelist */
    protected List<Base.IPass> passList = new ArrayList<Base.IPass>();

    /**
     * use the addChild(...) etc to add things to this class
     */

    protected boolean passListIsUnsorted = false;

    protected Map standards = new HashMap();

    protected Comparator<Base.IPass> passComparator = new Comparator<Base.IPass>() {
        public
        int compare(Base.IPass o1, Base.IPass o2) {
            if (o1.getValue() > o2.getValue()) return 1;
            return -1;
        }
    };

    public
    void addChildOnce(Base.ISceneListElement newChild) {
        if (!isChild(newChild)) super.addChild(newChild);
    }

    public
    void addChild(Base.ISceneListElement newChild) {
        super.addChild(newChild);
    }


    public
    void removeChild(Base.ISceneListElement newChild) {
        super.removeChild(newChild);
    }

    int xxx = 0;

    public
    BasicSceneList() {
    }

    @HiddenInAutocomplete
    public
    void listElements(String xx) {
        int i;
        for (Base.ISceneListElement element : getChildren()) {
            String nn = element.getClass().toString();
            nn = nn.substring(nn.lastIndexOf('.'));
            if (element instanceof BasicSceneList) ((BasicSceneList) element).listElements(xx + xx);
        }
    }

    @HiddenInAutocomplete
    public
    void performPass(Base.IPass p) {
        // update();

        // if (passList.contains(p)) for (iSceneListElement element :
        // getChildren())
        // element.performPass(p);
    }

    @HiddenInAutocomplete
    public
    Base.IPass requestPass(Base.IPass pass) {
        if (!passList.contains(pass)) passList.add(pass);
        Collections.sort(passList, passComparator);
        return pass;
    }

    @HiddenInAutocomplete
    public
    Base.IPass requestPassAfter(Base.IPass pass) {
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
    Base.IPass requestPassAfterAndBefore(Base.IPass after, Base.IPass before) {
        return requestPassBefore(before);
    }

    @HiddenInAutocomplete
    public
    Base.IPass requestPassBefore(Base.IPass pass) {
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
        for (Base.IPass pass : passList) {
            for (Base.ISceneListElement element : getChildren()) {
                element.performPass(pass);
            }
        }
    }

    @HiddenInAutocomplete
    public
    void updateFromButNotIncluding(Base.IPass from) {
        for (Base.IPass pass : passList) {
            if (pass.isLaterThan(from)) {
                // System.err.println("from pass <" + pass +
                // "> <" + System.identityHashCode(this) + "> <"
                // + this.getClass() + ">");
                for (Base.ISceneListElement element : new ArrayList<Base.ISceneListElement>(getChildren())) {
                    element.performPass(pass);
                }
            }
        }
    }

    @HiddenInAutocomplete
    public
    void updateFromButNotIncluding(Base.IPass from, Base.IPass upToAndIncluding) {
        for (Base.IPass pass : passList) {
            if (pass.isLaterThan(from)) {
                if (!pass.isEarlierThan(upToAndIncluding) && !pass.equals(upToAndIncluding)) return;

                // System.err.println("from pass <" + pass +
                // "> to <"+upToAndIncluding+"> <" +
                // System.identityHashCode(this) + "> <" +
                // this.getClass() + ">");
                for (Base.ISceneListElement element : getChildren())
                    element.performPass(pass);
            }
            if (!pass.isEarlierThan(upToAndIncluding)) return;
        }
    }

    // these are useful for subclasses of this thing that want to do
    // something cleaver
    @HiddenInAutocomplete
    public
    void updateUpToAndIncluding(Base.IPass to) {
        // System.err.println(" ordered pass list is <"+passList+">");
        for (Base.IPass pass : passList) {
            if (!pass.isEarlierThan(to) && !pass.equals(to)) return;

            // System.err.println("upto pass <" + pass + "> <" +
            // System.identityHashCode(this) + "> <" +
            // this.getClass() + ">");
            for (Base.ISceneListElement element : new ArrayList<Base.ISceneListElement>(getChildren())) {
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
    boolean isChild(Base.ISceneListElement e) {
        return getChildren().contains(e);
    }

    When when;

    public
    PythonCallableMap add(Base.StandardPass pass) {
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
        for (Base.ISceneListElement e : getChildren()) {
            if (e instanceof iGlobalEarly) {
                ((iGlobalEarly) e).early();
            }
            if (e instanceof BasicSceneList) {
                ((BasicSceneList) e).performGlobalEarly();
            }
        }

    }

}
