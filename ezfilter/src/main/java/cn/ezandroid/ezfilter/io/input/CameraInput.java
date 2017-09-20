package cn.ezandroid.ezfilter.io.input;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

import cn.ezandroid.ezfilter.core.FBORender;
import cn.ezandroid.ezfilter.view.IRender;

/**
 * CameraInput
 *
 * @author like
 * @date 2017-09-16
 */
public class CameraInput extends FBORender implements SurfaceTexture.OnFrameAvailableListener {

    private static final String UNIFORM_CAM_MATRIX = "u_Matrix";

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;

    private int mMatrixHandle;
    private float[] mMatrix = new float[16];

    private IRender mRender;

    private Camera.Size mPreviewSize;

    public CameraInput(IRender render, Camera camera) {
        super();
        this.mRender = render;
        this.mCamera = camera;
        Camera.Parameters params = mCamera.getParameters();
        this.mPreviewSize = params.getPreviewSize();
    }

    @Override
    protected void drawFrame() {
        try {
            mSurfaceTexture.updateTexImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.drawFrame();
    }

    @Override
    protected String getVertexShader() {
        return "uniform mat4 " + UNIFORM_CAM_MATRIX + ";\n"
                + "attribute vec4 " + ATTRIBUTE_POSITION + ";\n"
                + "attribute vec2 " + ATTRIBUTE_TEXTURE_COORD + ";\n"
                + "varying vec2 " + VARYING_TEXTURE_COORD + ";\n"
                + "void main() {\n"
                + "   vec4 texPos = " + UNIFORM_CAM_MATRIX + " * vec4(" + ATTRIBUTE_TEXTURE_COORD + ", 1, 1);\n"
                + "   " + VARYING_TEXTURE_COORD + " = texPos.xy;\n"
                + "   gl_Position = " + ATTRIBUTE_POSITION + ";\n"
                + "}\n";
    }

    @Override
    protected String getFragmentShader() {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES " + UNIFORM_TEXTURE_0 + ";\n"
                + "varying vec2 " + VARYING_TEXTURE_COORD + ";\n"
                + "void main() {\n"
                + "   gl_FragColor = texture2D(" + UNIFORM_TEXTURE_0 + ", " + VARYING_TEXTURE_COORD + ");\n"
                + "}\n";
    }

    @Override
    protected void initShaderHandles() {
        super.initShaderHandles();
        mMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_CAM_MATRIX);
    }

    @Override
    protected void initGLContext() {
        super.initGLContext();

        if (mTextureIn != 0) {
            int[] tex = new int[1];
            tex[0] = mTextureIn;
            GLES20.glDeleteTextures(1, tex, 0);
            mTextureIn = 0;
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        mTextureIn = textures[0];

        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mSurfaceTexture = new SurfaceTexture(mTextureIn);
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setRenderSize(mPreviewSize.height, mPreviewSize.width);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture arg0) {
        mRender.requestRender();
    }

    @Override
    protected void bindShaderValues() {
        mRenderVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false,
                8, mRenderVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        mTextureVertices[mCurrentRotation].position(0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
                8, mTextureVertices[mCurrentRotation]);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureIn);
        GLES20.glUniform1i(mTextureHandle, 0);

        mSurfaceTexture.getTransformMatrix(mMatrix);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix, 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mTextureIn != 0) {
            int[] tex = new int[1];
            tex[0] = mTextureIn;
            GLES20.glDeleteTextures(1, tex, 0);
            mTextureIn = 0;
        }
    }
}
