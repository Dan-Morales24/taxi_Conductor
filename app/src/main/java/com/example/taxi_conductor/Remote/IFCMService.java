package com.example.taxi_conductor.Remote;

import com.example.taxi_conductor.Model.FCMResponse;
import com.example.taxi_conductor.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA2hKRA_Q:APA91bFF_dWZI1bQ5qDni0ayR_4rgPufjA-6FP-mwSLNNvSUcxsTwuzAJEPQ7b3ulBlNPPNJElT2zWzCSwgpQyLOxOYZLKvM4hQ7J534D-H0qtVdM7Gv8mCaa_9OwFpmSsENW3MLXMyI"

    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification (@Body FCMSendData body);

}
