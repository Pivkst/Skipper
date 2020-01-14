package Shrek;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.skipper.R;
import com.example.skipper.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import programs.VaryingColorShaderProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class ShrekRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    private int frameCounter;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private ShrekTable shrekTable;

    private TwoTextureShaderProgram twoTextureShaderProgram;
    private VaryingColorShaderProgram varyingColorShaderProgram;

    private int shrekTexture;
    private int donkehTexture;

    public ShrekRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.4f, 0.6f, 0.4f, 0.0f);
        frameCounter = 0;
        shrekTable = new ShrekTable();

        twoTextureShaderProgram = new TwoTextureShaderProgram(context);
        varyingColorShaderProgram = new VaryingColorShaderProgram(context);

        shrekTexture = TextureHelper.loadTexture(context, R.drawable.squareshrek);
        donkehTexture = TextureHelper.loadTexture(context, R.drawable.wdonkeh);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        /* //Old projection matrix
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if(width>height) {
            //Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        }else{
            //Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0); */

        perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glLineWidth(5f);
        //Matrix
        frameCounter++;
        final float[] finalMatrix = new float[16];
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0, 0, -2f);
        rotateM(modelMatrix, 0, (float) frameCounter, 0f, 1f, 0f);
        multiplyMM(finalMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        //Textured objects
        twoTextureShaderProgram.useProgram();
        twoTextureShaderProgram.setUniforms(finalMatrix, shrekTexture, donkehTexture, (float)(frameCounter*0.01), 0);
        shrekTable.bindData(twoTextureShaderProgram);
        shrekTable.draw();
        //Solid objects
        varyingColorShaderProgram.useProgram();
        varyingColorShaderProgram.setUniforms(finalMatrix, 10f);
    }
}
