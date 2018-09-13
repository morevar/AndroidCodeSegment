package com.example.android.media;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.android.R;
import com.example.android.media.v2.VideoTextureView;
import com.example.android.media.v2.VideoUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActVideoDemo201 extends AppCompatActivity {


    VideoTextureView mVideoView;
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
        //mImageView.setVisibility(View.GONE);
        root.addView(mImageView);

        mVideoView = new VideoTextureView(this);
        mVideoView.setEvent(new VideoTextureView.IEvent() {
            @Override
            public void onPlay() {
                //mImageView.setVisibility(View.INVISIBLE);
            }
        });
        mVideoView.setVisibility(View.GONE);
        mVideoView.setVideoDisplayType(VideoUtil.DisplayType.CENTER_CROP);
        mVideoView.setUri(Uri.parse("http://s3.bytecdn.cn/aweme/resource/web/static/image/index/tvc-v2_30097df.mp4"));
        root.addView(mVideoView);

        findViewById(R.id.lay_op).bringToFront();
        findViewById(R.id.btn_sp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView != null && mVideoView.isPlaying()) {
                    Log.d("--->>>", "onClick: Pause ...");
                    // 停止播放视频
                    mVideoView.pause();
                    mVideoView.setVisibility(View.INVISIBLE);
                    //mImageView.setVisibility(View.VISIBLE);
                    ((Button) v).setText("Play");
                } else {
                    Log.d("--->>>", "onClick: Play ...");
                    // 继续播放视频
                    mVideoView.setVisibility(View.VISIBLE);
                    if (mVideoView != null) {
                        mVideoView.resume();
                    }
                    ((Button) v).setText("Pause");
                }
            }
        });
    }

    private boolean mIsMediaPlayingBeforePause = false;
    @Override
    protected void onPause() {
        if (mVideoView.isPlaying()) {
            mIsMediaPlayingBeforePause = true;
            mVideoView.pause();
        } else {
            mIsMediaPlayingBeforePause = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 此处直接调用VideoView的resume可能会出现短时间的黑屏/白屏（因为onPause时Surface会destroy，onResume时重新创建需要时间）
        Log.d("--->>>", "onResume: ---------");
        super.onResume();
        if (mIsMediaPlayingBeforePause) {
            // TODO loading view here
            mVideoView.resume();
        }
    }
}
