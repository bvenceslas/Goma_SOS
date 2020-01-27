package com.android.brain.sosfind.Remote;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Brain on 13/04/2019.
 */

public interface IGoogleAPI {

    @GET
    Call<String> getPath(@Url String url);
}
