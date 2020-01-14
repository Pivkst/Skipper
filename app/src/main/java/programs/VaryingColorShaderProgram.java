package programs;

import android.content.Context;

import com.example.skipper.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class VaryingColorShaderProgram extends ShaderProgram{
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int uSizeLocation;

    public VaryingColorShaderProgram(Context context){
        super(context, R.raw.varying_color_vertex_shader, R.raw.varying_color_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        aColorLocation = glGetAttribLocation(program, "a_Color");
        uSizeLocation = glGetUniformLocation(program, "u_Size");
    }

    public void setUniforms(float[] matrix, float pointSize){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uSizeLocation, pointSize);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getColorAttributeLocation(){
        return aColorLocation;
    }
}
