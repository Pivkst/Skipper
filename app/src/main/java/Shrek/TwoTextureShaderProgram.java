package Shrek;

import android.content.Context;

import com.example.skipper.R;

import programs.ShaderProgram;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class TwoTextureShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uTextureUnit2Location;
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private final int aTextureCoordinates2Location;
    private final int uTextureOffsetLocation;

    public TwoTextureShaderProgram(Context context){
        super(context, R.raw.two_texture_vertex_shader, R.raw.two_texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit");
        uTextureUnit2Location = glGetUniformLocation(program, "u_TextureUnit2");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TextureCoordinates");
        aTextureCoordinates2Location = glGetAttribLocation(program, "a_TextureCoordinates2");
        uTextureOffsetLocation = glGetUniformLocation(program, "u_TextureOffset");
    }

    public void setUniforms(float[] matrix, int textureID, int textureID2, float offsetX, float offsetY){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0); //Set the active texture unit to unit 0
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind the texture to this unit
        glUniform1i(uTextureUnitLocation, 0); //Tell the texture uniform sampler to use this texture in the shader by telling it to read from texture unit 0
        glActiveTexture(GL_TEXTURE1); //Set the active texture unit to unit 1
        glBindTexture(GL_TEXTURE_2D, textureID2); //Bind the texture to this unit
        glUniform1i(uTextureUnit2Location, 1); //Tell the texture uniform sampler to use this texture in the shader by telling it to read from texture unit 0
        glUniform2f(uTextureOffsetLocation, offsetX, offsetY);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation(){
        return aTextureCoordinatesLocation;
    }

    public int getTextureCoordinatesAttributeLocation2(){
        return aTextureCoordinates2Location;
    }
}
