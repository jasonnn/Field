package field.graphics.ci;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * low-level natives for CoreImage->Java Support
 *
 * @author marc
 */
public
class CoreImage {
    static {
        try {
            System.loadLibrary("FieldCoreImage");
        } catch (Throwable t) {
            System.err.println(" ** no core image **");
        }
    }

    // uses the current context
    public native
    long context_createOpenGLCIContextNow();

    public native
    long context_createBitmapCIContextNow();


    // draws an image into a context
    public native
    void context_drawImageNow(long context,
                              long image,
                              float x,
                              float y,
                              float dw,
                              float dh,
                              float sx,
                              float sy,
                              float srcw,
                              float srch);

    // draws an image into a float buffer (can do this any time, or only inside gl draw?)
    public native
    void context_drawImageToFloatBuffer(long context, long image, FloatBuffer buffer, int width, int height);

    public native
    void context_drawImageToByteBuffer(long context, long image, ByteBuffer buffer, int width, int height);

    public native
    long filter_createGenericWithText(String name, String args);

    // creates a filter from a "name"
    public native
    long filter_createWithName(String name);

    public native
    long filter_getImage(long filter, String key);

    public native
    String filter_getInputKeys(long filter);

    public native
    String filter_getOutputKeys(long filter);


    public native
    long filter_release(long filter);

    public native
    void filter_setDefaults(long filter);

    public native
    void filter_setValueFloat(long filter, String key, float value);

    public native
    void filter_setValueImage(long filter, String key, long image);

    public native
    void filter_setValueVector2(long filter, String key, float x, float y);

    public native
    void filter_setValueVector4(long filter, String key, float x, float y, float w, float h);

    // creates an image from a raw buffer
    public native
    long image_createWithARGBData(ByteBuffer bytes, int width, int height);

    // creates an image from a raw buffer
    public native
    long image_createWithARGBFData(Buffer bytes, int width, int height);

    // creates an image from an opengl texture
    public native
    long image_createWithTexture(int unit, int width, int height, boolean flipped);

    // creates an image from disk
    public native
    long image_createWithURL(String url);

    public native
    float image_getExtentHeight(long image);

    public native
    float image_getExtentWidth(long image);

    public native
    float image_getExtentX(long image);

    public native
    float image_getExtentY(long image);

    public native
    long image_release(long image);

    public native
    long image_retain(long image);

    public native
    long image_save(long context, long image, String url, String uti);


    public native
    long accumulator_createWithExtent(double x, double y, double w, double h, boolean isFloat);

    public native
    long accumulator_getOutputImage(long accumulator);

    public native
    void accumulator_setImage(long accumulator, long image, double x, double y, double w, double h);

    public native
    void accumulator_release(long accumulator);


}
