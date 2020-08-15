package com.jhonkkman.app2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv_chat;
    private ChatAdapter adapter;
    private DatabaseReference dbR;
    private ImageView iv_avatar;
    private TextView tv_nombre,tv_chat_v,tv_chat_l;
    private EditText et_mensaje;
    private ImageButton btn_enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rv_chat = (RecyclerView) findViewById(R.id.rv_chat);
        iv_avatar = (ImageView) findViewById(R.id.avatar);
        tv_nombre = (TextView) findViewById(R.id.tv_chat_user);
        tv_chat_l = (TextView) findViewById(R.id.tv_chat_local);
        tv_chat_v = (TextView) findViewById(R.id.tv_chat_visita);
        et_mensaje = (EditText) findViewById(R.id.et_mensaje);
        btn_enviar = (ImageButton) findViewById(R.id.btn_enviar);
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        rv_chat.setAdapter(adapter);
        dbR = FirebaseDatabase.getInstance().getReference();
        cargar_datos();
        enviar_mensaje();
    }

    public void cargar_datos(){
        tv_nombre.setText(getIntent().getStringExtra("nombre"));
        String sexo = getIntent().getStringExtra("sexo");
        if(sexo.equals("Masculino")){
            iv_avatar.setImageDrawable(getDrawable(R.drawable.man));
        }else{
            iv_avatar.setImageDrawable(getDrawable(R.drawable.woman));
        }
    }

    public void enviar_mensaje(){
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_mensaje.getText().toString().isEmpty()){
                    dbR.child("sessions").child(getIntent().getStringExtra("keySession")).child("mensajes").push().setValue(new SessionMensajes(et_mensaje.getText().toString(),MainActivity.name));
                }
            }
        });
    }
}