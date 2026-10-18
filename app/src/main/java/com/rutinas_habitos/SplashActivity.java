package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class SplashActivity extends AppCompatActivity {

    private boolean yaNavego = false;
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivSplashGif = findViewById(R.id.iv_splash_gif);

        timeoutRunnable = this::navegarSiguientePantalla;
        timeoutHandler.postDelayed(timeoutRunnable, 3000);

        try {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.splash_logo)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .format(DecodeFormat.PREFER_RGB_565))
                    .centerCrop()
                    .into(ivSplashGif);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void navegarSiguientePantalla() {
        if (yaNavego) return;
        yaNavego = true;

        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        SessionManager sessionManager = new SessionManager(SplashActivity.this);
        Intent intent;

        if (sessionManager.estaAutenticado()) {
            intent = new Intent(SplashActivity.this, InicioActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }
}
