package com.example.taxi_conductor.cloudMessage;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taxi_conductor.EventBus.DriverRequestRecived;
import com.example.taxi_conductor.Utils.ConductorUtils;
import com.example.taxi_conductor.reference.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            ConductorUtils.updateToken(this,s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null)
        {

            if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE))
            {
                DriverRequestRecived driverRequestRecived = new DriverRequestRecived();
                driverRequestRecived.setKey(dataRecv.get(Common.RIDER_KEY));
                driverRequestRecived.setPickupLocation(dataRecv.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestRecived.setPickupLocationString(dataRecv.get(Common.RIDER_PICKUP_LOCATION_STRING));
                driverRequestRecived.setDestinationLocation(dataRecv.get(Common.RIDER_DESTINATION));
                driverRequestRecived.setDestinationLocationString(dataRecv.get(Common.RIDER_DESTINATION_STRING));


                EventBus.getDefault().postSticky(driverRequestRecived);
            }

            else {
                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        null);
            }
        }
    }


}
