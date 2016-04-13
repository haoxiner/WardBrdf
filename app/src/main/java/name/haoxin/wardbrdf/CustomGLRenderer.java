package name.haoxin.wardbrdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import name.haoxin.wardbrdf.action.CaptureCallBack;
import name.haoxin.wardbrdf.action.FpsListener;
import name.haoxin.wardbrdf.scene.Camera;
import name.haoxin.wardbrdf.scene.SceneTeapot;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glViewport;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {
    // fps
    private FpsListener fpsListener = new FpsListener();
    // capture
    private boolean takeScreenShot = false;
    private CaptureCallBack captureCallBack;
    // sceneTeapot render
    private SceneTeapot sceneTeapot;
    private Context context;
    private int width,height;

    public CustomGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // opengl settings
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        sceneTeapot = new SceneTeapot();
        sceneTeapot.load(context.getAssets());
        sceneTeapot.setPcfON(true);
        fpsListener.reset();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        sceneTeapot.onSizeChanged(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        sceneTeapot.render();
        fpsListener.tick();
        if (takeScreenShot) {
            captureCallBack.saveCapture(createBitmapFromGLSurface(gl));
            takeScreenShot = false;
        }

    }

    public void capture(CaptureCallBack captureCallBack) {
        this.captureCallBack = captureCallBack;
        takeScreenShot = true;
    }

    private Bitmap createBitmapFromGLSurface(GL10 gl)
            throws OutOfMemoryError {
        int screenPixels = width * height;
        int bitmapBuffer[] = new int[screenPixels];
        int bitmapSource[] = new int[screenPixels];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < height; i++) {
                offset1 = i * width;
                offset2 = (height - i - 1) * width;
                for (int j = 0; j < width; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, width, height, Bitmap.Config.ARGB_8888);
    }

    public void rotateArcBall(float[] touchFromVector, float[] touchToVector) {
        sceneTeapot.rotate(touchFromVector, touchToVector);
    }

    public String getFps() {
        return fpsListener.getFPS();
    }

    public void setPCFON(boolean pcfON){
        sceneTeapot.setPcfON(pcfON);
    }
}
