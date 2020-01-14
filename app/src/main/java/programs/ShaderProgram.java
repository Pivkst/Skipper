package programs;

import android.content.Context;

import com.example.skipper.ShaderHelper;
import com.example.skipper.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public class ShaderProgram {
    protected final int program;
    protected ShaderProgram(Context context, int vertexShaderResourceID, int fragmentShaderResourceID){
        program = ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShaderResourceID),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceID));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
