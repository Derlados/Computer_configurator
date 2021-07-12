package com.derlados.computer_conf.views.pageFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.view_interfaces.PageBuildsView
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.presenters.PageBuildsPresenter
import com.derlados.computer_conf.views.adapters.BuildRecyclerAdapter
import com.derlados.computer_conf.views.BuildConstructorFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_build_list.view.*

class BuildsFragment : PageFragment(), PageBuildsView {
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var rvBuildRecycler: RecyclerView // Основной контейнер для бланков сборок
    private lateinit var createBt: FloatingActionButton

    private lateinit var presenter: PageBuildsPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_build_list, container, false)

        rvBuildRecycler = currentFragment.fragment_build_list_rv
        createBt = currentFragment.fragment_build_list_bt_add
        createBt.setOnClickListener {
            createBuild()
        }

        presenter = PageBuildsPresenter(this)
        presenter.init()
        return currentFragment
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            presenter.userReturn()
        }
    }

    override fun <T : BuildData> setBuildsData(buildsData: ArrayList<T>) {
        rvBuildRecycler.layoutManager = LinearLayoutManager(context)
        rvBuildRecycler.adapter = BuildRecyclerAdapter(buildsData, ::selectBuild, ::removeBuild)
    }

    override fun updateRangeBuildList(size: Int) {
        rvBuildRecycler.adapter?.notifyItemRangeChanged(0, size)
    }

    override fun updateItemBuildList(index: Int) {
        rvBuildRecycler.adapter?.notifyItemChanged(index)
    }

    override fun removeItemBuildList(index: Int) {
        rvBuildRecycler.adapter?.notifyItemRemoved(index)
    }

    override fun openBuildConstructor() {
        frListener.nextFragment(this, BuildConstructorFragment(), BackStackTag.BUILD)
    }

    private fun createBuild() {
        presenter.createNewBuild()
    }

    private fun selectBuild(id: String) {
        presenter.selectBuild(id)
    }

    private fun removeBuild(id: String) {
        presenter.removeBuild(id)
    }
}