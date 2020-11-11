package com.derlados.computerconf.Internet.GsonSerializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class HashMapDeserializer implements JsonDeserializer<HashMap<String, String>> {
    @Override
    public HashMap<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String jsonString = json.getAsJsonArray().get(0).toString();
        HashMap<String, String> map = new HashMap<>();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        map = new Gson().fromJson(jsonString, type);

        return map;
    }
}