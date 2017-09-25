package cn.ezandroid.ezfilter.core.util;import android.graphics.Bitmap;import android.opengl.GLES20;import android.opengl.GLUtils;/** * 纹理绑定工具 * * @author like * @date 2017-08-10 */public class TextureBindUtil {    /**     * 将bitmap绑定为纹理     *     * @param bitmap     * @return     */    public static int bindBitmap(Bitmap bitmap) {        int[] tex = new int[1];        GLES20.glGenTextures(1, tex, 0);        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0]);        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,                GLES20.GL_CLAMP_TO_EDGE);        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,                GLES20.GL_CLAMP_TO_EDGE);        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);        return tex[0];    }}