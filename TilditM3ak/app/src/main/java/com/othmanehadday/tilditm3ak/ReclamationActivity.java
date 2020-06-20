package com.othmanehadday.tilditm3ak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReclamationActivity extends AppCompatActivity {
    private EditText editTextFullName, editTextPhone, editTextAddress, editTextMessage;
    private Button buttonReclamation, buttonMediaPlay, buttonMediaStop;
    private ImageButton backButton, imageButtonRecord;
    private SeekBar seekBarMediaPlay;

    private Chronometer chronoRecordAudio;

    private String fullName, address, phone, violenceCat;

    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private MediaRecorder recorder = null;
    private String fileName = null;
    private static final String LOG_TAG = "Record_log";

    private MediaPlayer mediaPlayer;


    private static final int RECORD_AUDIO_status = 1;

    Handler handler;
    Runnable runnable;

    private int id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamation);

        if (!isOnline()) {
            Toast.makeText(this, "check your internet connection", Toast.LENGTH_LONG).show();
            findViewById(R.id.connectedLayout).setVisibility(View.GONE);
            findViewById(R.id.notConnected).setVisibility(View.VISIBLE);

        }
        CheckPermssion(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_status);

        handler = new Handler();

        //firebase Initialisation
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        requestQueue = Volley.newRequestQueue(this);


        id = new Random().nextInt(10000);
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/"+id+".3gp";


        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextMessage = findViewById(R.id.editTextmessage);
        buttonReclamation = findViewById(R.id.buttonreclamationFinal);
        backButton = findViewById(R.id.backButtonToMainActivity);
        imageButtonRecord = findViewById(R.id.imageButtonRecord);
        chronoRecordAudio = findViewById(R.id.chronoRecordAudio);
        buttonMediaPlay = findViewById(R.id.buttonMediaplay);
        buttonMediaStop = findViewById(R.id.buttonMediastop);
        seekBarMediaPlay = findViewById(R.id.seekBarMediaAudio);
        findViewById(R.id.playingAudioLayout).setVisibility(View.GONE);

        progressDialog = new ProgressDialog(ReclamationActivity.this);
        progressDialog.setTitle(getString(R.string.reclame));
        progressDialog.setMessage(getString(R.string.savingRecla));

        alertDialog = new AlertDialog.Builder(this);

        buttonReclamation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean b = verfierEditText();
                if (b == true) {
                    return;
                } else {
                    if (fullName == "" || phone == "" || address == "") {
                        return;
                    } else {
                        progressDialog.show();

                        ReclamationModel reclamationModel = new ReclamationModel();
                        reclamationModel.setFullName(fullName);
                        reclamationModel.setPhone(phone);
                        reclamationModel.setAddress(address);
                        reclamationModel.setViolenceCategorie(violenceCat);
                        reclamationModel.setCurrentDate(new Date());

                        storeData(reclamationModel);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageButtonRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == event.ACTION_DOWN) {
                    startRecording();
                    chronoRecordAudio.setBase(SystemClock.elapsedRealtime());
                    chronoRecordAudio.start();
                    findViewById(R.id.playingAudioLayout).setVisibility(View.VISIBLE);
                } else if (event.getAction() == event.ACTION_UP) {
                    stopRecording();
                    findViewById(R.id.playingAudioLayout).setVisibility(View.VISIBLE);
                    chronoRecordAudio.stop();
                } else {

                }
                return false;
            }
        });

        buttonMediaPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer == null) {
                        Uri uri = Uri.parse(fileName);
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    }
                    mediaPlayer.start();
                    buttonMediaPlay.setVisibility(View.GONE);
                    buttonMediaStop.setVisibility(View.VISIBLE);
                    initializeSeekBar();
                } catch (NullPointerException e) {
                    buttonMediaPlay.setVisibility(View.VISIBLE);
                    buttonMediaStop.setVisibility(View.GONE);
                    findViewById(R.id.playingAudioLayout).setVisibility(View.GONE);
                }

            }
        });

        buttonMediaStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                buttonMediaPlay.setVisibility(View.VISIBLE);
                buttonMediaStop.setVisibility(View.GONE);
            }
        });


        seekBarMediaPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    private void initializeSeekBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        try {
                            final double current = mediaPlayer.getCurrentPosition();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBarMediaPlay.setMax(mediaPlayer.getDuration());
                                    seekBarMediaPlay.setProgress((int) current);
                                    buttonMediaPlay.setVisibility(View.GONE);
                                    buttonMediaStop.setVisibility(View.VISIBLE);
                                }
                            });
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (!mediaPlayer.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buttonMediaPlay.setVisibility(View.VISIBLE);
                                buttonMediaStop.setVisibility(View.GONE);
                                seekBarMediaPlay.setProgress(0);
                                Thread.interrupted();
                            }
                        });
                    }
                }
            }
        }).start();

    }

    private Boolean verfierEditText() {
        boolean b = false;
        fullName = editTextFullName.getText().toString();
        address = editTextAddress.getText().toString();
        phone = editTextPhone.getText().toString();
        violenceCat = editTextMessage.getText().toString();

        if (fullName == null || fullName.trim().isEmpty()) {
            editTextFullName.setError(getString(R.string.name_obligation));
            b = true;
        }
        if (address == null || address.trim().isEmpty()) {
            editTextAddress.setError(getString(R.string.address_obligation));
            b = true;
        }
        if (phone == null || phone.trim().isEmpty()
                || phone.trim().length() != 10) {
            editTextPhone.setError(getString(R.string.number_invalide));
            b = true;
        } else {
            char[] phoneChars = phone.toCharArray();
            if (phoneChars[0] != '0' && phone.trim().length() == 10) {
                editTextPhone.setError(getString(R.string.number_invalide));
                b = true;
            }
        }

        return b;
    }

    private void storeData(final ReclamationModel reclamationModel) {
        final String key = mDatabase.child("Reclamations").push().getKey();
        reclamationModel.setId(key);
        mDatabase.child("Reclamations").child(key)
                .setValue(reclamationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotification(reclamationModel.getId(), reclamationModel.getFullName());
                uploadAudioRecorded(key, reclamationModel.getFullName());

                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_LONG).show();

                alertDialog.setTitle(getString(R.string.success));
                alertDialog.setMessage(getString(R.string.success_reclamation));
                alertDialog.setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialog.setNeutralButton(R.string.go_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();

                editTextFullName.setText("");
                editTextPhone.setText("");
                editTextAddress.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), R.string.Failure, Toast.LENGTH_LONG).show();

                alertDialog.setTitle(R.string.Failure);
                alertDialog.setMessage(R.string.failure_reclamation);
                alertDialog.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.setNeutralButton(R.string.go_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void uploadAudioRecorded(String id, String name) {
            try {
                StorageReference filePath = mStorageRef.child("Audio").child(id + name + ".3gp");

                    Uri uri = Uri.fromFile(new File(fileName));
                if(new File(fileName).exists()){
                    filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "audio saved", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "audio not saved", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "audio not saved", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void startRecording() {


        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (RuntimeException stopException) {
            // handle cleanup here
            findViewById(R.id.playingAudioLayout).setVisibility(View.GONE);
        }
    }


    private void CheckPermssion(String permission, int requestCode) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(ReclamationActivity.this, permission) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(ReclamationActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_AUDIO_status) {
            if (grantResults.length >= 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(ReclamationActivity.this,
                        "Audio Record Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "PermissionDEnied", Toast.LENGTH_LONG).show();
                findViewById(R.id.imageButtonRecord).setVisibility(View.GONE);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    private RequestQueue requestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    private void sendNotification(String t, String b) {
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", "/topics/" + "news");
            JSONObject notificatonObj = new JSONObject();
            notificatonObj.put("title", "تبليغ جديد   ");
            notificatonObj.put("body", "الاسم : " + b);
            mainObj.put("notification", notificatonObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAgep2x_g:APA91bG_dr80Zv9WfUIkwpwu0Su1BbAL-DkpHaqn1y-zeEO-i3vXzAwueiz8z5eLBfPeuh_7bxMM1yAGOLvt1rLDPCd3Vce0CWb3Vpfl8d-kUr8YivSUma4D2249PYd4rlW5R3JOh5RF");

                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}