package name.haoxin.wardbrdf.shader;

import android.util.Log;

import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by hx on 16/3/31.
 */
public class ShadowMapProgram {
    private int id;
    private int vPosition;
    private int uDepthMVP;

    public ShadowMapProgram(Shader vs, Shader fs) {
        id = glCreateProgram();
        glAttachShader(id, vs.getId());
        glAttachShader(id, fs.getId());
        glLinkProgram(id);
        vPosition = glGetAttribLocation(id, "vPosition");
        uDepthMVP = glGetUniformLocation(id, "uDepthMVP");
    }

    public void use() {
        glUseProgram(id);
    }

    public void release() {
        glUseProgram(0);
    }

    public void setDepthMVPMatrix(float[] matrix) {
        glUniformMatrix4fv(uDepthMVP, 1, false, matrix, 0);
    }

    public int getPositionLocation() {
        return vPosition;
    }

    public int getDepthMVPLocation() {
        return uDepthMVP;
    }
}
