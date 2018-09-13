package com.example.android.media.v2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    // VideoTextureView的可见性为VISIBLE时，完成初始化后会自动开始播放视频。
    // 因此，首次播放视频时，直接将TextureView设置为VISIBLE即可。
    //
    // 关于暂停/继续播放：当TextureView没有被销毁时，暂停播放调用pause()即可，继续播放调用resume()。
    // 关于onPause()：切换App或回到桌面，系统会执行onPause，此时TextureView已经available（可见性为VISIBLE/INVISIBLE，不可能为GONE）的话，会destroy。
    // 关于onResume()：在onPause()时TextureView可见性为VISIBLE的话，此时TextureView会重新创建。

    MediaPlayer mediaPlayer;
    private int mPlayingPosition;
    private Context mContext;
    private Uri mUri;
    private VideoUtil.DisplayType mType = VideoUtil.DisplayType.CENTER;
    private boolean mIsMediaPlaying = false;
    // events
    public interface IEvent {
        void onPlay();
    }
    public void setEvent(IEvent event) {
        mEvent = event;
    }
    private IEvent mEvent;

    public VideoTextureView(Context context) {
        super(context);
        init(context);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        log(" --------- init ");

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mediaPlayer == null) {
            log("  ------- onAvailable ... 1 ");
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mContext, mUri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 此处设置封面图隐藏，然后播放视频
                        if (mEvent != null) {
                            mEvent.onPlay();
                        }
                        log("  ------- onPrepared ...");
                        Surface surface = new Surface(getSurfaceTexture());
                        mediaPlayer.setSurface(surface);
                        mp.start();
                        mIsMediaPlaying = true;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayingPosition = 0;
                        mp.seekTo(0);
                        mIsMediaPlaying = false;
                        //mp.start(); // 循环播放
                    }
                });
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        log(String.format(" video . on size changed ...... %s %s ", width, height));
                        adjustVideoSize(width, height);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log("  ------- onAvailable ... 1222 ");
            Surface surface = new Surface(getSurfaceTexture());
            mediaPlayer.setSurface(surface);
            if (mPlayingPosition > 0 && mPlayingPosition < mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(mPlayingPosition);
            }
            mediaPlayer.start();
            mIsMediaPlaying = true;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        log("  ------- onSize ... 1333 ");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        log("  ------- onDestroy ... 1444 ");
        if (mediaPlayer != null && mIsMediaPlaying) {
            mediaPlayer.pause();
        }
        mIsMediaPlaying = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

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
        if (mediaPlayer != null && mIsMediaPlaying) {
            mPlayingPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            mIsMediaPlaying = false;
        }
    }

    public void resume() {
        // 如果onPause()执行了暂停播放视频操作，此处继续播放视频
        if (mediaPlayer != null && !mIsMediaPlaying) {
            mediaPlayer.start();
            mIsMediaPlaying = true;
        }
    }

    public boolean isPlaying() {
        log(String.format("isPlaying? %s  //  is null? %s  //  isPlaying? %s", mIsMediaPlaying, (mediaPlayer == null), (mediaPlayer != null ? mediaPlayer.isPlaying() : "null")));
        return mIsMediaPlaying;
    }

    private void log(String msg) { Log.d("--->>>", msg); }

}
