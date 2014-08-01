package field.graphics.imageprocessing;

import field.graphics.core.Base;
import field.graphics.core.BasicUtilities;
import field.math.abstraction.IProvider;

public
interface iImageProcessor {

    public abstract
    void addChild(Base.ISceneListElement e);

    public abstract
    IProvider<Integer> getOutput(int num);

    public abstract
    BasicUtilities.TwoPassElement getOutputElement(int num);

}