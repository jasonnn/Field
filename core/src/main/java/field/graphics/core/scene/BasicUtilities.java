package field.graphics.core.scene;

/**
 * some good, useful scenegraph elements that you might be interested in,
 * including:
 * <p/>
 * clearing the screen, blending the screen ('motion' blur), controlling stencil
 * state (also useful with 'motion' blur);
 * <p/>
 * and:
 * <p/>
 * important base classes for making SceneListElements (BasicOnePassElement) and
 * BasicSceneList (BasicOnePassList) subclasses
 */
public
class BasicUtilities {

    // for debugging only
    public static final boolean thinState = false;

    // public static FKey phase = new FKey("blurPhase").rootSet(0);
    //
    // public static FKey freq = new FKey("blurFreq").rootSet(1);

    // /**
    // * a base class for activating a stencil command set and then
    // resetting
    // * it.
    // */
    // static public class GenericStencilControl extends TwoPassElement {
    //
    // // saved state
    // int sfunc, sref, smask, sfail, sdfail, sdpass;
    //
    // boolean senabled = false;
    //
    // int func, ref, mask, fail, dfail, dpass;
    //
    // boolean noColour;
    //
    // public GenericStencilControl(int function, int ref, int mask, int
    // op_fail, int op_dfail, int op_pass, boolean noColour) {
    // super("unnamed", StandardPass.preRender, StandardPass.postRender);
    // this.func = function;
    // this.ref = ref;
    // this.mask = mask;
    // this.fail = op_fail;
    // this.dfail = op_dfail;
    // this.dpass = op_pass;
    // this.noColour = noColour;
    // }
    //
    // @Override
    // public void post() {
    // // restore state
    // glStencilFunc(sfunc, sref, smask);
    // glStencilOp(sfail, sdfail, sdpass);
    // if (!senabled)
    // glDisable(GL_STENCIL_TEST);
    // if (noColour) {
    // glColorMask(true, true, true, true);
    // glDepthMask(true);
    // }
    // }
    //
    // @Override
    // public void pre() {
    // // save state
    // int[] i = new int[1];
    // glGetIntegerv(GL_STENCIL_FUNC, i, 0);
    // sfunc = i[0];
    // glGetIntegerv(GL_STENCIL_REF, i, 0);
    // smask = i[0];
    // glGetIntegerv(GL_STENCIL_VALUE_MASK, i, 0);
    // smask = i[0];
    // glGetIntegerv(GL_STENCIL_FAIL, i, 0);
    // sfail = i[0];
    // glGetIntegerv(GL_STENCIL_PASS_DEPTH_FAIL, i, 0);
    // sdfail = i[0];
    // glGetIntegerv(GL_STENCIL_PASS_DEPTH_PASS, i, 0);
    // sdpass = i[0];
    // glGetIntegerv(GL_STENCIL_TEST, i, 0);
    // senabled = i[0] != 1;
    //
    // // push state
    // glStencilFunc(func, ref, mask);
    // glStencilOp(fail, dfail, dpass);
    // glEnable(GL_STENCIL_TEST);
    // glEnable(GL_DEPTH_TEST);
    // if (noColour) {
    // glColorMask(false, false, false, false);
    // glDepthMask(false);
    // }
    // }
    //
    // @Override
    // public void setup() {
    // }
    // }
    //
    // @Woven
    // static public class InSubContext extends BasicSceneList {
    // private final String name;
    //
    // protected GL2 gl = null;
    //
    // protected GLU glu = null;
    //
    // Set preRender = new HashSet();
    //
    // Set postRender = new HashSet();
    //
    // Base.StandardPass prePass;
    //
    // Base.StandardPass postPass;
    //
    // Object first = new Object();
    //
    // public InSubContext(String name) {
    // this.name = name;
    // this.prePass = Base.StandardPass.preTransform;
    // this.postPass = Base.StandardPass.preDisplay;
    // }
    //
    // @Override
    // public void notifyAddParent(iMutable<iSceneListElement> newParent) {
    // super.notifyAddParent(newParent);
    // preRender.add(preRender.add(((iSceneListElement)
    // newParent).requestPass(prePass)));
    // postRender.add(postRender.add(((iSceneListElement)
    // newParent).requestPass(postPass)));
    // }
    //
    // // no annotation
    // @Override
    // public void performPass(iPass p) {
    // gl = BasicContextManager.getGl();
    // glu = BasicContextManager.getGlu();
    //
    // assert (glGetError() == 0);
    // if ((p == null) || (preRender.contains(p))) {
    // if (!BasicContextManager.isValid(first)) {
    // BasicContextManager.markAsValidInThisContext(first);
    // assert (glGetError() == 0);
    // setup();
    // assert (glGetError() == 0);
    // }
    // assert (glGetError() == 0);
    // pre();
    // assert (glGetError() == 0) : this.getClass();
    // } else if ((p == null) || (postRender.contains(p))) {
    // assert (glGetError() == 0);
    // post();
    // assert (glGetError() == 0);
    // }
    // super.performPass(p);
    // }
    //
    // protected void post() {
    // Base.context.end(name);
    // }
    //
    // protected void pre() {
    // Base.context.begin(name);
    // }
    //
    // protected void setup() {
    // BasicContextManager.putId(this, 0);
    // }
    // }

    // static public class SetMatrix extends BasicUtilities.TwoPassElement
    // implements iSceneListElement {
    //
    // iInplaceProvider<iCoordinateFrame> provider;
    //
    // CoordinateFrame coordinateFrameNow = new CoordinateFrame();
    //
    // float matrix[] = null;
    //
    // public SetMatrix(float[] matrix) {
    // super("setmatrix", StandardPass.preRender, StandardPass.postRender);
    // setMatrix(matrix);
    // }
    //
    // public SetMatrix(iInplaceProvider<iCoordinateFrame> provider) {
    // super("set matrix", StandardPass.preRender, StandardPass.postRender);
    // this.provider = provider;
    // }
    //
    // @Override
    // public void post() {
    // glPopMatrix();
    // }
    //
    // /**
    // * main entry point, do your work for Pass 'p' here
    // */
    // @Override
    // public void pre() {
    // if (thinState)
    // return;
    //
    // if (provider != null) {
    // provider.get(coordinateFrameNow);
    //
    // // possible
    // // ordering
    // // problem!
    // matrix = coordinateFrameNow.getMatrix(null).get(matrix);
    // }
    // glPushMatrix();
    // glMultMatrixf(matrix, 0);
    // // glMatrixMode(GL_MATRIX0_ARB);
    // }
    //
    // public void setMatrix(float[] matrix) {
    // if (this.matrix == null)
    // this.matrix = new float[16];
    // for (int i = 0; i < 16; i++)
    // this.matrix[i] = matrix[i];
    // }
    //
    // @Override
    // public void setup() {
    // }
    // }

    // static public class Shadow extends GenericStencilControl {
    //
    // public Shadow() {
    // super(GL_NOTEQUAL, 0, ~0, GL_KEEP, GL_KEEP, GL_KEEP, false);
    // }
    //
    // @Override
    // public void post() {
    // super.post();
    // glDepthFunc(GL_LESS);
    // }
    //
    // @Override
    // public void pre() {
    // super.pre();
    // glDepthFunc(GL_ALWAYS);
    // }
    // }
    //
    // static public class ShadowBack extends GenericStencilControl {
    //
    // public ShadowBack() {
    // super(GL_ALWAYS, 0, ~0, GL_KEEP, GL_KEEP, GL_INVERT, true);
    // }
    //
    // @Override
    // public void post() {
    // super.post();
    // glCullFace(GL_BACK);
    // }
    //
    // @Override
    // public void pre() {
    // super.pre();
    // glCullFace(GL_FRONT);
    // }
    // }
    //
    // static public class ShadowFront extends GenericStencilControl {
    //
    // public ShadowFront() {
    // super(GL_ALWAYS, 0, ~0, GL_KEEP, GL_KEEP, GL_INVERT, true);
    // }
    //
    // @Override
    // public void post() {
    // super.post();
    // glCullFace(GL_BACK);
    // }
    //
    // @Override
    // public void pre() {
    // super.pre();
    // glCullFace(GL_BACK);
    // }
    // }

    static public boolean back = false;
}