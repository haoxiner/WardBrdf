package name.haoxin.wardbrdf.scene;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import name.haoxin.wardbrdf.Constants;
import name.haoxin.wardbrdf.model.BBox;
import name.haoxin.wardbrdf.shader.Shader;
import name.haoxin.wardbrdf.shader.ShadowMapProgram;
import name.haoxin.wardbrdf.shader.WardShaderProgram;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_EXTENSIONS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetString;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by hx on 16/3/27.
 */
public class SceneTeapot {
    private WardShaderProgram wardShaderProgram;
    private ShadowMapProgram shadowMapProgram;
    private ArcBall arcBall;
    private float distance = 10f;
    private Camera camera;
    private Camera shadowMapCamera;
    private BBox bBox;
    private int[] bufferObjects = new int[2];
    private int STRIDE = (Constants.FLOAT_PER_POSITION + Constants.FLOAT_PER_NORMAL + Constants.FLOAT_PER_UV + Constants.FLOAT_PER_TANGENT) * Constants.BYTE_PER_FLOAT;
    private int vertexCount;
    private float[] MVPMatrix = new float[16];
    private float[] ModelViewMatrix = new float[16];
    private int[] textureID = new int[3];
    private int[] frameBuffer = new int[1];
    private int[] renderBuffer = new int[1];

    private float[] biasMatrix = {
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f};
    private float[] depthMVP = new float[16];
    private float[] depthBiasMVP = new float[16];
    private boolean pcfON = true;

    public SceneTeapot() {
        camera = new Camera();
        shadowMapCamera = new Camera();
        float ratio = camera.getxResolution() / (float) camera.getyResolution();
        shadowMapCamera.ortho(-15, 15, -15, 15, 1.0f, 50.0f);
        shadowMapCamera.lookAt(0, 40, 20, 0, 0, 0, 0f, 1.0f, 0.0f);
    }

    public void render() {
        glCullFace(GL_FRONT);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shadowMapProgram.use();
        Matrix.multiplyMM(depthMVP, 0, shadowMapCamera.getWorldToCameraMatrix(), 0, arcBall.getModelToWorldMatrix(), 0);
        shadowMapProgram.setDepthMVPMatrix(depthMVP);
        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glEnableVertexAttribArray(shadowMapProgram.getPositionLocation());
        glVertexAttribPointer(shadowMapProgram.getPositionLocation(), Constants.FLOAT_PER_POSITION, GL_FLOAT, false, STRIDE, Constants.POSITION_OFFSET);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[1]);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(shadowMapProgram.getPositionLocation());
        shadowMapProgram.release();

        glCullFace(GL_BACK);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        wardShaderProgram.use();
        Matrix.multiplyMM(ModelViewMatrix, 0, camera.getViewMatrix(), 0, arcBall.getModelToWorldMatrix(), 0);
        wardShaderProgram.setModelViewMatrix(ModelViewMatrix);
        Matrix.multiplyMM(MVPMatrix, 0, camera.getWorldToCameraMatrix(), 0, arcBall.getModelToWorldMatrix(), 0);
        wardShaderProgram.setMVPMatrix(MVPMatrix);
        wardShaderProgram.setViewMatrix(camera.getViewMatrix());
        Matrix.multiplyMM(depthBiasMVP, 0, biasMatrix, 0, depthMVP, 0);
        wardShaderProgram.setDepthBiasMVPMatrix(depthBiasMVP);
        wardShaderProgram.setPCFON(pcfON);

        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glEnableVertexAttribArray(wardShaderProgram.getPositionLocation());
        glEnableVertexAttribArray(wardShaderProgram.getNormalLocation());
        glEnableVertexAttribArray(wardShaderProgram.getUVLocation());
        glEnableVertexAttribArray(wardShaderProgram.getTangentLocation());

        glVertexAttribPointer(wardShaderProgram.getPositionLocation(), Constants.FLOAT_PER_POSITION, GL_FLOAT, false, STRIDE, Constants.POSITION_OFFSET);
        glVertexAttribPointer(wardShaderProgram.getNormalLocation(), Constants.FLOAT_PER_NORMAL, GL_FLOAT, false, STRIDE, Constants.NORMAL_OFFSET);
        glVertexAttribPointer(wardShaderProgram.getUVLocation(), Constants.FLOAT_PER_UV, GL_FLOAT, false, STRIDE, Constants.UV_OFFSET);
        glVertexAttribPointer(wardShaderProgram.getTangentLocation(), Constants.FLOAT_PER_TANGENT, GL_FLOAT, false, STRIDE, Constants.TANGENT_OFFSET);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);
        glUniform1i(wardShaderProgram.getTextureLocation(), 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, textureID[1]);
        glUniform1i(wardShaderProgram.getNormalMapLocation(), 1);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, textureID[2]);
        glUniform1i(wardShaderProgram.getShadowMapLocation(), 2);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[1]);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(wardShaderProgram.getTangentLocation());
        glDisableVertexAttribArray(wardShaderProgram.getUVLocation());
        glDisableVertexAttribArray(wardShaderProgram.getNormalLocation());
        glDisableVertexAttribArray(wardShaderProgram.getPositionLocation());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        wardShaderProgram.release();

    }


    public void rotate(float[] touchFromVector, float[] touchToVector) {
        arcBall.rotate(touchFromVector, touchToVector);
    }

    public void onSizeChanged(int width, int height) {
        float ratio = (float) width / height;
        camera.frustum(-ratio, ratio, -1, 1, 1.0f, 50.0f);
        camera.setResolution(width, height);
        wardShaderProgram.use();
        wardShaderProgram.setxPixelOffset(1.0f / width);
        wardShaderProgram.setyPixelOffset(1.0f / height);
        wardShaderProgram.release();

        glBindTexture(GL_TEXTURE_2D, textureID[2]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);


        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureID[2], 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Test OES_depth_texture extension
        String extensions = glGetString(GL_EXTENSIONS);

        if (extensions.contains("OES_depth_texture")) {
            Log.e("OES_depth_texture", "true");
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureID[2], 0);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID[2], 0);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer[0]);
        }
    }

    public void load(AssetManager assets) {
        // load mesh
        int vertexBufferSize = 0, indexBufferSize = 0;
        byte vertexBuffer[] = null;
        byte indexBuffer[] = null;
        try {
            InputStream inputStream = assets.open("scene.pnuvti");
            int read = 0;

            byte intByte[] = new byte[4 * 2];
            read = 0;
            while (read < intByte.length) {
                read = inputStream.read(intByte, read, intByte.length - read);
            }
            IntBuffer intBuffer = ByteBuffer.wrap(intByte).order(ByteOrder.nativeOrder()).asIntBuffer();
            intBuffer.position(0);
            vertexBufferSize = intBuffer.get();
            indexBufferSize = intBuffer.get();

            byte floatByte[] = new byte[4 * 6];
            read = 0;
            while (read < floatByte.length) {
                read = inputStream.read(floatByte, read, floatByte.length - read);
            }
            FloatBuffer floatBuffer = ByteBuffer.wrap(floatByte).order(ByteOrder.nativeOrder()).asFloatBuffer();
            floatBuffer.position(0);
            float bBoxValue[] = new float[6];
            floatBuffer.get(bBoxValue);
            bBox = new BBox(bBoxValue[0], bBoxValue[1], bBoxValue[2], bBoxValue[3], bBoxValue[4], bBoxValue[5]);

            vertexBuffer = new byte[vertexBufferSize];
            read = 0;
            while (read < vertexBuffer.length) {
                read = inputStream.read(vertexBuffer, read, vertexBuffer.length - read);
            }

            indexBuffer = new byte[indexBufferSize];
            read = 0;
            while (read < indexBuffer.length) {
                read = inputStream.read(indexBuffer, read, indexBuffer.length - read);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.lookAt(bBox.centerX, bBox.centerY, bBox.centerZ + distance, bBox.centerX, bBox.centerY, bBox.centerZ, 0f, 1.0f, 0.0f);
        // allocate native memory
        vertexCount = indexBufferSize / (Constants.BYTE_PER_INT);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexBufferSize).order(ByteOrder.nativeOrder()).put(vertexBuffer);
        vertexByteBuffer.position(0);
        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(indexBufferSize).order(ByteOrder.nativeOrder()).put(indexBuffer);
        indexByteBuffer.position(0);
        // submit to gpu
        glGenBuffers(2, bufferObjects, 0);
        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexBufferSize, vertexByteBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferObjects[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferSize, indexByteBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        // load shader
        Shader vShader = null;
        try {
            vShader = new Shader(GL_VERTEX_SHADER, assets.open("ward.vert"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Shader fShader = null;
        try {
            fShader = new Shader(GL_FRAGMENT_SHADER, assets.open("ward.frag"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        wardShaderProgram = new WardShaderProgram(vShader, fShader);
        wardShaderProgram.use();
        wardShaderProgram.setLightDir(0, 2, 1);
        wardShaderProgram.release();
        // texture/normal map
        glGenTextures(3, textureID, 0);
        try {
            loadTexture(assets.open("4486-diffuse.jpg"), textureID[0]);
            loadTexture(assets.open("4486-normal.jpg"), textureID[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // arcball
        arcBall = new ArcBall(bBox.centerX, bBox.centerY, bBox.centerZ);
        // rotate to a prepared view
        float[] v1 = {0.0f, 0.0f, 0.0f, 0.0f};
        float[] v2 = {0.0f, -0.5f, 0.0f, 0.0f};
        arcBall.rotate(v1, v2);

        try {
            vShader = new Shader(GL_VERTEX_SHADER, assets.open("shadowmap.vert"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fShader = new Shader(GL_FRAGMENT_SHADER, assets.open("shadowmap.frag"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        shadowMapProgram = new ShadowMapProgram(vShader, fShader);
        glGenFramebuffers(1, frameBuffer, 0);
        glGenRenderbuffers(1, renderBuffer, 0);
    }

    public void loadTexture(InputStream inputStream, int textureID) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPcfON(boolean pcfON) {
        this.pcfON = pcfON;
    }
}
