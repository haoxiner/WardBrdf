package name.haoxin.wardbrdf;

/**
 * Created by hx on 16/3/27.
 */
public class Constants {
    public static final int BYTE_PER_FLOAT = 4, BYTE_PER_INT = 4;
    public static final int FLOAT_PER_POSITION = 3, FLOAT_PER_NORMAL = 3, FLOAT_PER_UV = 2;
    public static final int FLOAT_PER_TANGENT = 3;

    public static final int POSITION_OFFSET = 0;
    public static final int NORMAL_OFFSET = POSITION_OFFSET + Constants.FLOAT_PER_POSITION * Constants.BYTE_PER_FLOAT;
    public static final int UV_OFFSET = NORMAL_OFFSET + Constants.FLOAT_PER_NORMAL * Constants.BYTE_PER_FLOAT;
    public static final int TANGENT_OFFSET = UV_OFFSET + Constants.FLOAT_PER_TANGENT * Constants.BYTE_PER_FLOAT;
}
