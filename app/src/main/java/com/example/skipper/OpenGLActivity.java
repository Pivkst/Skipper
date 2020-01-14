package com.example.skipper;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import Shrek.ShrekRenderer;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.skipper.Constants.AIR_HOCKEY;
import static com.example.skipper.Constants.LIGHT_THEME;

public class OpenGLActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private boolean renderer_set = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        Intent intent = getIntent();
        int type = intent.getIntExtra("TYPE", AIR_HOCKEY);
        int theme = intent.getIntExtra("THEME", LIGHT_THEME);
        final OpenGlRenderer openGlRenderer = new OpenGlRenderer(this, theme);

        switch(type){
            case AIR_HOCKEY: glSurfaceView.setRenderer(openGlRenderer); break;
            default: glSurfaceView.setRenderer(new ShrekRenderer(this)); break;
        }
        renderer_set = true;
        setContentView(glSurfaceView);

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);
                if(event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                openGlRenderer.handleTouchPress(normalizedX, normalizedY);
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                openGlRenderer.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });
                    }
                    return true;
                } else return false;
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(renderer_set){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(renderer_set){
            glSurfaceView.onResume();
        }
    }
}