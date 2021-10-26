package com.example.taxi_conductor.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    public static String getAdressFromLocation(Context context, Location location){

        StringBuilder result = new StringBuilder();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addressList != null && addressList.size() > 0){

                if (addressList.get(0).getLocality() != null && !TextUtils.isEmpty(addressList.get(0).getLocality())){

                    //if adress have city field
                    result.append(addressList.get(0).getLocality());
                }
                    else if (addressList.get(0).getSubAdminArea() != null && !TextUtils.isEmpty(addressList.get(0).getSubAdminArea())){

                    //if adress have city field
                    result.append(addressList.get(0).getSubAdminArea());
                }

                else if (addressList.get(0).getAdminArea() != null && !TextUtils.isEmpty(addressList.get(0).getAdminArea())){

                    //if adress have city field
                    result.append(addressList.get(0).getAdminArea());
                }


                    else{

                        //Si no encuentra el resultado de ninguno de los anteriores...
                         result.append(addressList.get(0).getPostalCode());
                }

                    result.append("_").append(addressList.get(0).getPostalCode());

            }
            return result.toString();
        }catch (IOException e){

        return result.toString();
        }
    }

}
