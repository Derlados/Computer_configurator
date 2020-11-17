package com.derlados.computerconf.Internet;

import android.graphics.Bitmap;

import com.derlados.computerconf.Objects.Good;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    @GET("goods")
    Call<ArrayList<Good>> getGoodsPage(@Query("typeGood") String type, @Query("page") int page, @Query("search") String search);

    @GET("goods/fullData")
    Call<ArrayList<Good.dataBlock>> getGoodFullData(@Query("urlFullData") String url);

    @GET("goods/maxPages")
    Call<Integer> getMaxPages(@Query("typeGood") String type, @Query("search") String search);
}
