package Shrek;

import data.VertexArray;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.skipper.Constants.BYTES_PER_FLOAT;

public class ShrekTable {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT2 = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT2) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //Order of coordinates X, Y, S, T
            //Triangle fan
            -1f,-1f,0f,1f,-4f, 1f,
             1f,-1f,1f,1f, 1f, 5f,
             1f, 1f,1f,0f, 5f, 0f,
            -1f, 1f,0f,0f, 0f,-4f,
            -1f,-1f,0f,1f,-4f, 1f
    };

    private final VertexArray vertexArray;

    public ShrekTable(){ vertexArray = new VertexArray(VERTEX_DATA); }

    public void bindData(TwoTextureShaderProgram textureProgram){
        vertexArray.setVertexAttribPointer(0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(TEXTURE_COORDINATES_COMPONENT_COUNT+TEXTURE_COORDINATES_COMPONENT_COUNT2,
                textureProgram.getTextureCoordinatesAttributeLocation2(),
                TEXTURE_COORDINATES_COMPONENT_COUNT2,
                STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }
}
