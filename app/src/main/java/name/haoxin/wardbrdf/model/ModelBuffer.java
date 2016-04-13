package name.haoxin.wardbrdf.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import name.haoxin.wardbrdf.Constants;

/**
 * Created by hx on 16/3/31.
 */
public class ModelBuffer {
    public BBox bBox;
    public int vertexCount;
    public ByteBuffer vertexByteBuffer,indexByteBuffer;

    public static ModelBuffer load(InputStream inputStream){
        ModelBuffer modelBuffer = new ModelBuffer();
        int vertexBufferSize = 0, indexBufferSize = 0;
        byte vertexBuffer[] = null;
        byte indexBuffer[] = null;
        try {
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
            modelBuffer.bBox = new BBox(bBoxValue[0], bBoxValue[1], bBoxValue[2], bBoxValue[3], bBoxValue[4], bBoxValue[5]);
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
        modelBuffer.vertexCount = indexBufferSize / (Constants.BYTE_PER_INT);
        modelBuffer.vertexByteBuffer = ByteBuffer.allocateDirect(vertexBufferSize).order(ByteOrder.nativeOrder()).put(vertexBuffer);
        modelBuffer.vertexByteBuffer.position(0);
        modelBuffer.indexByteBuffer = ByteBuffer.allocateDirect(indexBufferSize).order(ByteOrder.nativeOrder()).put(indexBuffer);
        modelBuffer.indexByteBuffer.position(0);
        return modelBuffer;
    }
}
