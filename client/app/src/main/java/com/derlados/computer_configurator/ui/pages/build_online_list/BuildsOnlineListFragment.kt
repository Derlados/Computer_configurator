package com.derlados.computer_configurator.ui.pages.build_online_list

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.ui.pages.main.MainView
import com.derlados.computer_configurator.ui.adapters.BuildOnlineRecyclerAdapter
import com.derlados.computer_configurator.ui.pages.build.build_view.BuildOnlineViewFragment
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.derlados.computer_configurator.ui.pages.main.PageFragment
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
        presenter = BuildOnlineListPresenter(requireActivity() as MainView,this, App.app.resourceProvider)
        presenter.init()

        return currentFragment
    }

    override fun initRefreshing() {
        srlRefresh.isEnabled = true
        srlRefresh.setOnRefreshListener { presenter.refresh() }
    }

    override fun copyToClipboard(uri: String) {
        val clipboard: ClipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("simple text", uri)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
    }

    override fun disableRefreshAnim() {
        currentFragment.fragment_build_list_srl_refresh.isRefreshing = false
    }

    override fun <T : Build> setBuildsData(buildsData: ArrayList<T>) {
        rvBuildRecycler.layoutManager = LinearLayoutManager(context)
        rvBuildRecycler.adapter = BuildOnlineRecyclerAdapter(buildsData, ::selectBuild, ::onShareBuild, ::onReport)
    }

    private fun selectBuild(id: Int) {
        presenter.selectBuild(id)
    }

    private fun onShareBuild(id: Int) {
        presenter.share(id)
    }

    private fun onReport(id: Int) {
        val tvDialog = layoutInflater.inflate(R.layout.inflate_dialog_text, null) as TextView
        tvDialog.text =  "Пожаловаться на нарушение правил ?"

        AlertDialog.Builder(context, R.style.DarkAlert)
            .setCustomTitle(tvDialog)
            .setPositiveButton("Да") { _, _ ->  presenter.report(id) }
            .setNegativeButton("Нет") { _, _ -> }
            .show()
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