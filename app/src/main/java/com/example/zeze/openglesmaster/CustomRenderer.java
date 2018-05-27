package com.example.zeze.openglesmaster;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by liuyue on 27/05/2018.
 */

public class CustomRenderer implements GLSurfaceView.Renderer {
    private static String VERTEX_SHADER =
            "uniform mat4 u_matrix;" +
            "attribute vec4 a_position;" +
            "attribute vec4 a_color;" +
            "varying vec4 v_color;" +
            "void main() {" +
                    "v_color = a_color;" +
                    "gl_Position = u_matrix * a_position;" +
                    "gl_PointSize = 10.0;" +
            "}";
    private static String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec4 v_color;" +

                    "void main() {" +
                    "   gl_FragColor = v_color;" +
                    "}" +
            "";

    private static final int BYTES_PRE_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PRE_FLOAT;

    private int mProgramId;
    private int mPositionLocation;
    private int mColorLocation;
    private int mProjectMatrixLocation;

    private float[] mProjectMatrixArray = new float[16];
    private FloatBuffer mVertexData;
    private FloatBuffer mProjectMatrixBuffer;

    private static float[] VERTEX_ARRAY = new float[] {
            0f, 0f, 1f, 1f, 1f,
            0.5f, 0.7f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.7f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.7f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.7f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.7f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.25f, 1f, 0f, 0f,
            0f,  0.25f, 0f, 0f, 1f,
    };
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgramId = OpenGLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        glUseProgram(mProgramId);
        mPositionLocation = glGetAttribLocation(mProgramId, "a_position");
        mColorLocation = glGetAttribLocation(mProgramId, "a_color");
        mProjectMatrixLocation = glGetUniformLocation(mProgramId, "u_matrix");

        mVertexData = ByteBuffer.allocateDirect(VERTEX_ARRAY.length * BYTES_PRE_FLOAT).
                order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexData.put(VERTEX_ARRAY);

        mVertexData.position(0);
        glVertexAttribPointer(mPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(mPositionLocation);
        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(mColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(mColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        if (width > height) {
            float ratio = width / (float)height;
            Matrix.orthoM(mProjectMatrixArray, 0, -ratio, ratio, -1, 1, -1, 1);
        } else {
            float ratio = height / (float)width;
            Matrix.orthoM(mProjectMatrixArray, 0, -1, 1, -ratio, ratio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(mProjectMatrixLocation, 1, false, mProjectMatrixArray, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        glDrawArrays(GL_LINES, 6, 2);

        glDrawArrays(GL_POINTS, 8, 1);

        glDrawArrays(GL_POINTS, 9, 1);
    }
}

