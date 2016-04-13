package name.haoxin.wardbrdf.scene;

import android.content.res.AssetManager;
import android.opengl.Matrix;

import name.haoxin.wardbrdf.Constants;
import name.haoxin.wardbrdf.model.BBox;
import name.haoxin.wardbrdf.model.Model;
import name.haoxin.wardbrdf.shader.WardShaderProgram;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by hx on 16/3/31.
 */
public class BrushedTeapotScene {
    // brushed metal shader program
    private WardShaderProgram wardShaderProgram;
    // buffer objects

    public BrushedTeapotScene(Camera camera,AssetManager assets) {

    }

    public void render() {

    }
}
