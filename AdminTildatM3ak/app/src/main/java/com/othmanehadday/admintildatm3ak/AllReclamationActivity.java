package com.othmanehadday.admintildatm3ak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.othmanehadday.admintildatm3ak.adapter.ReclamationAdapter;
import com.othmanehadday.admintildatm3ak.model.ReclamationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllReclamationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReclamationAdapter reclamationAdapter;
    private List<ReclamationModel> listRec=new ArrayList<ReclamationModel>();

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reclamation);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reclamations");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRec.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ReclamationModel reclamationModel = snapshot.getValue(ReclamationModel.class);
                        listRec.add(reclamationModel);
                    }
                    Collections.reverse(listRec);
                    reclamationAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.progressbar).setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        recyclerView = findViewById(R.id.recyclerViewAllRec);


        reclamationAdapter = new ReclamationAdapter(listRec,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reclamationAdapter);



    }
}
