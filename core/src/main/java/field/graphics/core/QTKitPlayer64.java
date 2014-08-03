package field.graphics.core;

import field.graphics.core.pass.StandardPass;
import field.graphics.core.scene.TwoPassElement;
import field.graphics.qt.QTKitVideo;

public
class QTKitPlayer64 extends TwoPassElement {

    private final QTKitVideo video;

    public
    QTKitPlayer64(String file, int w, int height) {
        super("qtkit", StandardPass.preRender, StandardPass.postRender);
        video = new QTKitVideo();

        video.openMovie(video.handle, file);
        try {
            Thread.sleep(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println(" opening movie complete");
        video.setRate(0.0f);
    }

    @Override
    protected
    void post() {
        video.unbind(video.handle);
    }

    @Override
    protected
    void pre() {
        video.bind(video.handle);
    }

    @Override
    protected
    void setup() {
        BasicContextManager.putId(this, 0);
    }

}
