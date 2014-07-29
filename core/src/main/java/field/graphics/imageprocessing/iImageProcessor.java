package field.graphics.imageprocessing;

import field.graphics.core.Base.iSceneListElement;
import field.graphics.core.BasicUtilities;
import field.math.abstraction.IProvider;

public
interface iImageProcessor {

    public abstract
    void addChild(iSceneListElement e);

    public abstract
    IProvider<Integer> getOutput(int num);

    public abstract
    BasicUtilities.TwoPassElement getOutputElement(int num);

}