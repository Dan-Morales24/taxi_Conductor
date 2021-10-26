package com.example.taxi_conductor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment } subclass.
 */

public class FragmentRegistro extends Fragment {

    EditText Nombre, Numero, Correo, pass;
    Button Login;


    private String SNombre = "";
    String SNumero = "";
    String SCorreo = "";
    String SPass = "";
    String typeUser = "conductor";

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    DatabaseReference mDatabase;

    public FragmentRegistro() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Nombre = (EditText) view.findViewById(R.id.editTextNombre);
        Numero = (EditText) view.findViewById(R.id.editTextNumero);
        Correo = (EditText) view.findViewById(R.id.editTextCorreo);
        pass = (EditText) view.findViewById(R.id.editTextContrasena);
        Login = (Button) view.findViewById(R.id.Registrarse);
        Login.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                SNombre = Nombre.getText().toString();
                SNumero = Numero.getText().toString();
                SCorreo = Correo.getText().toString();
                SPass = pass.getText().toString();

                if (!SNombre.isEmpty() && !SNumero.isEmpty() && !SCorreo.isEmpty() && !SPass.isEmpty()) {



                    if (SPass.length() >= 6) {

                        registerUser();
                    } else {

                        Toast.makeText(getContext(), "La contraseña debe de tener al menos 6 caracteres: " + SNombre, Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(getContext(), "Faltan datos  " + SNombre, Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;


    }

            private void registerUser(){

            mAuth.createUserWithEmailAndPassword(SCorreo, SPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        Map<String, Object> map = new HashMap<>();
                            map.put("Nombre",SNombre);
                            map.put("Numero",SNumero);
                            map.put("Correo",SCorreo);
                            map.put("Password",SPass);
                            map.put("typeUser",typeUser);


                        String id = mAuth.getCurrentUser().getUid();
                        mDatabase.child("Drivers").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {

                                    if (task2.isSuccessful()){

                                        Toast.makeText(getContext(), "Conductor registrado en firebase" + SNombre, Toast.LENGTH_SHORT).show();


                                    }

                                    else {

                                        Toast.makeText(getContext(), "No se pudo registrar el Conductor" + SNombre, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                    }

                        else {

                        Toast.makeText(getContext(), "No se pudo registrar el Conductor" + SNombre, Toast.LENGTH_SHORT).show();

                    }


                }
            });


            }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);







        TextView btnAtrasRegistro = view.findViewById(R.id.btnAtrasRegistro);

    }
}
