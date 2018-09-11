package com.example.android.media.v2;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class VideoSurface extends SurfaceView {

    MediaPlayer mediaPlayer;
    private int mPlayingPosition;
    private Uri mUri;
    private VideoUtil.DisplayType mType = VideoUtil.DisplayType.CENTER;
    private Context mContext;

    public VideoSurface(Context context) {
        super(context, null);
        init(context);
    }

    public VideoSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                log("video surface. create");
                if (mediaPlayer == null) {
                    startPlay(holder);
                } else {
                    if (mPlayingPosition > 0 && mPlayingPosition < mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(mPlayingPosition);
                    }
                    mediaPlayer.setDisplay(holder);
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        log("video.start 1");
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                log("video surface. change 2");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                log("video surface. destroy 3");

            }
        });
    }

    private void startPlay(SurfaceHolder holder) {
        // todo throw exception.
        if (mUri == null) return;

        mediaPlayer = MediaPlayer.create(mContext, mUri, holder);
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                adjustVideoSize(width, height);
            }
        });
        mediaPlayer.start();
        log("video.start 2");
    }

    private void adjustVideoSize(int width, int height) {
        VideoUtil.adjustSize(this, width, height, mType);
    }

    public void setVideoDisplayType(VideoUtil.DisplayType type) {
        mType = type;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public void pause() {
        // 如果正在播放视频，此处执行暂停播放操作
        //if (getVisibility() != VISIBLE) return;

        //if (mediaPlayer != null) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mPlayingPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            log("video.pause");
        }
    }

    public void resume() {
        // 如果onPause()执行了暂停播放视频操作，此处继续播放视频
        if (getVisibility() != VISIBLE) {
            setVisibility(View.VISIBLE);
            return;
        }

        if (mediaPlayer == null) {
            startPlay(getHolder());
            return;
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            log("video.start 3");
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    private void log(String msg) {
        Log.d("--->>>", msg);
    }

}
