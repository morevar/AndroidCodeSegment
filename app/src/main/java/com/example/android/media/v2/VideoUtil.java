package com.example.android.media.v2;

import android.media.MediaPlayer;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;

public class VideoUtil {

    public enum DisplayType {
        /**
         * 居中展示（不进行缩放，可能会有平移）
         */
        CENTER,
        /**
         * 居中展示（放大+平移）
         */
        CENTER_CROP,
        /**
         * 居中展示（缩小+平移）
         */
        CENTER_INSIDE,
        /**
         * 铺满屏幕（可能会变形）
         */
        FIT_XY
    }

    /**
     * 调整SurfaceView的宽高与translation
     * @param surfaceView 展示图片或视频的SurfaceView/TextureView
     * @param width {@link MediaPlayer.OnVideoSizeChangedListener#onVideoSizeChanged} 方法中的width
     * @param height {@link MediaPlayer.OnVideoSizeChangedListener#onVideoSizeChanged} 方法中的height
     * @param type 图片或视频的展示方式
     */
    public static void adjustSize(View surfaceView, int width, int height, DisplayType type) {
        int rootWidth;  // = windowManager.getDefaultDisplay().getWidth();
        int rootHeight; // = windowManager.getDefaultDisplay().getHeight();
        ViewParent viewParent = surfaceView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            rootWidth = v.getWidth();
            rootHeight = v.getHeight();
        } else {
            rootWidth = width;
            rootHeight = height;
        }

        // 1, 设置宽高
        android.view.ViewGroup.LayoutParams surfaceParams = surfaceView.getLayoutParams();
        switch (type) {
            case CENTER:
            default:
                surfaceParams.width = width;
                surfaceParams.height = height;
                break;
            case CENTER_CROP:
            case CENTER_INSIDE:
                float rootViewAspectRatio = rootWidth * 1.0f / rootHeight;
                float mediaAspectRatio = width * 1.0f / height;
                boolean fitWidth;
                if (type == DisplayType.CENTER_CROP) {
                    fitWidth = mediaAspectRatio < rootViewAspectRatio;
                } else {
                    fitWidth = mediaAspectRatio > rootViewAspectRatio;
                }

                if (fitWidth) {
                    // 宽度填充，高度等比例放大/缩小
                    surfaceParams.width = rootWidth;
                    surfaceParams.height = (int) ((rootWidth * 1.0f / width) * height);
                } else {
                    // 高度填充，宽度等比例放大/缩小
                    surfaceParams.height = rootHeight;
                    surfaceParams.width = (int) ((rootHeight * 1.0f / height) * width);
                }
                break;
            case FIT_XY:
                surfaceParams.width = rootWidth;
                surfaceParams.height = rootHeight;
                break;
        }
        surfaceView.setLayoutParams(surfaceParams);

        // 2, 进行平移
        float tx = (rootWidth - surfaceParams.width) / 2.0f;
        float ty = (rootHeight - surfaceParams.height) / 2.0f;
        surfaceView.setTranslationX(tx);
        surfaceView.setTranslationY(ty);

    }
}
