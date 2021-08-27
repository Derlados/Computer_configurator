package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.models.OnlineBuildModel
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.view_interfaces.BuildsOnlineListView
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

class BuildOnlineListPresenter(private val view: BuildsOnlineListView, private val resourceProvider: ResourceProvider) {
    private var downloadJob: Job? = null
    private var isInit = false

    fun init() {
        downloadBuilds()
    }

    fun refresh() {
        downloadBuilds()
    }

    fun selectBuild(serverId: Int) {
        OnlineBuildModel.selectBuild(serverId)
        view.openBuildOnlineView()
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
                }
            } catch (e: NetworkErrorException) {
                if (isActive) {
                    errorHandle(e.message)
                }
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