package com.union.unionapp.notifications;


import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.Callback;
import retrofit2.Call;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAANM7cmo8:APA91bHlO-mW9W0_ft6UajNdSshU7txa9xYbDzzU4GPZUCb32l-DP5ewv1ue-WeLxcG_D2ECWtSkaDtpSsq4AxHlwg3qB52IMIiSgpxvnAYEpjIR-tW5ObZ1oXX-R0X5pX5SMoBRlYda"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);


}
