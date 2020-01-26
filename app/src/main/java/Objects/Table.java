package Objects;

import data.VertexArray;
import programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.skipper.Constants.BYTES_PER_FLOAT;

public class Table {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private float UVTY = 0.53f;
    private float UVBY = 0.63f;
    private float UVLX = 0.35f;
    private float UVRX = 0.58f;
    private float UVMX = (UVLX+UVRX)/2;

    private final float[] VERTEX_DATA;

    private final VertexArray vertexArray;

    public Table(float scale){
        float width = 1;
        float length = 1.6f;
        float leftX = -width/2 * scale;
        float rightX = width/2 * scale;
        float farZ = -length/2 * scale;
        float nearZ = length/2 * scale;
        VERTEX_DATA = new float[]{
                //Order of coordinates X, Y, Z, S, T
                //The vertices should be drawn with GL_TRIANGLE_FAN
                /*
                   0f,0f,    0,UVMX,UVTY+0.02f,
                -0.5f,0f,    0,UVLX,UVTY,
                -0.5f,0f,-0.8f,UVLX,UVBY,
                 0.5f,0f,-0.8f,UVRX,UVBY,
                 0.5f,0f,    0,UVRX,UVTY,
                 0.5f,0f, 0.8f,UVRX,UVBY,
                -0.5f,0f, 0.8f,UVLX,UVBY,
                -0.5f,0f,    0,UVLX,UVTY
    */
                0f,0f, 0f,0.5f,0.5f,
                leftX, 0f, nearZ,0f,1f,
                rightX,0f, nearZ,1f,1f,
                rightX,0f, farZ, 1f,0f,
                leftX, 0f, farZ, 0f,0f,
                leftX, 0f, nearZ,0f,1f

        };
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram){
        vertexArray.setVertexAttribPointer(0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
