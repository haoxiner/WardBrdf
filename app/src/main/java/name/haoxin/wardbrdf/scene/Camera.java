package name.haoxin.wardbrdf.scene;

import android.opengl.Matrix;

/**
 * Created by hx on 16/3/22.
 */
public class Camera {
    private final float[] worldToCamera = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private int xResolution, yResolution;

    public Camera() {
        Matrix.setIdentityM(worldToCamera, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
    }

    public void lookAt(float positionX, float positionY, float positionZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        Matrix.setLookAtM(viewMatrix, 0, positionX, positionY, positionZ, centerX, centerY, centerZ, upX, upY, upZ);
    }


    // Projection * View
    public float[] getWorldToCameraMatrix() {
        Matrix.multiplyMM(worldToCamera, 0, projectionMatrix, 0, viewMatrix, 0);
        return worldToCamera;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public void frustum(float left, float right, int bottom, int top, float near, float far) {
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void ortho(float left, float right, int bottom, int top, float near, float far) {
        Matrix.orthoM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void setResolution(int w, int h) {
        xResolution = w;
        yResolution = h;
    }

    public int getxResolution() {
        return xResolution;
    }

    public int getyResolution() {
        return yResolution;
    }
}
