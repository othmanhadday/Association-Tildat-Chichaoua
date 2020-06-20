package com.othmanehadday.tilditm3ak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonRec, buttonCallUs, buttonMessageUs,imageButtonAbout ,imageButtonFb, imageButtonYoutube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRec = findViewById(R.id.buttonReclamation);
        buttonCallUs = findViewById(R.id.buttonCallUs);
        buttonMessageUs = findViewById(R.id.buttonMessageUs);
        imageButtonFb = findViewById(R.id.imageButtonFb);
        imageButtonYoutube = findViewById(R.id.imageButtonYoutube);
        imageButtonAbout = findViewById(R.id.imageButtonAbout);


        buttonCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+212668174663"));
                startActivity(intent);
            }
        });

        buttonMessageUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:+212668174663"));
                startActivity(intent);
            }
        });

        buttonRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View evt) {
                Intent intent = new Intent(getApplicationContext(), ReclamationActivity.class);
                startActivity(intent);
            }
        });

        imageButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View evt) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        imageButtonYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://m.youtube.com/channel/UCGl8P8WufFRi3tKBLw-I0FQ"));

                startActivity(intent);
            }
        });

        imageButtonFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ASSOCIATIONTILDAT/"));
                startActivity(intent);
            }
        });

    }
}
