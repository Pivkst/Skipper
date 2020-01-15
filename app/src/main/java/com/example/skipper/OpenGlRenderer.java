package com.example.skipper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Objects.Mallet;
import Objects.Puck;
import Objects.Table;
import data.VertexArray;
import programs.CircleShaderProgram;
import programs.TextureShaderProgram;
import programs.UniformColorShaderProgram;
import programs.VaryingColorShaderProgram;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FUNC_ADD;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBlendEquation;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static com.example.skipper.Constants.DARK_THEME;
import static com.example.skipper.Geometry.divideByW;
import static com.example.skipper.Geometry.intersectionPoint;
import static com.example.skipper.Geometry.vectorBetween;

public class OpenGlRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    @SuppressWarnings("unused")
    private int frameCounter = 0;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new  float[16];
    private final float[] viewProjectionMatrix = new  float[16];
    private final float[] modelViewProjectionMatrix = new  float[16];
    private final float[] invertedProjectionMatrix = new  float[16];
    private float width, aspect;
    private int theme;

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;
    private int[] score = {0, 0}; //Blue and red's score in that order

    private final float[] redColor = {1, 0, 0, 1};
    private final float[] blueColor = {0, 0, 1, 1};
    private final float[] grayColor = {0.5f, 0.5f, 0.5f, 1};

    private boolean malletPressed = false;
    private Geometry.Point redMalletPosition;

    private Geometry.Point blueMalletPosition;
    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point blueMalletTargetPosition;

    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;

    private TextureShaderProgram textureShaderProgram;
    private UniformColorShaderProgram uniformColorShaderProgram;
    private VaryingColorShaderProgram varyingColorShaderProgram;
    private CircleShaderProgram circleShaderProgram;

    private int texture;
    private int texture2;

    OpenGlRenderer(Context context, int theme){
        this.context = context;
        this.theme = theme;

        blueMalletPosition = new Geometry.Point(0f, 0f, 0.6f);
        previousBlueMalletPosition = blueMalletPosition;
        redMalletPosition = new Geometry.Point(0f, 0f, -0.6f);
        puckPosition = new Geometry.Point(0f, 0f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
    }

    private void positionObjectInScene(float x, float y, float z){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix,
                0,
                viewProjectionMatrix,
                0,
                modelMatrix,
                0);
    }

    private void positionObjectInScene(Geometry.Point point){
        positionObjectInScene(point.x, point.y, point.z);
    }

    private void setBlueMalletTargetPosition(Geometry.Point point){
        blueMalletTargetPosition = point;
    }

    private Geometry.Point getTouchPosition(float normalizedX, float normalizedY){
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        //Define a plane representing the table
        Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0f, 0f, 0f), new Geometry.Vector(0f, 1f, 0f));
        return intersectionPoint(ray, plane);
    }

    void handleTouchPress(float normalizedX, float normalizedY){
        setBlueMalletTargetPosition(getTouchPosition(normalizedX, normalizedY));
        malletPressed = true;
    }

    void handleTouchDrag(float normalizedX, float normalizedY){
        if(malletPressed){
            setBlueMalletTargetPosition(getTouchPosition(normalizedX, normalizedY));
        }
    }

    void handleTouchRelease(){
        malletPressed = false;
    }

    private void doPhysics(){
        final float malletMaxSpeed = 0.1f;
        final float puckMaxSpeed = 0.2f;
        final float puckReboundSpeedLossFactor = 0.95f;
        final float puckSlideSpeedLossFactor = 0.99f;
        final float leftBound = -0.5f;
        final float rightBound = 0.5f;
        final float goalWidth = 0.4f;

        //Move mallet
        if(malletPressed){
            Geometry.Vector vectorToTouchPosition = vectorBetween(previousBlueMalletPosition, blueMalletTargetPosition);
            blueMalletPosition = (vectorToTouchPosition.length() > malletMaxSpeed) ?
                    previousBlueMalletPosition.translate(vectorToTouchPosition.scale(malletMaxSpeed/vectorToTouchPosition.length())) :
                    blueMalletTargetPosition;
        }
            //Clamp mallet
        blueMalletPosition = new Geometry.Point(
                clamp(blueMalletPosition.x, leftBound+mallet.radius, rightBound-mallet.radius),
                0f,
                clamp(blueMalletPosition.z, mallet.radius, nearBound-mallet.radius)
        );
        Geometry.Vector malletMoveVector = vectorBetween(previousBlueMalletPosition, blueMalletPosition);
        previousBlueMalletPosition = blueMalletPosition;

        //Move puck
        puckPosition = puckPosition.translate(puckVector);
            //Collision with blue mallet
        float distanceBetweenObjects = vectorBetween(blueMalletPosition, puckPosition).length();
        if(distanceBetweenObjects < (puck.radius + mallet.radius)){
            puckPosition = puckPosition.translate(puckVector.scale(-(puck.radius + mallet.radius)-distanceBetweenObjects*2)); //Move puck outside collision
            Geometry.Vector hitVector = vectorBetween(blueMalletPosition, puckPosition);
            puckVector = puckVector
                    .rebound(hitVector) //Reflect the momentum of the puck
                    .add(hitVector.scale(malletMoveVector.length()/hitVector.length())) //Add the momentum of the mallet
                    .scale(puckReboundSpeedLossFactor); //Drop some momentum
        }
            //Collision with red mallet
        distanceBetweenObjects = vectorBetween(redMalletPosition, puckPosition).length();
        if(distanceBetweenObjects < (puck.radius + mallet.radius)){
            puckPosition = puckPosition.translate(puckVector.scale(-(puck.radius + mallet.radius)-distanceBetweenObjects*2)); //Move puck outside collision
            Geometry.Vector hitVector = vectorBetween(redMalletPosition, puckPosition);
            puckVector = puckVector
                    .rebound(hitVector) //Reflect the momentum of the puck
                    .add(hitVector.scale(malletMoveVector.length()/hitVector.length())) //Add the momentum of the mallet
                    .scale(puckReboundSpeedLossFactor+0.3f); //Drop some momentum
        }
            //Collision with wall or goal
        if(puckPosition.x < leftBound+puck.radius || puckPosition.x > rightBound-puck.radius){
            puckVector = puckVector.scale(puckReboundSpeedLossFactor);
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
        }if(puckPosition.z < farBound+puck.radius){
            if(puckPosition.x>(-goalWidth/2) && puckPosition.x<(goalWidth/2)) goal(true);
            else{
                puckVector = puckVector.scale(puckReboundSpeedLossFactor);
                puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            }
        }if(puckPosition.z > nearBound-puck.radius){
            if(puckPosition.x>(-goalWidth/2) && puckPosition.x<(goalWidth/2)) goal(false);
            else{
                puckVector = puckVector.scale(puckReboundSpeedLossFactor);
                puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            }
        }
            //Clamp puck
        puckPosition=new Geometry.Point(
                clamp(puckPosition.x,leftBound+puck.radius,rightBound-puck.radius),
                puckPosition.y,
                clamp(puckPosition.z,farBound+puck.radius,nearBound-puck.radius)
        );
            //Slow down puck due to drag
        puckVector = puckVector.scale(puckSlideSpeedLossFactor);
            //Clamp puck speed
        if(puckVector.length()>puckMaxSpeed)
            puckVector = puckVector.scale(puckMaxSpeed/puckVector.length());
    }

    private void goal(boolean scoreGoesToBlue){
        if(scoreGoesToBlue) score[0]++;
        else score[1]++;

        blueMalletPosition = new Geometry.Point(0f, 0f, 0.6f);
        previousBlueMalletPosition = blueMalletPosition;
        redMalletPosition = new Geometry.Point(0f, 0f, -0.6f);
        puckPosition = new Geometry.Point(0f, 0f, (scoreGoesToBlue) ? -0.2f : 0.2f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
    }

    private float clamp(float value, float min, float max){
        return Math.min(max, Math.max(value, min));
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
        //Converts the point into a ray spanning from the near plane to the far plan by first
        // multiplying with the inverse of the perspective matrix and then undoing the perspective
        // divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        //Undo projection matrix
        multiplyMV(nearPointWorld, 0, invertedProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedProjectionMatrix, 0, farPointNdc, 0);
        //Undo perspective divide
        divideByW(nearPointWorld);
        divideByW(farPointWorld);
        //Create ray
        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Geometry.Ray(nearPointRay, vectorBetween(nearPointRay, farPointRay));
    }

    private void draw3DPoint(Geometry.Point point, float radius, float[] color){
        float apery = 1.2020569f;
        float[] vertexData = {point.x, point.y, point.z};
        VertexArray vertexArray = new VertexArray(vertexData);
        circleShaderProgram.useProgram();
        circleShaderProgram.setUniforms(viewProjectionMatrix, color, radius*apery*2f*width/aspect);
        vertexArray.setVertexAttribPointer(0, circleShaderProgram.getPositionAttributeLocation(), 3, 0);
        glDrawArrays(GL_POINTS, 0, 1);
    }

    private void displayScore(){
        draw3DPoint(new Geometry.Point(0.168f - (0.168f*2/9) * score[0], 0.02f, nearBound), 0.02f, redColor);
        draw3DPoint(new Geometry.Point(-0.168f + (0.168f*2/9) * score[1], 0.02f, farBound), 0.02f, redColor);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(theme == DARK_THEME) glClearColor(0f, 0f, 0f, 0f);
        else glClearColor(0.4f, 0.6f, 0.4f, 0.0f);
        table = new Table(1.35f);
        mallet = new Mallet( 0.08f, 0.1f, 32);
        puck = new Puck(0.08f, 0.03f, 32);

        textureShaderProgram = new TextureShaderProgram(context);
        uniformColorShaderProgram = new UniformColorShaderProgram(context);
        varyingColorShaderProgram = new VaryingColorShaderProgram(context);
        circleShaderProgram = new CircleShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface2);
        texture2 = TextureHelper.loadTexture(context, R.drawable.adonkeh);

        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        glViewport(0, 0, width, height);
        float aspect = (float)width/(float)height;
        this.aspect = aspect;
        perspectiveM(projectionMatrix, 0, 45, aspect, 0.9f, 10f);
        Log.v("SCREEN", "width: "+width+" height: "+height+" aspect: "+aspect);
        setLookAtM(viewMatrix,
                0,
                0f,
                1.2f,
                1.5f,
                0f,
                0f,
                0f,
                0f,
                1f,
                0f);
        //System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
        //glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLineWidth(5f);
        frameCounter++;
        //Game logic
        doPhysics();

        //View updates
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedProjectionMatrix, 0, viewProjectionMatrix, 0);

        //Textured objects
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //arguments in order are: source factor, destination factor
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(viewProjectionMatrix, texture, texture2);
        textureShaderProgram.setActiveTextureUnit(0);
        table.bindData(textureShaderProgram);
        table.draw();
        glDisable(GL_BLEND);
        //Solid objects
        varyingColorShaderProgram.useProgram();
        varyingColorShaderProgram.setUniforms(modelViewProjectionMatrix, 10f);
        //Puck
        positionObjectInScene(puckPosition);
        uniformColorShaderProgram.useProgram();
        uniformColorShaderProgram.setUniforms(modelViewProjectionMatrix, grayColor,10f);
        puck.bindData(uniformColorShaderProgram);
        puck.draw();

        //Red mallet (near)
        positionObjectInScene(redMalletPosition);
        uniformColorShaderProgram.useProgram();
        uniformColorShaderProgram.setUniforms(modelViewProjectionMatrix, redColor,10f);
        mallet.bindData(uniformColorShaderProgram);
        mallet.draw();
        draw3DPoint(redMalletPosition.translateY(mallet.height), mallet.radius/2,redColor);
        //drawTableBorder(color);

        //Blue mallet (far)
        positionObjectInScene(blueMalletPosition);
        uniformColorShaderProgram.useProgram();
        uniformColorShaderProgram.setUniforms(modelViewProjectionMatrix, blueColor,10f);
        mallet.bindData(uniformColorShaderProgram);
        mallet.draw();
        draw3DPoint(blueMalletPosition.translateY(mallet.height), mallet.radius/2, blueColor);

        displayScore();
        //wait();
    }

    @SuppressWarnings("unused")
    private void drawTableBorder(float[] color){
        float[] vertexData = {
                -0.5f,0f, 0.8f,
                -0.5f,0.05f, 0.8f,
                0.5f,0f, 0.8f,
                0.5f,0.05f, 0.8f,
                0.5f,0f,-0.8f,
                0.5f,0.05f,-0.8f,
                -0.5f,0f,-0.8f,
                -0.5f,0.05f,-0.8f,
                -0.5f,0f, 0.8f,
                -0.5f,0.05f, 0.8f
        };
        VertexArray vertexArray = new VertexArray(vertexData);
        uniformColorShaderProgram.useProgram();
        uniformColorShaderProgram.setUniforms(viewProjectionMatrix, color, 1f);
        vertexArray.setVertexAttribPointer(0, uniformColorShaderProgram.getPositionAttributeLocation(), 3, 0);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 10);
    }

    @SuppressWarnings("unused")
    private void wait(int n){
        try { Thread.sleep(n); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
