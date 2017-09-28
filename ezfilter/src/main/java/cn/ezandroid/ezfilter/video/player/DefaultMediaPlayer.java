package cn.ezandroid.ezfilter.video.player;

import android.media.MediaPlayer;

/**
 * 默认的视频播放器，采用系统MediaPlayer
 *
 * @author like
 * @date 2017-08-16
 */
public class DefaultMediaPlayer extends MediaPlayer implements IMediaPlayer {

    @Override
    public void setOnPreparedListener(final IMediaPlayer.OnPreparedListener var1) {
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                var1.onPrepared(DefaultMediaPlayer.this);
            }
        });
    }

    @Override
    public void setOnCompletionListener(final IMediaPlayer.OnCompletionListener var1) {
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                var1.onCompletion(DefaultMediaPlayer.this);
            }
        });
    }
}