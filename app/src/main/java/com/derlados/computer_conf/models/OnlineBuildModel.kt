package com.derlados.computer_conf.models

import com.derlados.computer_conf.internet.BuildsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OnlineBuildModel {
    private val retrofit: Retrofit
    private val api: BuildsApi
    var publicBuilds = ArrayList<Build>()
    var selectedBuild: Build? = null // Выбранная сборка, должна являться клоном из списка

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BuildsApi.BASE_URL_IGNORED)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(BuildsApi::class.java)
    }

    ////////////////////////////////////API////////////////////////////
    /**TESTED*/
    fun getPublicBuilds() {
        val call = api.getPublicBuilds()
        call.enqueue(object : Callback<ArrayList<Build>> {
            override fun onResponse(
                    call: Call<ArrayList<Build>>,
                    response: Response<ArrayList<Build>>
            ) {
                val builds = response.body()
                if (builds != null && response.code() == 200) {
                    publicBuilds = builds
                } else {
                    TODO("Not yet implemented")
                }
            }

            override fun onFailure(call: Call<ArrayList<Build>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun selectBuild(serverId: Int) {
        selectedBuild = publicBuilds.find { build -> build.serverId == serverId }
    }
}