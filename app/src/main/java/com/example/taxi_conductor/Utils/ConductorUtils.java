package com.example.taxi_conductor.Utils;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taxi_conductor.EventBus.NotifyToRiderEvent;
import com.example.taxi_conductor.Model.FCMSendData;
import com.example.taxi_conductor.Model.TokenModel;
import com.example.taxi_conductor.R;
import com.example.taxi_conductor.Remote.IFCMService;
import com.example.taxi_conductor.Remote.RetrofitFCMClient;
import com.example.taxi_conductor.reference.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.EnumValue;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ConductorUtils {


    public static void updateToken(Context context, String token){

        TokenModel tokenModel = new TokenModel(token);
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(aVoid ->{


        });

    }


    public static void sendDeclineRequest(View  view, Context context, String key) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);


        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE_USERS)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_DECLINE);
                            notificationData.put(Common.NOTI_CONTENT,"Conductor cancelo la solicitud de viaje");
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0){

                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                        }
                                            else{

                                            Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();

                                        }


                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));

                        }else{
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    public static void sendAcceptRequestToRider(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);


        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE_USERS)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_ACCEPT);
                            notificationData.put(Common.NOTI_CONTENT,"Conductor acepto el viaje");
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.TRIP_KEY,tripNumberId);


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0){

                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                        }
                                       

                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));

                        }else{
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });


    }

    public static void sendNotifyToRider( Context context,View view, String key) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);


        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE_USERS)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                            Map<String,String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE,context.getString(R.string.driver_arrived));
                            notificationData.put(Common.NOTI_CONTENT,context.getString(R.string.your_driver_arrived));
                            notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.RIDER_KEY,key);


                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0){

                                            compositeDisposable.clear();
                                            Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                        }   else
                                            EventBus.getDefault().postSticky(new NotifyToRiderEvent());


                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }));

                        }else{
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                });





    }

    public static void sendDeclineAndRemoveTripRequest(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //primero, quitamos el viaje de firebase
        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(tripNumberId)
                .removeValue()
                .addOnFailureListener(e ->{

                    Snackbar.make(view, e.getMessage(),Snackbar.LENGTH_SHORT).show();


                }).addOnSuccessListener(unused -> {

                    // SI EL VIAJE SE BORRO CORRECTAMENTE ENVIAR NOTIFICACION AL CLIENTE;
            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REFERENCE_USERS)
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                                Map<String,String> notificationData = new HashMap<>();
                                notificationData.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP);
                                notificationData.put(Common.NOTI_CONTENT,"Conductor cancelo el viaje, lamentamos los inconvenientes");
                                notificationData.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());


                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            if (fcmResponse.getSuccess() == 0){

                                                compositeDisposable.clear();
                                                Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                            }
                                            else{

                                                Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();

                                            }


                                        }, throwable -> {

                                            compositeDisposable.clear();
                                            Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                        }));

                            }else{
                                compositeDisposable.clear();
                                Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            compositeDisposable.clear();
                            Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    });


                });


    }

    public static void sendCompleteTripToRider(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        //primero, quitamos el viaje de firebase
        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(tripNumberId)
                .removeValue()
                .addOnFailureListener(e ->{

                    Snackbar.make(view, e.getMessage(),Snackbar.LENGTH_SHORT).show();


                }).addOnSuccessListener(unused -> {

            // SI EL VIAJE SE BORRO CORRECTAMENTE ENVIAR NOTIFICACION AL CLIENTE;
            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REFERENCE_USERS)
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                TokenModel tokenModel = snapshot.getValue(TokenModel.class);
                                Map<String,String> notificationData = new HashMap<>();
                                notificationData.put(Common.NOTI_TITLE,Common.RIDER_COMPLETE_TRIP);
                                notificationData.put(Common.NOTI_CONTENT,"Viaje terminado");
                                notificationData.put(Common.TRIP_KEY,tripNumberId);



                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            if (fcmResponse.getSuccess() == 0){

                                                compositeDisposable.clear();
                                                Snackbar.make(view,context.getString(R.string.complete_trip_failed),Snackbar.LENGTH_LONG).show();

                                            }
                                            else{

                                                Snackbar.make(view,context.getString(R.string.complete_trip_success),Snackbar.LENGTH_LONG).show();

                                            }


                                        }, throwable -> {

                                            compositeDisposable.clear();
                                            Snackbar.make(view,throwable.getMessage(),Snackbar.LENGTH_LONG).show();

                                        }));

                            }else{
                                compositeDisposable.clear();
                                Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            compositeDisposable.clear();
                            Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    });


        });



    }
}
