package com.example.android.media;

import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.example.android.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActVideoDemo1 extends AppCompatActivity implements View.OnClickListener {
    SurfaceView mVideoView;
    ImageSurface mImageView;
    MediaPlayer mediaPlayer;
    boolean mIsPlayingBeforePause;
    private int mPlayingPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_demo1);

        mImageView = findViewById(R.id.sv_1);
        mImageView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // todo 渲染图片
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas(null);
                    synchronized (holder) {
                        mImageView.drawImage(canvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        mVideoView = findViewById(R.id.sv_2);
//        mVideoView.setVideoDisplayType(VideoSurface.VIDEO_IMAGE_DISPLAY_TYPE.FILL_SCROP);
        mVideoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                // todo 播放视频
                if (mediaPlayer == null) {
                    //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.douyin_ad_video);
                    Uri uri = Uri.parse("http://s3.bytecdn.cn/aweme/resource/web/static/image/index/tvc-v2_30097df.mp4");

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri, holder);

                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            //adjustSizeCenter(mp, width, height);
                            //adjustSizeCenterCrop(mp, width, height);
                            //adjustSizeCenterInside(mp, width, height);
                            //adjustSizeFitXY(mp, width, height);
                            adjustSize(mVideoView, width, height, DisplayType.CENTER_INSIDE);
                            //adjustSize(mVideoView, width, height, DisplayType.CENTER_CROP);
                            //adjustSize(mVideoView, width, height, DisplayType.CENTER);
                            //adjustSize(mVideoView, width, height, DisplayType.FIT_XY);
                        }
                    });
                    mediaPlayer.start();
                } else {
                    if (mPlayingPosition > 0 && mPlayingPosition < mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(mPlayingPosition);
                    }
                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        findViewById(R.id.btn_sp).setOnClickListener(this);
    }

    enum DisplayType {
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
     *
     * @param surfaceView 展示图片或视频的SurfaceView
     * @param width       {@link MediaPlayer.OnVideoSizeChangedListener#onVideoSizeChanged} 方法中的width
     * @param height      {@link MediaPlayer.OnVideoSizeChangedListener#onVideoSizeChanged} 方法中的height
     * @param type        图片或视频的展示方式
     */
    private void adjustSize(SurfaceView surfaceView, int width, int height, DisplayType type) {
        int rootWidth = getWindowManager().getDefaultDisplay().getWidth();
        int rootHeight = getWindowManager().getDefaultDisplay().getHeight();
        ViewParent viewParent = surfaceView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            rootWidth = v.getWidth();
            rootHeight = v.getHeight();
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
                if (width <= rootWidth && height <= rootHeight) {
                    // 设置宽高
                    surfaceParams.width = width;
                    surfaceParams.height = height;
                } else {
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

    // 居中展示（不进行缩放，只平移）
    private void adjustSizeCenter(MediaPlayer mp, int width, int height) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        ViewParent viewParent = mVideoView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            screenWidth = v.getWidth();
            screenHeight = v.getHeight();
        }

        // 1, 设置宽高
        android.view.ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();
        videoParams.width = width;
        videoParams.height = height;
        mVideoView.setLayoutParams(videoParams);

        // 2, 进行平移
        float tx = (screenWidth - width) / 2.0f;
        float ty = (screenHeight - height) / 2.0f;
        mVideoView.setTranslationX(tx);
        mVideoView.setTranslationY(ty);
    }

    // 居中展示（放大+平移）
    private void adjustSizeCenterCrop(MediaPlayer mp, int width, int height) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        ViewParent viewParent = mVideoView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            screenWidth = v.getWidth();
            screenHeight = v.getHeight();
        }

        // 1, 设置宽高
        android.view.ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();
        if (width <= screenWidth && height <= screenHeight) {
            // 设置宽高
            videoParams.width = width;
            videoParams.height = height;
        } else {
            float screenAspectRatio = screenWidth * 1.0f / screenHeight;
            float videoAspectRatio = width * 1.0f / height;
            if (videoAspectRatio > screenAspectRatio) {
                // 高度填充，宽度等比例放大
                videoParams.height = screenHeight;
                videoParams.width = (int) ((screenHeight * 1.0f / height) * width);
            } else {
                // 宽度填充，高度等比例放大
                videoParams.width = screenWidth;
                videoParams.height = (int) ((screenWidth * 1.0f / width) * height);
            }
        }
        mVideoView.setLayoutParams(videoParams);

        // 2, 进行平移
        float tx = (screenWidth - videoParams.width) / 2.0f;
        float ty = (screenHeight - videoParams.height) / 2.0f;
        mVideoView.setTranslationX(tx);
        mVideoView.setTranslationY(ty);
    }

    // 居中展示（缩小+平移）
    private void adjustSizeCenterInside(MediaPlayer mp, int width, int height) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        ViewParent viewParent = mVideoView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            screenWidth = v.getWidth();
            screenHeight = v.getHeight();
        }

        // 1, 设置宽高
        android.view.ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();
        if (width <= screenWidth && height <= screenHeight) {
            // 设置宽高
            videoParams.width = width;
            videoParams.height = height;
        } else {
            float screenAspectRatio = screenWidth * 1.0f / screenHeight;
            float videoAspectRatio = width * 1.0f / height;
            if (videoAspectRatio > screenAspectRatio) {
                // 宽度填充，高度等比例缩小
                videoParams.width = screenWidth;
                videoParams.height = (int) ((screenWidth * 1.0f / width) * height);
            } else {
                // 高度填充，宽度等比例缩小
                videoParams.height = screenHeight;
                videoParams.width = (int) ((screenHeight * 1.0f / height) * width);
            }
        }
        mVideoView.setLayoutParams(videoParams);

        // 2, 进行平移
        float tx = (screenWidth - videoParams.width) / 2.0f;
        float ty = (screenHeight - videoParams.height) / 2.0f;
        mVideoView.setTranslationX(tx);
        mVideoView.setTranslationY(ty);
    }

    // 宽高均填充满屏幕
    private void adjustSizeFitXY(MediaPlayer mp, int width, int height) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        ViewParent viewParent = mVideoView.getParent();
        if (viewParent != null) {
            View v = (View) viewParent;
            screenWidth = v.getWidth();
            screenHeight = v.getHeight();
        }

        // 1, 设置宽高
        android.view.ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();
        videoParams.width = screenWidth;
        videoParams.height = screenHeight;
        mVideoView.setLayoutParams(videoParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果正在播放视频，此处执行暂停播放操作
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mIsPlayingBeforePause = true;
            mPlayingPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        } else {
            mIsPlayingBeforePause = false;
        }
    }

    @Override
    protected void onResume() {
        // 如果onPause()执行了暂停播放视频操作，此处继续播放视频
        if (mIsPlayingBeforePause) {
            mImageView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sp:
                if (isDisplayVideoNow()) {
                    // 停止播放视频
                    mPlayingPosition = mediaPlayer.getCurrentPosition();
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
                    mediaPlayer.pause();

                    mVideoView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    ((Button) v).setText("Play");
                } else {
                    // 继续播放视频
                    mImageView.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.VISIBLE);
                    ((Button) v).setText("Pause");
                }
                break;
        }
    }

    private boolean isDisplayVideoNow() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }
}
