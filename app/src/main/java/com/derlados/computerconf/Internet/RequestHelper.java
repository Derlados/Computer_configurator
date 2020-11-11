package com.derlados.computerconf.Internet;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    public static String MAIN_URL = "http://192.168.1.3/"; // Базовый URL сервера
    private static String TAG_LOG_ERROR = "Response error"; // Тег для логов с ошибками с работой сервера
    private static RequestQueue requestQueue = null; // Очередь запросов

    // Типы запросов
    public enum TypeRequest {
        STRING,
        IMAGE
    }

    /* Интерфейс колбека для работы с DBHelper
    * Методы:
    * T - обобщенный параметр, поскольку ответом с сервера могут быть разные данные
    * call(T) - метод должен вызываться в случае если запрос был успешно обработан сервером. В метод передается строка ответа с сервера.
    * fail(String) - метод должен вызываться в случае если сервер не смог обработать запрос с сервера. В метод передается строка с сообщением об ошибке
     * */
    public interface CallBack<T> {
        void call(T response);
        void fail(String message);
    }

    /* Запросы типа GET
    * Параметры:
    * appContext - контекст приложения
    * apiUrl - вторая часть запроса, соответствующая API
    * typeRequest - тип запроса который необходимо выполнить
    * callBack - интерфейс колбека класса DBHelper
    * T - обобщение для обратного вызова (String, Bitmap ...)
    * */
    @SuppressWarnings("unchecked")
    public static<T> void getRequest(Context appContext, String apiUrl, TypeRequest typeRequest, final CallBack<T> callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);
        String url = apiUrl;

        switch (typeRequest) {
            case STRING:
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callBack.call((T) response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_LOG_ERROR, error.toString());
                        callBack.fail(error.toString());
                    }
                });
                requestQueue.add(stringRequest);
                break;
            case IMAGE:
                ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        callBack.call((T) response);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_LOG_ERROR, error.toString());
                        callBack.fail(error.toString());
                    }
                });
                requestQueue.add(imageRequest);
                break;

        }

        //TODO
        //request.setRetryPolicy(new DefaultRetryPolicy(200,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /* Запросы типа POST
     * Параметры:
     * appContext - контекст приложения
     * apiUrl - вторая часть запроса, соответствующая API
     * callBack - интерфейс колбека класса DBHelper
     * params - параметры POST запроса
     * */
    public static void postRequest(Context appContext, String apiUrl, final Map<String, String> params, final CallBack<String> callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);
        String url = RequestHelper.MAIN_URL + apiUrl;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.call(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG_LOG_ERROR, error.toString());
                callBack.fail(error.toString());
            }
        })  {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        requestQueue.add(request);
    }

    /* Запросы типа UPDATE
     * Параметры:
     * appContext - контекст приложения
     * apiUrl - вторая часть запроса, соответствующая API
     * callBack - интерфейс колбека класса DBHelper
     * params - параметры UPDATE запроса
     * */
    public static void putRequest(Context appContext, String apiUrl, final Map<String, String> params, final  CallBack<String> callBack) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext);

        String url = RequestHelper.MAIN_URL + apiUrl;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        callBack.call(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_LOG_ERROR, error.toString());
                        callBack.fail(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
                //or try with this:
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        requestQueue.add(putRequest);
    }
}
