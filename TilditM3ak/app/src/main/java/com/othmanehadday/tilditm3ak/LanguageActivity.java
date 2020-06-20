package com.othmanehadday.tilditm3ak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    private ImageButton buttonAr,buttonChalha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        buttonAr = findViewById(R.id.buttonAr);
        buttonChalha = findViewById(R.id.buttonchalha);

        buttonAr.setOnClickListener((evt)->{
            setLocale("ar");
        });

        buttonChalha.setOnClickListener((evt)->{
            setLocale("ber");
        });

    }



    @SuppressWarnings("deprecation")
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        Configuration conf = getResources().getConfiguration();
        conf.locale = myLocale;
        getResources().updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
