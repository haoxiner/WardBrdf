package name.haoxin.wardbrdf.scene;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by hx on 16/3/22.
 */
public class ArcBall {

    private final float[] arcballMatrix = new float[16];
    private final float[] modelToWorld = new float[16];
    private final float[] translateBack = new float[16];
    private final float[] from = new float[4];
    private final float[] to = new float[4];
    private final float[] inverseModelMat = new float[16];
    private final float centerX, centerY, centerZ;

    public ArcBall(float centerX, float centerY, float centerZ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        Matrix.setIdentityM(modelToWorld, 0);
        Matrix.setIdentityM(arcballMatrix, 0);
    }

    public float[] getModelToWorldMatrix() {
        return modelToWorld;
    }

    public void load(float[] matrix) {
        System.arraycopy(matrix, 0, arcballMatrix, 0, arcballMatrix.length);
        Matrix.translateM(arcballMatrix, 0, -centerX, -centerY, -centerZ);
        Matrix.setIdentityM(translateBack, 0);
        Matrix.translateM(translateBack, 0, centerX, centerY, centerZ);
        Matrix.multiplyMM(modelToWorld, 0, translateBack, 0, arcballMatrix, 0);
        Matrix.translateM(arcballMatrix, 0, centerX, centerY, centerZ);
    }

    public void rotate(float[] v0, float v1[]) {
        float squareV0 = v0[0] * v0[0] + v0[1] * v0[1];
        float squareV1 = v1[0] * v1[0] + v1[1] * v1[1];
        if (squareV0 < 0.999f) {
            v0[2] = (float) Math.sqrt(1 - squareV0);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV0));

            v0[0] *= invLength;
            v0[1] *= invLength;
            v0[2] = 0;
        }
        if (squareV1 < 0.999f) {
            v1[2] = (float) Math.sqrt(1 - squareV1);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV1));
            v1[0] *= invLength;
            v1[1] *= invLength;
            v1[2] = 0;
        }

        float angle = (float) (Math.acos(Math.min(v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2], 0.999f)) / Math.PI * 180);

        Matrix.invertM(inverseModelMat, 0, arcballMatrix, 0);
        Matrix.multiplyMV(from, 0, inverseModelMat, 0, v0, 0);
        Matrix.multiplyMV(to, 0, inverseModelMat, 0, v1, 0);

        float crossX = from[1] * to[2] - from[2] * to[1];
        float crossY = from[2] * to[0] - from[0] * to[2];
        float crossZ = from[0] * to[1] - from[1] * to[0];
        Matrix.rotateM(arcballMatrix, 0, angle, crossX, crossY, crossZ);
        Matrix.translateM(arcballMatrix, 0, -centerX, -centerY, -centerZ);
        Matrix.setIdentityM(translateBack, 0);
        Matrix.translateM(translateBack, 0, centerX, centerY, centerZ);
        Matrix.multiplyMM(modelToWorld, 0, translateBack, 0, arcballMatrix, 0);

        Matrix.translateM(arcballMatrix, 0, centerX, centerY, centerZ);
    }
}
