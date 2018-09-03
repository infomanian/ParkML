package com.example.mohammadrezasadeghi.parkml;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParkMLAPIInterface {

    @POST("api")
    Call<String> api(@Body Map<String, Object> body);

}
