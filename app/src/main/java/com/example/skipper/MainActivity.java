package com.example.skipper;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.skipper.Constants.DARK_THEME;
import static com.example.skipper.Constants.LIGHT_THEME;
import static com.example.skipper.Constants.SHREK;

public class MainActivity extends AppCompatActivity {

    /** THIS APP IS FOR TESTING AND EDUCATION ONLY
     **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final int theme = intent.getIntExtra("THEME", LIGHT_THEME);
        if(theme == DARK_THEME) setTheme(R.style.DarkTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailText = findViewById(R.id.editTextEmail);
                EditText subjectText = findViewById(R.id.editTextSubject);
                String[] TO = {emailText.getText().toString()};

                Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                email.putExtra(Intent.EXTRA_EMAIL, TO);
                email.putExtra(Intent.EXTRA_SUBJECT, subjectText.getText().toString());
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Send via.."));

            }
        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = findViewById(R.id.editTextSearch);
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, searchText.getText().toString());
                startActivity(intent);
            }
        });

        Button shrekButton = findViewById(R.id.shrekButton);
        shrekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OpenGLActivity.class);
                intent.putExtra("TYPE", SHREK);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.toastButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
