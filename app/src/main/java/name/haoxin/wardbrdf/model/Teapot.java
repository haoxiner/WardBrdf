package name.haoxin.wardbrdf.model;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import name.haoxin.wardbrdf.Constants;
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
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by hx on 16/3/31.
 */
public class Teapot {

    private int STRIDE = (Constants.FLOAT_PER_POSITION + Constants.FLOAT_PER_NORMAL + Constants.FLOAT_PER_UV + Constants.FLOAT_PER_TANGENT) * Constants.BYTE_PER_FLOAT;
    private int vertexCount;

    private int bufferObjects[];
    private int textureID[];

    public Teapot(AssetManager assets) {
        bufferObjects = new int[4];
        glGenBuffers(4, bufferObjects, 0);
        textureID = new int[2];
        load(assets);
    }

    public void render(WardShaderProgram shaderProgram) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glEnableVertexAttribArray(shaderProgram.getPositionLocation());
        glVertexAttribPointer(shaderProgram.getPositionLocation(), Constants.FLOAT_PER_POSITION, GL_FLOAT, false, STRIDE, Constants.POSITION_OFFSET);
        glEnableVertexAttribArray(shaderProgram.getNormalLocation());
        glVertexAttribPointer(shaderProgram.getNormalLocation(), Constants.FLOAT_PER_NORMAL, GL_FLOAT, false, STRIDE, Constants.NORMAL_OFFSET);
        glEnableVertexAttribArray(shaderProgram.getUVLocation());
        glVertexAttribPointer(shaderProgram.getUVLocation(), Constants.FLOAT_PER_UV, GL_FLOAT, false, STRIDE, Constants.UV_OFFSET);
        glEnableVertexAttribArray(shaderProgram.getTangentLocation());
        glVertexAttribPointer(shaderProgram.getTangentLocation(), Constants.FLOAT_PER_TANGENT, GL_FLOAT, false, STRIDE, Constants.TANGENT_OFFSET);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glUniform1i(shaderProgram.getTextureLocation(), 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, textureID[1]);
        glUniform1i(shaderProgram.getNormalMapLocation(),1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[1]);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(shaderProgram.getTangentLocation());
        glDisableVertexAttribArray(shaderProgram.getUVLocation());
        glDisableVertexAttribArray(shaderProgram.getNormalLocation());
        glDisableVertexAttribArray(shaderProgram.getPositionLocation());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void load(AssetManager assets){
        InputStream inputStream = null;
        try {
            inputStream = assets.open("lid.pnuvti");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModelBuffer lid = ModelBuffer.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = assets.open("body.pnuvti");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModelBuffer body = ModelBuffer.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
