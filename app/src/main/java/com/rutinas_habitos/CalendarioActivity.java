package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, InicioActivity.class);
        intent.putExtra("open_tab", 2);
        startActivity(intent);
        finish();
    }
}
