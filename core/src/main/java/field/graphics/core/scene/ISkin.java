package field.graphics.core.scene;

import field.math.abstraction.IInplaceProvider;
import field.math.linalg.iCoordinateFrame;

/**
 * Created by jason on 8/2/14.
 */
public
interface ISkin extends ISceneListElement {
    // this vertex, has this weight on this bone
    public
    void addBoneWeightInfo(int vertexIndex, float weight, int boneIndex);

    // this bone is this transform
    public
    void setBone(int boneIndex, IInplaceProvider<iCoordinateFrame.iMutable> frame);

    // we've finished setting the weights for this vertex
    public
    void normalizeBoneWeights(int vertexIndex, float scaleFactor);
}
