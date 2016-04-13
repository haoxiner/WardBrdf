package name.haoxin.wardbrdf.shader;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_INFO_LOG_LENGTH;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by hx on 16/3/22.
 */
public class Shader {
    private int id;

    public Shader(int type, InputStream inputStream) {
        StringBuilder text = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open shader");
        }
        id = glCreateShader(type);
        glShaderSource(id, text.toString());
        glCompileShader(id);
    }

    public int getId() {
        return id;
    }

    public void delete() {
        if (id != 0) {
            glDeleteShader(id);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        delete();
    }
}
