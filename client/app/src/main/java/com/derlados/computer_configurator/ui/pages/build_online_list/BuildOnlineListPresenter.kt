package com.derlados.computer_configurator.ui.pages.build_online_list

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import com.derlados.computer_configurator.consts.Domain
import com.derlados.computer_configurator.stores.PublicBuildsStore
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.UserStore
import com.derlados.computer_configurator.ui.pages.main.MainView
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
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

    fun selectBuild(id: Int) {
        PublicBuildsStore.selectBuildById(id);
        view.openBuildOnlineView()
    }

    fun share(serverId: Int) {
        val build = PublicBuildsStore.publicBuilds.find { b -> b.id == serverId }
        build?.let {
            val uri = "${Domain.APP_DOMAIN}build/${serverId} - ${build.name}"
            view.copyToClipboard(uri)
        }
    }

    fun report(buildId: Int) {
        if (UserStore.token == null) {
            view.showError(resourceProvider.getString(ResourceProvider.ResString.ONLY_FOR_AUTHORIZED))
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            UserStore.token?.let {
                try {
                    LocalBuildsStore.reportBuild(it, buildId)
                    ensureActive()
                    view.showError(resourceProvider.getString(ResourceProvider.ResString.REPORT_WAS_SENDED))
                } catch (e: NetworkErrorException) {
                    ensureActive()
                    errorHandle(e.message)
                } catch (e: SocketTimeoutException) {
                    ensureActive()
                    view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
                }
            }
        }
    }


    private fun downloadBuilds() {
        downloadJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                PublicBuildsStore.getPublicBuilds()

                ensureActive()
                val sortedList = PublicBuildsStore.publicBuilds.sortedByDescending { build -> build.publishDate  }
                view.setBuildsData(ArrayList(sortedList))
                view.disableRefreshAnim()

                if (!isInit) {
                    view.initRefreshing()
                    isInit = true
                }

                checkUriChoice()
            } catch (e: NetworkErrorException) {
                ensureActive()
                errorHandle(e.message)
            } catch (e: SocketTimeoutException) {
                ensureActive()
                view.showError(resourceProvider.getString(ResourceProvider.ResString.NO_CONNECTION))
            }
        }
    }

    private fun checkUriChoice() {
        if (PublicBuildsStore.selectedBuildId != -1) {
            val build = PublicBuildsStore.publicBuilds.find { b -> b.id == PublicBuildsStore.selectedBuildId }

            if (build != null) {
                selectBuild(PublicBuildsStore.selectedBuildId)

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