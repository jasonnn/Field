package field.graphics.qt;

import field.graphics.core.Base;
import field.graphics.core.BasicContextManager;
import field.graphics.core.BasicUtilities;

import java.nio.Buffer;

/**
 * faster (apparently) if you are just going to put it on the screen
 */
public
class QTKitVideo {
    static {
        /*
		 * this will fail on (64 bit) Java 6
		 */
        try {
            System.loadLibrary("FieldVideo_32");
        } catch (java.lang.UnsatisfiedLinkError link) {
            System.loadLibrary("FieldVideo");
        }
    }

    public long handle;

    public
    QTKitVideo() {
        handle = nativeHandle();
    }

//	public QTKitVideo openMovie(String path) {
//		openMovie(handle2, path);
//		return this;
//	}

    public
    QTKitVideo setRate(float rate) {
        setRate(handle, rate);
        return this;
    }

    native
    void setRate(long handle, float rate);

    public
    float getDuration() {
        return getDuration(handle);
    }

    native
    float getDuration(long handle);

    public
    void setPosition(float position) {
        setPosition(handle, position);
    }

    public
    float getPosition() {
        return getPosition(handle);
    }

    native
    void setPosition(long handle, float position);

    native
    float getPosition(long handle);

    private native
    long nativeHandle();

    public native
    void openMovie(long handle, String path);

    public native
    int bind(long handle);

    public native
    int bindInto(long handle, Buffer memory, int width, int height);

    public native
    void unbind(long handle);

    public native
    void cleanUp(long handle);

    public
    class Element extends BasicUtilities.TwoPassElement {
        public
        Element(String name) {
            super(name, Base.StandardPass.preRender, Base.StandardPass.postRender);
        }

        @Override
        protected
        void setup() {
            BasicContextManager.putId(this, 0);
        }

        @Override
        protected
        void pre() {
            bind(handle);
        }

        @Override
        protected
        void post() {
            unbind(handle);
        }
    }

    public
    int getHeight() {
        return getHeight(handle);
    }

    private native
    int getHeight(long handle);

    public
    int getWidth() {
        return getWidth(handle);
    }

    private native
    int getWidth(long handle);

}
