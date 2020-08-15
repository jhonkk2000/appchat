package com.jhonkkman.app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
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

    public void chatear(){
        btn_chatear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Buscando");
                pd.show();
                if (!et_nombre.getText().toString().isEmpty() && (r_m.isChecked() || r_f.isChecked())){
                    name= et_nombre.getText().toString();
                    dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                boolean validarName = false;
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    if(ds.child("nombre").getValue().toString().equals(name)){
                                        validarName=true;
                                    }
                                }
                                if(!validarName){
                                    if(r_m.isChecked()){
                                        dbR.child("users").push().setValue(new User(name,r_m.getText().toString(),true));
                                    }else{
                                        dbR.child("users").push().setValue(new User(name,r_f.getText().toString(),true));
                                    }
                                    //Buscar usuario
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                                        Random ran = new Random();
                                                        Toast.makeText(MainActivity.this, String.valueOf(usuariosL.size()), Toast.LENGTH_SHORT).show();
                                                        int num = ran.nextInt(usuariosL.size()-1);
                                                        user = usuariosL.get(num);
                                                        String finalKey =keys.get(num);
                                                        dbR.child("users").child(finalKey).child("estado").setValue(false);
                                                        dbR.child("users").child(key1).child("estado").setValue(false);
                                                        dbR.child("sessions").push().child("users").setValue(new SessionUsers(name,user.getNombre()));
                                                        dbR.child("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    String keySession = "";
                                                                    for (DataSnapshot ds : snapshot.getChildren()){
                                                                        if (ds.child("users").child("user_1").getValue().toString().equals(name) && ds.child("users").child("user_2").getValue().toString().equals(user.getNombre())){
                                                                            keySession = ds.getKey();
                                                                        }
                                                                    }
                                                                    pd.dismiss();
                                                                    Intent i = new Intent(MainActivity.this,ChatActivity.class);
                                                                    i.putExtra("nombre",user.getNombre());
                                                                    i.putExtra("sexo",user.getSexo());
                                                                    i.putExtra("keySession", keySession);
                                                                    startActivity(i);
                                                                }
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
                                    },5000);
                                }else{
                                    pd.dismiss();
                                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                                    alerta.setTitle("MENSAJE");
                                    alerta.setMessage("Nombre existente");
                                    alerta.setPositiveButton("OK",null);
                                    alerta.show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }else{
                    pd.dismiss();
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("MENSAJE");
                    alerta.setMessage("Rellena todas las casillas para continuar");
                    alerta.setPositiveButton("OK",null);
                    alerta.show();
                }
            }
        });
    }
}