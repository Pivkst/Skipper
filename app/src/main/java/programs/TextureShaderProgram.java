package programs;

import android.content.Context;

import com.example.skipper.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class TextureShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context){
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TextureCoordinates");
    }

    public void setUniforms(float[] matrix, int textureID, int textureID2){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0); //Set the active texture unit to unit 0
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind the texture to this unit
        glActiveTexture(GL_TEXTURE1); //Set the active texture unit to unit 0
        glBindTexture(GL_TEXTURE_2D, textureID2); //Bind the texture to this unit
    }

    public void setActiveTextureUnit(int textureUnitID){
        glUniform1i(uTextureUnitLocation, textureUnitID); //Tell the texture uniform sampler to use this texture in the shader by telling it to read from texture unit 0
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation(){
        return aTextureCoordinatesLocation;
    }

}
