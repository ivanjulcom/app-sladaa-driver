package com.sladaa.driver.retrofit;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {


    @POST(APIClient.APPEND_URL + "dboy_login.php")
    Call<JsonObject> getLogin(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "dboy_appstatus.php")
    Call<JsonObject> getStatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "total_order_report.php")
    Call<JsonObject> orderStatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "pending_order.php")
    Call<JsonObject> getOlist(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "pkg_order.php")
    Call<JsonObject> pkgOrder(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "complete_order.php")
    Call<JsonObject> getComplete(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "noti.php")
    Call<JsonObject> getNoti(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "order_status_change.php")
    Call<JsonObject> getOstatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "pkg_change.php")
    Call<JsonObject> getPKGOstatus(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "up_profile.php")
    Call<JsonObject> updateProfile(@Body JsonObject object);

}
