package name.haoxin.wardbrdf.scene;

import android.opengl.Matrix;

/**
 * Created by hx on 16/3/31.
 */
public class Scene {
    private ArcBall arcBall;
    protected final float[] MVPMatrix = new float[16];
    protected final float[] ModelViewMatrix = new float[16];
    private Camera camera;

    public Scene(Camera camera) {
        this.camera = camera;
    }

    public void update(){
        Matrix.multiplyMM(ModelViewMatrix, 0, camera.getViewMatrix(), 0, arcBall.getModelToWorldMatrix(), 0);
        Matrix.multiplyMM(MVPMatrix, 0, camera.getWorldToCameraMatrix(), 0, arcBall.getModelToWorldMatrix(), 0);
    }

    public void render(){

    }

    public void rotate(float[] touchFromVector, float[] touchToVector) {
        arcBall.rotate(touchFromVector, touchToVector);
    }
}
