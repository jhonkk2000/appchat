package com.jhonkkman.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btn_chatear;
    private EditText et_nombre;
    private RadioButton r_m,r_f;
    private DatabaseReference dbR;
    private ProgressDialog pd;
    public static String name;
    private TextView tv_cant_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_chatear = (Button) findViewById(R.id.btn_chatear);
        et_nombre = (EditText) findViewById(R.id.et_nombre);
        tv_cant_users = (TextView) findViewById(R.id.tv_cant_user);
        r_m = (RadioButton) findViewById(R.id.r_m);
        r_f = (RadioButton) findViewById(R.id.r_f);
        dbR = FirebaseDatabase.getInstance().getReference();
        cantUsers();
        chatear();
    }

    public void cantUsers(){
        dbR.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cant = 0;
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if(ds.child("estado").getValue().toString().equals("true")){
                            cant++;
                        }
                    }
                    tv_cant_users.setText(cant + " Usuarios buscando");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void alerta(String mensaje){
        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
        alerta.setTitle("MENSAJE");
        alerta.setMessage(mensaje);
        alerta.setPositiveButton("OK",null);
        alerta.show();
    }

    public void chatear(){
        btn_chatear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Buscando");
                pd.show();
                if (!et_nombre.getText().toString().isEmpty() && (r_m.isChecked() || r_f.isChecked())){
                    name= et_nombre.getText().toString();
                    final boolean[] validarName = {false};
                    dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                if(ds.child("nombre").getValue().toString().equals(name)){
                                    validarName[0] =true;
                                }
                            }
                            if(!validarName[0]){
                                if(r_m.isChecked()){
                                    dbR.child("users").push().setValue(new User(name,r_m.getText().toString(),true));
                                }else{
                                    dbR.child("users").push().setValue(new User(name,r_f.getText().toString(),true));
                                }
                                final int[] l = {0};
                                final String[] key1 = {""};
                                //Buscar usuario
                                dbR.child("users").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pd.setTitle("Buscando");
                                                l[0]++;
                                                if(snapshot.exists()){
                                                    final User user;
                                                    ArrayList<String> keys = new ArrayList<>();
                                                    ArrayList<User> usuariosL = new ArrayList<>();
                                                    String key1 = "";
                                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                                        if (dataSnapshot.child("nombre").getValue().toString().equals(name)){
                                                            key1 = dataSnapshot.getKey();
                                                        }
                                                        if(dataSnapshot.child("estado").getValue().toString().equals("true") && !dataSnapshot.child("nombre").getValue().toString().equals(name)){
                                                            keys.add(dataSnapshot.getKey());
                                                            usuariosL.add(dataSnapshot.getValue(User.class));
                                                        }
                                                    }
                                                    if(snapshot.child(key1).child("estado").getValue().toString().equals("true")){
                                                        Random ran = new Random();
                                                        //Toast.makeText(MainActivity.this, String.valueOf(usuariosL.size()), Toast.LENGTH_SHORT).show();
                                                        if (usuariosL.size()>0){
                                                            int num;
                                                            if (usuariosL.size()==1){
                                                                num = 0;
                                                            }else{
                                                                num = ran.nextInt(usuariosL.size()-1);
                                                            }
                                                            user = usuariosL.get(num);
                                                            final String finalKey =keys.get(num);
                                                            final String finalKey1 = key1;
                                                            //modificar estado del usuario visitante
                                                            Intent i = new Intent(MainActivity.this,ChatActivity.class);
                                                            i.putExtra("nombre",user.getNombre());
                                                            i.putExtra("sexo",user.getSexo());
                                                            i.putExtra("youKey",finalKey);
                                                            i.putExtra("codigo","verdad");
                                                            i.putExtra("myKey", finalKey1);
                                                            pd.dismiss();
                                                            startActivity(i);
                                                        }
                                                    }else{
                                                        final String finalKey2 = key1;
                                                        dbR.child("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    String nombre = "";
                                                                    for (DataSnapshot ds : snapshot.getChildren()){
                                                                        if(ds.child("users").child("user_1").getValue().toString().equals(MainActivity.name)){
                                                                            nombre = ds.child("users").child("user_2").getValue().toString();
                                                                        }else{
                                                                            if (ds.child("users").child("user_2").getValue().toString().equals(MainActivity.name)){
                                                                                nombre=ds.child("users").child("user_1").getValue().toString();
                                                                            }
                                                                        }
                                                                    }
                                                                    final String finalNombre = nombre;
                                                                    dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            String sexo2 = "";
                                                                            String youKey = "";
                                                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                                                if(ds.child("nombre").getValue().toString().equals(finalNombre)){
                                                                                    sexo2 = ds.child("sexo").getValue().toString();
                                                                                    youKey = ds.getKey();
                                                                                }
                                                                            }
                                                                            Intent i = new Intent(MainActivity.this,ChatActivity.class);
                                                                            i.putExtra("myKey", finalKey2);
                                                                            i.putExtra("youKey",youKey);
                                                                            i.putExtra("nombre", finalNombre);
                                                                            i.putExtra("codigo","false");
                                                                            i.putExtra("sexo",sexo2);
                                                                            pd.dismiss();
                                                                            startActivity(i);
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                        /*dbR.child("users").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pd.setTitle(" sec " + l[0]);
                                                l[0]++;
                                                if(snapshot.exists()){
                                                    final User user;
                                                    ArrayList<String> keys = new ArrayList<>();
                                                    ArrayList<User> usuariosL = new ArrayList<>();
                                                    String key1 = "";
                                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                                        if (dataSnapshot.child("nombre").getValue().toString().equals(name)){
                                                            key1 = dataSnapshot.getKey();
                                                        }
                                                        if(dataSnapshot.child("estado").getValue().toString().equals("true") && !dataSnapshot.child("nombre").getValue().toString().equals(name)){
                                                            keys.add(dataSnapshot.getKey());
                                                            usuariosL.add(dataSnapshot.getValue(User.class));
                                                        }
                                                    }
                                                    if(snapshot.child(key1).child("estado").getValue().toString().equals("true")){
                                                        Random ran = new Random();
                                                        //Toast.makeText(MainActivity.this, String.valueOf(usuariosL.size()), Toast.LENGTH_SHORT).show();
                                                        if (usuariosL.size()>0){
                                                            int num;
                                                            if (usuariosL.size()==1){
                                                                num = 0;
                                                            }else{
                                                                num = ran.nextInt(usuariosL.size()-1);
                                                            }
                                                            user = usuariosL.get(num);
                                                            final String finalKey =keys.get(num);
                                                            final String finalKey1 = key1;
                                                            //modificar estado del usuario visitante
                                                            finish();
                                                            Intent i = new Intent(MainActivity.this,ChatActivity.class);
                                                            i.putExtra("nombre",user.getNombre());
                                                            i.putExtra("sexo",user.getSexo());
                                                            i.putExtra("youKey",finalKey);
                                                            i.putExtra("myKey", finalKey1);
                                                            pd.dismiss();
                                                            startActivity(i);
                                                        }
                                                    }else{

                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });*/

                            }else{
                                pd.dismiss();
                                alerta("Nombre existente");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else{
                    pd.dismiss();
                    alerta("Rellena todas las casillas para continuar");
                }
            }
        });
    }
}