package programs;

import android.content.Context;

import com.example.skipper.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class UniformColorShaderProgram extends ShaderProgram{
    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uColorLocation;
    private final int uSizeLocation;

    public UniformColorShaderProgram(Context context){
        super(context, R.raw.uniform_color_vertex_shader, R.raw.uniform_color_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        uColorLocation = glGetUniformLocation(program, "u_Color");
        uSizeLocation = glGetUniformLocation(program, "u_Size");
    }

    public void setUniforms(float[] matrix, float[] color, float pointSize){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, color[0], color[1], color[2], color[3]);
        glUniform1f(uSizeLocation, pointSize);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }
}
