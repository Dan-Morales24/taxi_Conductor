package com.example.taxi_conductor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Inicio_Sesion# newInstance} factory method to
 * create an instance of this fragment.
 */

public class Inicio_Sesion extends Fragment {
    private SharedPreferences preferences;
    private SharedPreferences.Editor datos_Activity2;
    EditText Usuario;
    EditText Pass;
    Button LoginI;
    private String usuario="";
    private String pass="";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public Inicio_Sesion() {
        // Required empty public constructor
    }

    //permisos de localizacion
    private void getLocalization() {
        int permiso = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLocalization();

        preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if(preferences.contains("Usuario")){
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();

        }

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inicio__sesion, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Usuario =(EditText) view.findViewById(R.id.Inicio_Correo);
        Pass =(EditText) view.findViewById(R.id.Inicio_Password);
        LoginI=(Button) view.findViewById(R.id.Inicio_Login);
        LoginI.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                usuario = Usuario.getText().toString();
                pass = Pass.getText().toString();
                if(!usuario.isEmpty() && !pass.isEmpty()){

                    progressDialog = ProgressDialog.show(getContext(), "Espera un poquito..",
                            "Estoy verificando tus datos.", true);
                    loginUser();
                }


                else{
                    Toast.makeText(getContext(), "Faltan datos por completar ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }


    public void loginUser(){

        mAuth.signInWithEmailAndPassword(usuario, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    getUserInfo();
                }

                else {
                    Toast.makeText(getContext(), "Este usuario no existe en nuestro registro" , Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }

            }
        });

    }



    private void getUserInfo(){

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Drivers").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error Obteniendo el dato", task.getException());
                    Toast.makeText(getContext(), "Firebase Error: "+task.getException() , Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
                else {
                    progressDialog.cancel();

                    if(task.getResult().getValue()!=null){
                        String Nombre = task.getResult().child("Nombre").getValue(String.class);
                        String Correo = task.getResult().child("Correo").getValue(String.class);
                        String Numero = task.getResult().child("Numero").getValue(String.class);
                        preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                        datos_Activity2 = preferences.edit();
                        datos_Activity2.putString("Usuario", Nombre);
                        datos_Activity2.putString("Correo", Correo);
                        datos_Activity2.putString("Numero", Numero);
                        datos_Activity2.commit();
                        getActivity().finish();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }

                    else{
                        Toast.makeText(getContext(), "Credenciales Invalidas" , Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}