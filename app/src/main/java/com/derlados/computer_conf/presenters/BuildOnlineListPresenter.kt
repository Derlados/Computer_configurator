package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import com.derlados.computer_conf.consts.Domain
import com.derlados.computer_conf.models.OnlineBuildModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.BuildsOnlineListView
import com.derlados.computer_conf.view_interfaces.MainView
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

class BuildOnlineListPresenter(private val mainView: MainView, private val view: BuildsOnlineListView, private val resourceProvider: ResourceProvider) {
    private var downloadJob: Job? = null
    private var isInit = false

    fun init() {
        downloadBuilds()
    }

    fun refresh() {
        downloadBuilds()
    }

    fun selectBuild(serverId: Int) {
        OnlineBuildModel.selectedBuildId = serverId
        view.openBuildOnlineView()
    }

    fun share(serverId: Int) {
        val build = OnlineBuildModel.publicBuilds.find { b -> b.serverId == serverId }
        build?.let {
            val uri = "${Domain.APP_DOMAIN}build/${serverId} - ${build.name}"
            view.copyToClipboard(uri)
        }
    }

    private fun downloadBuilds() {
        downloadJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                OnlineBuildModel.getPublicBuilds()
                if (isActive) {
                    val sortedList = OnlineBuildModel.publicBuilds.sortedByDescending { build -> build.publishDate  }
                    view.setBuildsData(ArrayList(sortedList))
                    view.disableRefreshAnim()

                    if (!isInit) {
                        view.initRefreshing()
                        isInit = true
                    }

                    checkUriChoice()
                }
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
            }
        }
    }

    private fun checkUriChoice() {
        if (OnlineBuildModel.selectedBuildId != -1) {
            val build = OnlineBuildModel.publicBuilds.find { b -> b.serverId == OnlineBuildModel.selectedBuildId }

            if (build != null) {
                selectBuild(OnlineBuildModel.selectedBuildId)

                Handler(Looper.getMainLooper()).postDelayed({
                    mainView.closeProgressLoading()
                }, 200)
            } else {
                view.showError(resourceProvider.getString(ResourceProvider.ResString.BUILD_NOT_FOUND))
            }
        }
    }

    private fun errorHandle(message: String?) {
        if (message == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.UNEXPECTED_ERROR))
        } else {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.valueOf(message)))
        }
    }
}