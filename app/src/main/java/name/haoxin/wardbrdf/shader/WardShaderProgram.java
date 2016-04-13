package name.haoxin.wardbrdf.shader;

import android.util.Log;

import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by hx on 16/3/29.
 */
public class WardShaderProgram {
    private int id;
    private int uMVP;
    private int uView;
    private int uModelView;
    private int uLightDir;
    private int vPosition, vNormal, vTexUV;
    private int vTangent;
    private int uTex, uNormalMap;
    private int uDepthBiasMVPMatrix;
    private int uShadowMap;

    private int uxPixelOffset, uyPixelOffset;
    private int uPCFON;

    public WardShaderProgram(Shader vs, Shader fs) {
        id = glCreateProgram();
        glAttachShader(id, vs.getId());
        glAttachShader(id, fs.getId());
        glLinkProgram(id);

        uMVP = glGetUniformLocation(id, "uMVP");
        uView = glGetUniformLocation(id, "uView");
        uModelView = glGetUniformLocation(id, "uModelView");
        uLightDir = glGetUniformLocation(id, "uLightDir");
        vPosition = glGetAttribLocation(id, "vPosition");
        vNormal = glGetAttribLocation(id, "vNormal");
        vTexUV = glGetAttribLocation(id, "vTexUV");

        vTangent = glGetAttribLocation(id, "vTangent");

        uTex = glGetUniformLocation(id, "uTex");
        uNormalMap = glGetUniformLocation(id, "uNormalMap");
        uDepthBiasMVPMatrix = glGetUniformLocation(id, "uDepthBiasMVP");
        uShadowMap = glGetUniformLocation(id, "uShadowMap");
        uxPixelOffset = glGetUniformLocation(id, "uxPixelOffset");
        uyPixelOffset = glGetUniformLocation(id, "uyPixelOffset");
        uPCFON = glGetUniformLocation(id, "uPCFON");
    }

    public void use() {
        glUseProgram(id);
    }

    public void release() {
        glUseProgram(0);
    }

    public void setMVPMatrix(float[] matrix) {
        glUniformMatrix4fv(uMVP, 1, false, matrix, 0);
    }

    public void setViewMatrix(float[] matrix) {
        glUniformMatrix4fv(uView, 1, false, matrix, 0);
    }

    public void setModelViewMatrix(float[] matrix) {
        glUniformMatrix4fv(uModelView, 1, false, matrix, 0);
    }

    public void setLightDir(float x, float y, float z) {
        glUniform3f(uLightDir, x, y, z);
    }

    public int getPositionLocation() {
        return vPosition;
    }

    public int getNormalLocation() {
        return vNormal;
    }

    public int getUVLocation() {
        return vTexUV;
    }

    public int getTangentLocation() {
        return vTangent;
    }

    public int getTextureLocation() {
        return uTex;
    }

    public int getNormalMapLocation() {
        return uNormalMap;
    }

    public void setDepthBiasMVPMatrix(float[] matrix) {
        glUniformMatrix4fv(uDepthBiasMVPMatrix, 1, false, matrix, 0);
    }

    public int getShadowMapLocation() {
        return uShadowMap;
    }

    public void setxPixelOffset(float f) {
        glUniform1f(uxPixelOffset, f);
    }

    public void setyPixelOffset(float f) {
        glUniform1f(uyPixelOffset, f);
    }

    public void setPCFON(boolean pcfon) {
        if (pcfon) {
            glUniform1i(uPCFON, GL_TRUE);
        } else {
            glUniform1i(uPCFON, GL_FALSE);
        }
    }
}
