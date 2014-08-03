package field.graphics.core.scene;

import field.graphics.core.pass.IPass;
import field.math.graph.IMutable;

/**
 * this is an interface for something that can go into a multipass scene list
 */
public
interface ISceneListElement extends IMutable<ISceneListElement> {
    public
    IPass requestPass(IPass pass);

    public
    IPass requestPassAfter(IPass pass);

    public
    IPass requestPassBefore(IPass pass);

    public
    IPass requestPassAfterAndBefore(IPass after, IPass before);


    /**
     * main entry point, do your work for iPass 'p' here
     */
    public
    void performPass(IPass p);
}
