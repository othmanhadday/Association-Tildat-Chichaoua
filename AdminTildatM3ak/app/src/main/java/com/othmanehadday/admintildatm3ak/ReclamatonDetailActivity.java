package com.othmanehadday.admintildatm3ak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.othmanehadday.admintildatm3ak.model.ReclamationModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ReclamatonDetailActivity extends AppCompatActivity {
    private StorageReference mStorageRef;

    private TextView textViewNme, textViewAddress, textViewPhone, textViewRec, textViewDate,textViewAudioFailed;
    private ImageButton imageButtonPlay, imageButtonPause;
    private SeekBar seekBarAudio;

    private File rootPath, localFile;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamaton_detail);

        //Check permission form external stockage
        CheckPermssion(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_STATUS);

        //Notification Firebase
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        Intent intent = getIntent();
        ReclamationModel reclamationModel = (ReclamationModel) intent.getSerializableExtra("reclamation");

        //initialaze Layout View
        textViewNme = findViewById(R.id.textViewShowName);
        textViewAddress = findViewById(R.id.textViewShowAddress);
        textViewPhone = findViewById(R.id.textViewShowPhone);
        textViewRec = findViewById(R.id.textViewShowRec);
        textViewDate = findViewById(R.id.textViewDate);
        textViewAudioFailed = findViewById(R.id.textViewAudioFailed);
        imageButtonPlay = findViewById(R.id.imageViewPlay);
        imageButtonPause = findViewById(R.id.imageViewPause);
        seekBarAudio = findViewById(R.id.seekBarAudio);

        //set object data in view layout
        textViewNme.setText(reclamationModel.getFullName());
        textViewAddress.setText(reclamationModel.getAddress());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy hh:mm aa");
        String currentDate = dateFormat.format(reclamationModel.getCurrentDate());
        textViewDate.setText(currentDate);
        textViewPhone.setText(reclamationModel.getPhone());
        if (!reclamationModel.getViolenceCategorie().isEmpty()) {
            textViewRec.setText(reclamationModel.getViolenceCategorie());
        }else{
            textViewRec.setText("لا توجد شكاية");
        }

        //downoald Audio from firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        downoaldAudio(reclamationModel.getId(), reclamationModel.getFullName());

        //play and pause audio
        imageButtonPlay.setOnClickListener((evt) -> {
            try {
                if (mediaPlayer == null) {
                    Uri uri = Uri.parse(localFile.toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                }
                mediaPlayer.start();
                imageButtonPause.setVisibility(View.VISIBLE);
                imageButtonPlay.setVisibility(View.GONE);
                initializeSeekbar();
            } catch (NullPointerException e) {
                imageButtonPause.setVisibility(View.GONE);
                imageButtonPlay.setVisibility(View.VISIBLE);
            }

        });

        imageButtonPause.setOnClickListener((evt) -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            imageButtonPause.setVisibility(View.GONE);
            imageButtonPlay.setVisibility(View.VISIBLE);
        });

        //seek bar initialise
        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initializeSeekbar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        try {
                            final double current = mediaPlayer.getCurrentPosition();
                            runOnUiThread(() -> {
                                seekBarAudio.setMax(mediaPlayer.getDuration());
                                seekBarAudio.setProgress((int) current);
                                imageButtonPause.setVisibility(View.VISIBLE);
                                imageButtonPlay.setVisibility(View.GONE);
                            });

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!mediaPlayer.isPlaying()) {
                        runOnUiThread(() -> {
                            imageButtonPause.setVisibility(View.GONE);
                            imageButtonPlay.setVisibility(View.VISIBLE);
                            seekBarAudio.setProgress(0);
                        });
                    }
                }
            }
        }).start();
    }

    private void downoaldAudio(String id, String name) {
        rootPath = new File(Environment.getExternalStorageDirectory(), "TildatM3akAudios");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        localFile = new File(rootPath, id + name + ".mp3");
        if (!localFile.exists()) {
            StorageReference audioref = mStorageRef.child("Audio").child(id + name + ".3gp");
            audioref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Audio added", Toast.LENGTH_LONG).show();
                    findViewById(R.id.AudioLayout).setVisibility(View.VISIBLE);
                    textViewAudioFailed.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Audio Faild", Toast.LENGTH_LONG).show();
                    findViewById(R.id.AudioLayout).setVisibility(View.GONE);
                    textViewAudioFailed.setText("لا يوجد تسجيل الصوتي");
                    e.printStackTrace();
                }
            });
        }else{
            findViewById(R.id.AudioLayout).setVisibility(View.VISIBLE);
            textViewAudioFailed.setVisibility(View.GONE);
        }
    }


    private void CheckPermssion(String permission, int requestCode) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(ReclamatonDetailActivity.this, permission) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(ReclamatonDetailActivity.this, new String[]{permission}, requestCode);
        }
    }

    private int WRITE_EXTERNAL_STORAGE_STATUS = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_STATUS) {
            if (grantResults.length >= 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ReclamatonDetailActivity.this,
                        " Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "PermissionDEnied", Toast.LENGTH_LONG).show();
                findViewById(R.id.AudioLayout).setVisibility(View.GONE);
            }
        }
    }
}
