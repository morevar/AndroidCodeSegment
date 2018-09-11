package com.example.android.media;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.android.R;
import com.example.android.media.v2.VideoSurface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 使用SurfaceView播放视频的Demo
 * 在 {@link ActVideoDemo1} 的基础上修改，提取出了 {@link VideoSurface} 和 {@link com.example.android.media.v2.VideoUtil} 两个类
 */
public class ActVideoDemo2 extends AppCompatActivity {

    VideoSurface mVideoView;
    ImageSurface mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_demo2);

        ViewGroup root = findViewById(R.id.lay_root);

        mImageView = new ImageSurface(this);
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
        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(lp);
        root.addView(mImageView);

        mVideoView = new VideoSurface(this);
        mVideoView.setVisibility(View.GONE);
        mVideoView.setUri(Uri.parse("http://s3.bytecdn.cn/aweme/resource/web/static/image/index/tvc-v2_30097df.mp4"));
        root.addView(mVideoView);

        findViewById(R.id.lay_op).bringToFront();
        findViewById(R.id.btn_sp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView != null && mVideoView.isPlaying()) {
                    // 停止播放视频
                    mVideoView.pause();

                    mVideoView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    ((Button) v).setText("Play");
                } else {
                    // 继续播放视频
                    mImageView.setVisibility(View.GONE);
                    //mVideoView.setVisibility(View.VISIBLE);
                    if (mVideoView != null) {
                        mVideoView.resume();
                    }
                    ((Button) v).setText("Pause");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (mVideoView != null) {
            mIsPlayingBeforePause = mVideoView.isPlaying();
            mVideoView.pause();
        }
        super.onPause();
    }

    private boolean mIsPlayingBeforePause = false;

    @Override
    protected void onResume() {
        if (mVideoView != null && mIsPlayingBeforePause) {
            mVideoView.resume();
        }
        super.onResume();
    }
}