package field.graphics.imageprocessing;

import field.graphics.core.scene.ISceneListElement;
import field.graphics.core.scene.TwoPassElement;
import field.math.abstraction.IProvider;

public
interface iImageProcessor {

    public abstract
    void addChild(ISceneListElement e);

    public abstract
    IProvider<Integer> getOutput(int num);

    public abstract
    TwoPassElement getOutputElement(int num);

}