package com.derlados.computerconf.Internet;

import com.derlados.computerconf.Objects.Good;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RequestAPI {

    @Headers({"Accept: application / json"})
    @GET("goods")
    Call<Good[]> getGoodsPage(@Query("typeGood") String type, @Query("page") int page);

    @GET("goods/fullData")
    Call<List<Good>> getGoodFullData(@Query("urlFullData") String url);

    @GET
    Call<ResponseBody> getImage(@Url String url);

    @GET("goods/maxPages")
    Call<Integer> getMaxPages(@Query("typeGood") String type);
}
