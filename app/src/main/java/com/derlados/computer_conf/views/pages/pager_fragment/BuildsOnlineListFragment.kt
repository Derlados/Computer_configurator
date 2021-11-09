package com.derlados.computer_conf.views.pages.pager_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.models.entities.BuildData
import com.derlados.computer_conf.presenters.BuildOnlineListPresenter
import com.derlados.computer_conf.view_interfaces.BuildsOnlineListView
import com.derlados.computer_conf.views.pages.BuildOnlineViewFragment
import com.derlados.computer_conf.views.pages.OnFragmentInteractionListener
import com.derlados.computer_conf.views.adapters.BuildOnlineRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_build_list.view.*

class BuildsOnlineListFragment : PageFragment(), BuildsOnlineListView {
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var rvBuildRecycler: RecyclerView // Основной контейнер для бланков сборок
    private lateinit var presenter: BuildOnlineListPresenter
    private lateinit var srlRefresh: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_build_list, container, false)
        currentFragment.fragment_build_list_bt_add.visibility = View.GONE

        srlRefresh = currentFragment.fragment_build_list_srl_refresh
        srlRefresh.isEnabled = false

        rvBuildRecycler = currentFragment.fragment_build_list_rv
        presenter = BuildOnlineListPresenter(this, App.app.resourceProvider)
        presenter.init()

        return currentFragment
    }

    override fun initRefreshing() {
        srlRefresh.isEnabled = true
        srlRefresh.setOnRefreshListener { presenter.refresh() }
    }

    override fun disableRefreshAnim() {
        currentFragment.fragment_build_list_srl_refresh.isRefreshing = false
    }

    override fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>) {
        rvBuildRecycler.layoutManager = LinearLayoutManager(context)
        rvBuildRecycler.adapter = BuildOnlineRecyclerAdapter(buildsData, ::selectBuild)
    }

    private fun selectBuild(id: Int) {
        presenter.selectBuild(id)
    }

    override fun openBuildOnlineView() {
        frListener.nextFragment(this, BuildOnlineViewFragment(), BackStackTag.BUILD_VIEW)
    }

    override fun updateRangeBuildList(size: Int) {
        rvBuildRecycler.adapter?.notifyItemRangeChanged(0, size)
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}