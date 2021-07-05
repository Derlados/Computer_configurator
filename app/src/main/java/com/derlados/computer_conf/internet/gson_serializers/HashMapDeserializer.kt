package com.derlados.computer_conf.internet.gson_serializers

import android.os.Debug
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.Throws

class HashMapDeserializer : JsonDeserializer<HashMap<String?, String?>> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HashMap<String?, String?> {
       Log.d(Log.DEBUG.toString(), json.toString())

        val jsonString = json.asJsonArray[0].toString()
        var map: HashMap<String?, String?>
        val type = object : TypeToken<HashMap<String?, String?>?>() {}.type
        map = Gson().fromJson(jsonString, type)
        return map
    }


}