package com.jhonkkman.app2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private String key;
    private ArrayList<SessionMensajes> sm;

    public ChatAdapter(String key, ArrayList<SessionMensajes> sm){
        this.key=key;
        this.sm=sm;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_item,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        //holder.prueba(position);
        holder.cargar_mensajes(position);
        //holder.mostrar_mensaje(position,sm);
    }

    @Override
    public int getItemCount() {
        return sm.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout chat1,chat2;
        DatabaseReference dbR;
        TextView tv_chat_v,tv_chat_l;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chat1 = (LinearLayout) itemView.findViewById(R.id.chat1);
            chat2 = (LinearLayout) itemView.findViewById(R.id.chat2);
            tv_chat_l = (TextView) itemView.findViewById(R.id.tv_chat_local);
            tv_chat_v = (TextView) itemView.findViewById(R.id.tv_chat_visita);
            dbR = FirebaseDatabase.getInstance().getReference();
        }

        //hacer ese codigo en ChatActivity
        public void cargar_mensajes(int pos){
            SessionMensajes msj = sm.get(pos);
            if(msj.getUser().equals(MainActivity.name)){
                chat1.setVisibility(View.INVISIBLE);
                chat2.setVisibility(View.VISIBLE);
                tv_chat_l.setText(msj.getMensaje());
            }else{
                chat2.setVisibility(View.INVISIBLE);
                chat1.setVisibility(View.VISIBLE);
                tv_chat_v.setText(msj.getMensaje());
            }
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
