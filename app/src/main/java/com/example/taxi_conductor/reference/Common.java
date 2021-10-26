package com.example.taxi_conductor.reference;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.taxi_conductor.Model.DriverModel;
import com.example.taxi_conductor.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String DRIVER_INFO_REFERENCE ="Drivers" ;
    public static final String TOKEN_REFERENCE = "TokenDrivers";
    public static final String TOKEN_REFERENCE_USERS ="Token";
    public static final String DRIVERS_LOCATION_REFERENCE = "DriversLocation";
    public static final String REQUEST_DRIVER_DECLINE = "Decline";
    public static final String DRIVER_KEY = "DriverKey";
    public static final String RIDER_INFO = "Riders";
    public static final String REQUEST_DRIVER_ACCEPT = "Accept";
    public static final String TRIP_KEY = "TripKey";
    public static final double MIN_RANGE_PICKUP_IN_KM =0.05 ;
    public static final int WAIT_TIME_IN_MIN = 1;
    public static final String TRIP_DESTINATION_LOCATION_REF = "TripDestinationLocation" ;
    public static final String REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP = "Viaje cancelado";
    public static final String RIDER_COMPLETE_TRIP = "DriverCompleteTrip";
    public static DriverModel currentRide;
    public static final String NOTI_TITLE ="title";
    public static final String NOTI_CONTENT ="message";
    public static final String RIDER_PICKUP_LOCATION = "PickupLocation";
    public static final String RIDER_KEY = "RiderKey";
    public static final String REQUEST_DRIVER_TITLE ="Peticion de Viaje";
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickupLocationString";
    public static final String RIDER_DESTINATION_STRING = "DestinationLocationString";
    public static final String RIDER_DESTINATION = "DestinationLocation" ;
    public static final String TRIP = "Trips";
    public static String TRIP_PICKUP_REF="TripPickupLocation";


    public static  String builderWelcomeMessage(){
        if(Common.currentRide != null){
            return  new StringBuilder("")
                    .append(Common.currentRide.getFirstName())
                    .append(" ")
                    .append(Common.currentRide.getLastName()).toString();

        }
        else
            return "";
    }


    public static void showNotification (Context context , int id, String title, String body, Intent intent){

        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID ="Taxi Driver Conductor";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Taxi Driver Conductor ",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Taxi Driver Conductor");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_baseline_directions_car_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_baseline_directions_car_24));

        if(pendingIntent != null)
        {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }



    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }


    public static String createuniqueTripNumber(long timeOffset) {

        Random random = new Random();
        Long current = System.currentTimeMillis()+timeOffset;
        Long unique = current + random.nextLong();
        if(unique < 0) unique*=(-1);

        return String.valueOf(unique) ;
    }
}
