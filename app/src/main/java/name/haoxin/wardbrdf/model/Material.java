package name.haoxin.wardbrdf.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by hx on 16/3/6.
 */
public class Material {
    public float[] ambient = new float[3];
    public float[] diffuse = new float[3];
    public float[] specular = new float[3];
    public float shiness;
    public int[] textureID = new int[1];
    public boolean hasTexture;

    public Material() {
        hasTexture = false;
    }

    public void loadTexture(InputStream inputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        glGenTextures(1, textureID, 0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        hasTexture = true;
    }

    public void setDefault() {
        for (int i = 0; i < 3; i++) {
            ambient[i] = 0.1f;
            diffuse[i] = 0.6f;
            specular[i] = 0.3f;
        }
        shiness = 20.0f;
    }
}
