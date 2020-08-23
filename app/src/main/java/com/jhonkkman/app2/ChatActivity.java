package com.jhonkkman.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv_chat;
    private ChatAdapter adapter;
    private DatabaseReference dbR;
    private ImageView iv_avatar;
    private TextView tv_nombre;
    private EditText et_mensaje;
    private Button btn_salir;
    private ImageButton btn_enviar;
    private  String keySession;
    private int valor_cerrar=0;
    private ArrayList<SessionMensajes> sm = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rv_chat = (RecyclerView) findViewById(R.id.rv_chat);
        iv_avatar = (ImageView) findViewById(R.id.avatar);
        tv_nombre = (TextView) findViewById(R.id.tv_chat_user);
        et_mensaje = (EditText) findViewById(R.id.et_mensaje);
        btn_enviar = (ImageButton) findViewById(R.id.btn_enviar);
        btn_salir = (Button) findViewById(R.id.btn_salir);
        dbR = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(this, getIntent().getStringExtra("codigo"), Toast.LENGTH_SHORT).show();
        cargar_session();
        cargar_datos();
        enviar_mensaje();
        contador();
        cargar_chat();
        btn_salir();
        close_for_btn();
    }

    public void cargar_session(){
        if(getIntent().getStringExtra("codigo").toString().equals("verdad")){
            dbR.child("users").child(getIntent().getStringExtra("youKey")).child("estado").setValue(false);
            dbR.child("users").child(getIntent().getStringExtra("myKey")).child("estado").setValue(false);
            dbR.child("sessions").push().child("users").setValue(new SessionUsers(MainActivity.name,getIntent().getStringExtra("nombre")));
        }
        dbR.child("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if ((ds.child("users").child("user_1").getValue().toString().equals(MainActivity.name) && ds.child("users").child("user_2").getValue().toString().equals(getIntent().getStringExtra("nombre")))
                        ||(ds.child("users").child("user_1").getValue().toString().equals(getIntent().getStringExtra("nombre")) && ds.child("users").child("user_2").getValue().toString().equals(MainActivity.name))){
                            keySession = ds.getKey();
                        }
                    }
                    Toast.makeText(ChatActivity.this, keySession, Toast.LENGTH_SHORT).show();
                    Log.d("Mensajeeeeee!!!!",keySession);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void close_for_btn(){
            dbR.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (valor_cerrar==0) {
                        valor_cerrar=3;
                        if(!snapshot.child(getIntent().getStringExtra("youKey")).exists()){
                            final ProgressDialog pd = new ProgressDialog(ChatActivity.this);
                            pd.setTitle(getIntent().getStringExtra("nombre") + " ha finalizado la session");
                            pd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                    finish();
                                }
                            },3000);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    public void btn_salir(){
        if (valor_cerrar==0){
            btn_salir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    valor_cerrar=1;
                    dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    if (ds.child("nombre").getValue().toString().equals(MainActivity.name)){
                                        ds.getRef().removeValue();
                                    }
                                    if (ds.child("nombre").getValue().toString().equals(getIntent().getStringExtra("nombre"))){
                                        ds.getRef().removeValue();
                                    }
                                }
                                dbR.child("sessions").child(keySession).getRef().removeValue();
                                final ProgressDialog pd = new ProgressDialog(ChatActivity.this);
                                pd.setTitle("Cerrando Session");
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        finish();
                                    }
                                },3000);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
    }

    public void contador(){
        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long l) {
                btn_salir.setText("SALIR " + l/1000);
            }

            @Override
            public void onFinish() {
                btn_salir.setText("SALIR");
                if (valor_cerrar==0){
                    valor_cerrar=2;
                    dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    if (ds.child("nombre").getValue().toString().equals(MainActivity.name)){
                                        ds.getRef().removeValue();
                                    }
                                }
                                dbR.child("sessions").child(keySession).getRef().removeValue();
                                final ProgressDialog pd = new ProgressDialog(ChatActivity.this);
                                pd.setTitle("Cerrando Session");
                                pd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        finish();
                                    }
                                },3000);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        }.start();
    }

    public void cargar_chat(){
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        dbR.child("sessions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sm.clear();
                    for(DataSnapshot ds : snapshot.child(keySession).child("mensajes").getChildren()){
                        sm.add(ds.getValue(SessionMensajes.class));
                    }
                    rv_chat.smoothScrollToPosition(sm.size());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new ChatAdapter(getIntent().getStringExtra("keySession"),sm);
        rv_chat.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                    dbR.child("sessions").child(keySession).child("mensajes").push().setValue(new SessionMensajes(et_mensaje.getText().toString(),MainActivity.name));
                    et_mensaje.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Presione el boton SALIR para cerrar la sessión", Toast.LENGTH_SHORT).show();
    }
}