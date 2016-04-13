package name.haoxin.wardbrdf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import name.haoxin.wardbrdf.action.CaptureCallBack;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLSurfaceView extends GLSurfaceView {
    private CustomGLRenderer renderer;
    private boolean renderOnRequest = true;
    private float previousX;
    private float previousY;
    private float[] touchFromVector = new float[4];
    private float[] touchToVector = new float[4];

    public CustomGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new CustomGLRenderer(context);
        setRenderer(renderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void capture(CaptureCallBack callBack) {
        renderer.capture(callBack);
        if (!renderOnRequest) {
            requestRender();
        }
    }

    public void switchRenderMode() {
        if (renderOnRequest) {
            setRenderMode(RENDERMODE_CONTINUOUSLY);
            renderOnRequest = false;
        } else {
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            renderOnRequest = true;
        }
    }

    public void setPCFON(boolean pcfon){
        renderer.setPCFON(pcfon);
    }

    public boolean isRenderOnRequest() {
        return renderOnRequest;
    }


    public String getFps() {
        return renderer.getFps();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        int w = getWidth();
        int h = getHeight();
        float currentX = (2 * x - w) / (float) (w);
        float currentY = (h - 2 * y) / (float) (h);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(currentX - previousX) > 0 || Math.abs(currentY - previousY) > 0) {
                    touchFromVector[0] = previousX;
                    touchFromVector[1] = previousY;
                    touchToVector[0] = currentX;
                    touchToVector[1] = currentY;
                    renderer.rotateArcBall(touchFromVector, touchToVector);
                    requestRender();
                }
                break;

            default:
        }

        previousX = currentX;
        previousY = currentY;
        return true;
    }
}
