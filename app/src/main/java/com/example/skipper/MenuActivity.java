package com.example.skipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.skipper.Constants.AIR_HOCKEY;
import static com.example.skipper.Constants.DARK_THEME;
import static com.example.skipper.Constants.LIGHT_THEME;

public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final int theme = intent.getIntExtra("THEME", 0);
        if(theme == DARK_THEME) setTheme(R.style.DarkTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_menu);

        Button openGLButton = findViewById(R.id.playButton);
        openGLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OpenGLActivity.class);
                intent.putExtra("TYPE", AIR_HOCKEY);
                intent.putExtra("THEME", theme);
                startActivity(intent);
            }
        });

        Button optionsButton = findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                if(theme == DARK_THEME)intent.putExtra("THEME", LIGHT_THEME);
                else intent.putExtra("THEME", DARK_THEME);
                startActivity(intent);
                finish();
            }
        });

        Button extrasButton = findViewById(R.id.extrasButton);
        extrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("THEME", theme);
                startActivity(intent);
            }
        });
    }
}
