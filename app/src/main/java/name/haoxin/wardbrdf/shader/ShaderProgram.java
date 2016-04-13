package name.haoxin.wardbrdf.shader;


import name.haoxin.wardbrdf.model.Material;

import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by hx on 16/3/22.
 */
public class ShaderProgram {

    private int id;
    private int uMVP;
    private int uModelView;
    private int uModel;
    private int uViewPos;

    private int uAmbient;
    private int uSpecular;
    private int uDiffuse;
    private int uShiness;

    private int uLightPos;

    private int uHasTexture;
    private int uTexture;

    private int vPosition, vNormal, vTextureUV;
    private float[] MVPMatrix;


    public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
        id = glCreateProgram();
        glAttachShader(id, vertexShader.getId());
        glAttachShader(id, fragmentShader.getId());
        glLinkProgram(id);
        // transform
        uMVP = glGetUniformLocation(id, "uMVP");
        uModelView = glGetUniformLocation(id, "uModelView");
//        uModel = glGetUniformLocation(id, "uModel");
//        uViewPos = glGetUniformLocation(id, "uViewPos");
        // material
        uAmbient = glGetUniformLocation(id, "uMaterial.ambient");
        uSpecular = glGetUniformLocation(id, "uMaterial.specular");
        uDiffuse = glGetUniformLocation(id, "uMaterial.diffuse");
        uShiness = glGetUniformLocation(id, "uMaterial.shiness");
        // light
        uLightPos = glGetUniformLocation(id, "uLightPos");
        // texture
        uHasTexture = glGetUniformLocation(id, "uHasTexture");
        // attribute
        vPosition = glGetAttribLocation(id, "vPosition");
        vNormal = glGetAttribLocation(id, "vNormal");
        vTextureUV = glGetAttribLocation(id, "vTextureUV");
    }

    public void use() {
        glUseProgram(id);
    }

    public void release() {
        glUseProgram(0);
    }

    public void delete() {
        if (id != 0) {
            glDeleteProgram(id);
        }
    }

    public void setMaterial(Material material) {
        glUniform3fv(uAmbient, 1, material.ambient, 0);
        glUniform3fv(uSpecular, 1, material.specular, 0);
        glUniform3fv(uDiffuse, 1, material.diffuse, 0);
        glUniform1f(uShiness, material.shiness);
        if (material.hasTexture) {
            glUniform1i(uHasTexture, GL_TRUE);
        } else {
            glUniform1i(uHasTexture, GL_FALSE);
        }
    }

    public void setLightPosition(float x, float y, float z) {
        glUniform3f(uLightPos, x, y, z);
    }

    public int getPositionLocation() {
        return vPosition;
    }

    public int getNormalLocation() {
        return vNormal;
    }

    public int getUVLocation() {
        return vTextureUV;
    }

    public void setMVPMatrix(float[] matrix) {
        glUniformMatrix4fv(uMVP, 1, false, matrix, 0);
    }

    public void setModelViewMatrix(float[] matrix) {
        glUniformMatrix4fv(uModelView, 1, false, matrix, 0);
    }

}
