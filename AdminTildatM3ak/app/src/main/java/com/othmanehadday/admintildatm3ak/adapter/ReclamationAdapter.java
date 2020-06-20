package com.othmanehadday.admintildatm3ak.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.othmanehadday.admintildatm3ak.R;
import com.othmanehadday.admintildatm3ak.ReclamatonDetailActivity;
import com.othmanehadday.admintildatm3ak.model.ReclamationModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReclamationAdapter extends RecyclerView.Adapter<ReclamationAdapter.ViewHolder> {

    private List<ReclamationModel> listRecl;
    private Context context;
    private DatabaseReference mDatabase;

    public ReclamationAdapter(List<ReclamationModel> list, Context context) {
        this.listRecl = list;
        this.context = context;

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listView = layoutInflater.inflate(R.layout.reclamation_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ReclamationModel reclamationModel = listRecl.get(position);

        if (reclamationModel.isSeen()) {
            holder.imageViewSeen.setColorFilter(context.getResources().getColor(R.color.colorAccent));
        }else{
            holder.imageViewSeen.setColorFilter(context.getResources().getColor(R.color.colorDarkerGray));
        }

        holder.textViewphone.setText("الهاتف :" + reclamationModel.getPhone());

        holder.textViewName.setText(" الاسم :" + reclamationModel.getFullName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy hh:mm aa");
        String currentDate = dateFormat.format(reclamationModel.getCurrentDate());
        holder.textViewDate.setText(currentDate);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reclamationModel.isSeen()) {
                    reclamationModel.setSeen(true);
                    mDatabase.child("Reclamations").child(reclamationModel.getId())
                            .setValue(reclamationModel);
                }
                Intent intent = new Intent(context, ReclamatonDetailActivity.class);
                intent.putExtra("reclamation", reclamationModel);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listRecl.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewphone, textViewName, textViewDate;
        private ImageView imageViewSeen;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewphone = itemView.findViewById(R.id.textViewPhone);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewTime);
            imageViewSeen = itemView.findViewById(R.id.imageViewseen);
        }

    }

}
