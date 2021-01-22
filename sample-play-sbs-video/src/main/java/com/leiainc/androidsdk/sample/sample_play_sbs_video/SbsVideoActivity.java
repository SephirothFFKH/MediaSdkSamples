package com.leiainc.androidsdk.sample.sample_play_sbs_video;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.leiainc.androidsdk.core.QuadView;
import com.leiainc.androidsdk.core.SurfaceTextureReadyCallback;
import com.leiainc.androidsdk.display.LeiaDisplayManager;
import com.leiainc.androidsdk.display.LeiaSDK;
import com.leiainc.androidsdk.sbs.video.SbsVideoSurfaceRenderer;
import com.leiainc.androidsdk.sbs.video.TextureShape;


public class SbsVideoActivity extends Activity implements SurfaceTextureReadyCallback {
    private SbsVideoSurfaceRenderer sbsVideoSurfaceRenderer = null;
    private SimpleExoPlayer exoPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbs_video);

        QuadView quadView = findViewById(R.id.quad_view);
        quadView.getInputSurfaceTexture(this);

        // Initialize ExoPlayer
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
    }

    @Override
    public void onSurfaceTextureReady(SurfaceTexture surfaceTexture) {
        if (sbsVideoSurfaceRenderer == null) {
            sbsVideoSurfaceRenderer = new SbsVideoSurfaceRenderer(
                    this,
                    new Surface(surfaceTexture),
                    TextureShape.LANDSCAPE,
                    (surfaceTexure) -> {
                        configureExoplayer(surfaceTexure);
                    });
        }
    }

    private void configureExoplayer(SurfaceTexture surfaceTexture) {
        exoPlayer.setVideoSurface(new Surface(surfaceTexture));

        String userAgent = Util.getUserAgent(this, "exoplayer2example");
        Uri uri = Uri.parse(
                "https://dev.streaming.leialoft.com/out/v1/08cd49f09fbc4a1e9c063424fa0bfc00/7845cda1bdd5494db13a24f5d13374ea/adacb4edf0434177ae441f124d989fe7/index.mpd"
        );
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        MediaSource videoSource =
                new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        exoPlayer.prepare(loopingSource);
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
        LeiaDisplayManager displayManager = LeiaSDK.getDisplayManager(this);
        displayManager.requestBacklightMode(LeiaDisplayManager.BacklightMode.MODE_3D);

        /*  Make app full screen */
        setFullScreenImmersive();
        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
        LeiaDisplayManager displayManager = LeiaSDK.getDisplayManager(this);
        displayManager.requestBacklightMode(LeiaDisplayManager.BacklightMode.MODE_2D);
        exoPlayer.setPlayWhenReady(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        sbsVideoSurfaceRenderer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    private void setFullScreenImmersive() {
        int flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        View decorView = this.getWindow().getDecorView();
        decorView.clearFocus();
        decorView.setSystemUiVisibility(flags);

    }

}
