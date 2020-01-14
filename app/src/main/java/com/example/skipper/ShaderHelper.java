package com.example.skipper;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderHelper {

    private static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    private static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectID = glCreateShader(type);
        glShaderSource(shaderObjectID, shaderCode);
        glCompileShader(shaderObjectID);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectID, GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0]==GL_FALSE){
            Log.v("SHDR", "Shader compile failed: \n"+shaderCode+"\nInfo log:"+glGetShaderInfoLog(shaderObjectID));
            glDeleteShader(shaderObjectID);
            return 0;
        }
        return shaderObjectID;
    }

    private static int linkProgram(int vertexShaderID, int fragmentShaderID){
        final int programObjectID = glCreateProgram();
        glAttachShader(programObjectID, vertexShaderID);
        glAttachShader(programObjectID, fragmentShaderID);
        glLinkProgram(programObjectID);
        return programObjectID;
    }

    private static void validateProgram(int programObjectID){
        // Returns true if validation was successful
        glValidateProgram(programObjectID);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectID, GL_VALIDATE_STATUS, validateStatus, 0);
        if(validateStatus[0] != GL_TRUE) throw new RuntimeException("Program failed to validate!");
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource){
        int program;

        //Compile the shaders
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        //Link shaders
        program = linkProgram(vertexShader, fragmentShader);
        validateProgram(program);

        return program;
    }
}
