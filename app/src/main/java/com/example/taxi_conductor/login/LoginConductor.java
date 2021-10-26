package com.example.taxi_conductor.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxi_conductor.Model.DriverModel;
import com.example.taxi_conductor.R;
import com.example.taxi_conductor.home.NavigationConductorActivity;
import com.example.taxi_conductor.reference.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginConductor extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    FirebaseDatabase database;
    DatabaseReference DriverInfoRef;
    private ProgressDialog progressDialog;
    private EditText Usuario;
    private EditText Password;
    private Button LoginI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_conductor);


        firebaseAuth = FirebaseAuth.getInstance();
        DriverInfoRef = FirebaseDatabase.getInstance().getReference();
        Usuario =(EditText) findViewById(R.id.Inicio_Correo);
        Password =(EditText) findViewById(R.id.Inicio_Password);
        LoginI=(Button) findViewById(R.id.Inicio_Login);
        LoginI.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String usuario = Usuario.getText().toString();
                String pass = Password.getText().toString();
                if(!usuario.isEmpty() && !pass.isEmpty()){

                    progressDialog = ProgressDialog.show(LoginConductor.this, "Espera un poquito..",
                            "Estoy verificando tus datos.", true);
                    loginUser(usuario,pass);

                }


                else{
                    Toast.makeText(LoginConductor.this, "Faltan datos por completar ", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }


    public void loginUser(String usuario, String pass){

       database = FirebaseDatabase.getInstance();
       DriverInfoRef = database.getReference(Common.DRIVER_INFO_REFERENCE);
       firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(usuario, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    checkUserFromFirebase();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                  //  updateUI(user);
                }

                else {
                    Toast.makeText(LoginConductor.this, "Usuario no registrado para ser conductor" , Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }

            }
        });

    }

    private void checkUserFromFirebase() {

        DriverInfoRef.child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    // Toast.makeText(LoginTypeUber.this, "Usuario ya registrado: ", Toast.LENGTH_SHORT).show();
                  //  DriverModel driverModel = snapshot.getValue(DriverModel.class);
                        goToHomeActivity();

                }
                else{

                    // registrando datos del conductor
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void goToHomeActivity() {

       // Common.currentRide = driverModel;
        Toast.makeText(LoginConductor.this, "Usuario registrado" , Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
        startActivity(new Intent(LoginConductor.this, NavigationConductorActivity.class));
        finish();

    }


}