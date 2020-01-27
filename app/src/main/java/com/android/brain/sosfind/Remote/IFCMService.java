package com.android.brain.sosfind.Remote;

import com.android.brain.sosfind.Models.FSMReponse;
import com.android.brain.sosfind.Models.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Brain on 13/04/2019.
 */

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAu6Dnm4s:APA91bGp7OlETWZJTpCOI4a6BScCzoBeb_kxECUCibn-1z8_mtrDmCpsJzc9SMEqfy5gEoTkiBvzp4i3ZFX66-EFqC54ZANlOV2Wb2F7ZWD9YYVgwlycbILMENwSwWK2Fs5pcwHACQW-"
    })
    @POST("fcm/send")
    Call<FSMReponse> sendMessage(@Body Sender body);
}
