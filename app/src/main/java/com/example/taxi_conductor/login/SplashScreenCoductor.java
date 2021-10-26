package com.example.taxi_conductor.login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.taxi_conductor.Model.DriverModel;
import com.example.taxi_conductor.home.NavigationConductorActivity;
import com.example.taxi_conductor.R;
import com.example.taxi_conductor.Utils.ConductorUtils;
import com.example.taxi_conductor.reference.Common;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashScreenCoductor extends AppCompatActivity {

    private final static int LOGIN_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    FirebaseDatabase database;
    DatabaseReference DriverInfoRef;
    private List<AuthUI.IdpConfig> providers;

  //  @BindView(R.id.progress_login_bar)
    ProgressBar progress_login_bar;

    @Override
    public void onStart() {
        super.onStart();
        delaySplashScreen();
    }


    @Override
    public void onStop() {
        if(firebaseAuth != null && listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();


    }



    private void init() {

        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();
        DriverInfoRef = database.getReference(Common.DRIVER_INFO_REFERENCE);
        firebaseAuth = FirebaseAuth.getInstance();
      /*  providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        ); */


        listener = myFirebaseAuth -> {

            FirebaseUser user = myFirebaseAuth.getCurrentUser();
            if(user != null)
            {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.d("Token final: ", token);
                                Toast.makeText(SplashScreenCoductor.this, token, Toast.LENGTH_SHORT).show();
                                ConductorUtils.updateToken(SplashScreenCoductor.this,token);

                            }
                        });

                checkUserFromFirebase();
            }
            else {

                goToLoginActivity();

            }
        };

    }



    private void checkUserFromFirebase() {

        DriverInfoRef.child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    // Toast.makeText(LoginTypeUber.this, "Usuario ya registrado: ", Toast.LENGTH_SHORT).show();
                    DriverModel driverModel = snapshot.getValue(DriverModel.class);
                    goToHomeActivity(driverModel);

                }
                else{

                    // mostraremos el layout de que estamos esperando a que se carguen los datos
                   // showRegisterLayout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void goToLoginActivity(){

        startActivity(new Intent(SplashScreenCoductor.this,LoginConductor.class));
        finish();

    }



    private void goToHomeActivity(DriverModel driverModel) {

        Common.currentRide = driverModel;
        startActivity(new Intent(SplashScreenCoductor.this, NavigationConductorActivity.class));
        finish();


    }


    private void delaySplashScreen() {

        Completable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() ->

                        firebaseAuth.addAuthStateListener(listener)

                );
        // progress_login_bar.setVisibility(View.VISIBLE);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_CODE) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){

                FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

            }
            else{

                Toast.makeText(this, "[ERROR]: "+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



}