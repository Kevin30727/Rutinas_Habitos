package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 segundos para mostrar el GIF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivSplashGif = findViewById(R.id.iv_splash_gif);

        // Cargar el GIF animado usando Glide garantizando reproducción sin distorsión
        try {
            int gifResourceId = getResources().getIdentifier("splash_logo", "drawable", getPackageName());
            if (gifResourceId != 0) {
                Glide.with(this)
                        .asGif()
                        .load(gifResourceId)
                        .fitCenter()
                        .into(ivSplashGif);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Navegar después de la animación
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            Intent intent;

            if (sessionManager.estaAutenticado()) {
                intent = new Intent(SplashActivity.this, InicioActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
