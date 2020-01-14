package com.example.skipper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

public class TextureHelper {
    public static int loadTexture(Context context, int resourceID){
        if(context == null) return 0;
        final int[] textureObjectIDs = new int[1];
        glGenTextures(1, textureObjectIDs, 0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceID, options);
        glBindTexture(GL_TEXTURE_2D, textureObjectIDs[0]); //Bind android texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0); //Send texture data to OpenGL
        bitmap.recycle(); //Free android texture
        glGenerateMipmap(GL_TEXTURE_2D); //Generate all mipmap levels
        glBindTexture(GL_TEXTURE_2D, 0); // 0 means unbind. We do it so we don't accidentally mess it up later
        return textureObjectIDs[0];
    }
}
