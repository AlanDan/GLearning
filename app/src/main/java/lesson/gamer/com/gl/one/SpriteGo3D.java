package lesson.gamer.com.gl.one;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by daiepngfei on 7/8/16
 */
public class SpriteGo3D extends FragmentActivity {

    private GLSurfaceView mGLSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(1);
        mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        mGLSurfaceView.setRenderer(new GLSurfaceView.Renderer() {

            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                // Set the background color to black ( rgba ).
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);  // OpenGL docs.
                // Enable Smooth Shading, default not really needed.
                gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
                // Depth buffer setup.
                gl.glClearDepthf(1.0f);// OpenGL docs.
                // Enables depth testing.
                gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
                // The type of depth testing to do.
                gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
                // Really nice perspective calculations.
                gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
                        GL10.GL_NICEST);
            }


            public void onDrawFrame(GL10 gl) {

                // Clears the screen and depth buffer.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
                        GL10.GL_DEPTH_BUFFER_BIT);

                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, -4);


                gl.glFrontFace(GL10.GL_CCW);
                gl.glEnable(GL10.GL_CULL_FACE);
                gl.glCullFace(GL10.GL_BACK);

                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFloatBuffer);

                gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, mShortBuffer);


                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glDisable(GL10.GL_CULL_FACE);
            }


            public void onSurfaceChanged(GL10 gl, int width, int height) {
                // Sets the current view port to the new size.
                gl.glViewport(0, 0, width, height);// OpenGL docs.
                // Select the projection matrix
                gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
                // Reset the projection matrix
                gl.glLoadIdentity();// OpenGL docs.
                // Calculate the aspect ratio of the window
                GLU.gluPerspective(gl, 45.0f,
                        (float) width / (float) height,
                        0.1f, 100.0f);
                // Select the modelview matrix
                gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
                // Reset the modelview matrix
                gl.glLoadIdentity();// OpenGL docs.
            }

        });
        setContentView(mGLSurfaceView);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mFloatBuffer = byteBuffer.asFloatBuffer();
        mFloatBuffer.put(vertices);
        mFloatBuffer.position(0);

        ByteBuffer iBuffer = ByteBuffer.allocateDirect(indices.length * 2);
        iBuffer.order(ByteOrder.nativeOrder());
        mShortBuffer = iBuffer.asShortBuffer();
        mShortBuffer.put(indices);
        mShortBuffer.position(0);

    }

    private short[] indices = {0, 1, 2, 0, 2, 3};

    FloatBuffer mFloatBuffer;
    ShortBuffer mShortBuffer;

    private float vertices[] = {
            -1.0f, 1.0f, 0.0f,  // 0, Top Left
            -1.0f, -1.0f, 0.0f,  // 1, Bottom Left
            1.0f, -1.0f, 0.0f,  // 2, Bottom Right
            1.0f, 1.0f, 0.0f,  // 3, Top Right
    };


    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }


}
