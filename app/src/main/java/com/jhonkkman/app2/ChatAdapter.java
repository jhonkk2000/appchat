package com.jhonkkman.app2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {




    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_item,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        holder.prueba(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout chat1,chat2;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chat1 = (LinearLayout) itemView.findViewById(R.id.chat1);
            chat2 = (LinearLayout) itemView.findViewById(R.id.chat2);
        }



        public void prueba(int pos){
            if(pos==0){
                chat2.setVisibility(View.INVISIBLE);
            }else{
                chat2.setVisibility(View.VISIBLE);
                chat1.setVisibility(View.INVISIBLE);
            }
        }
    }
}
