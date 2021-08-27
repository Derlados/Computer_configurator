package com.derlados.computer_conf.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.App
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.view_interfaces.ComponentSearchView
import com.derlados.computer_conf.models.entities.Component
import com.derlados.computer_conf.presenters.ComponentSearchPresenter
import com.derlados.computer_conf.views.adapters.ComponentRecyclerAdapter
import com.derlados.computer_conf.views.dialog_fragments.FilterDialogFragment
import kotlinx.android.synthetic.main.fragment_component_search.view.*
import java.util.*
import kotlin.collections.HashMap

class ComponentSearchFragment : Fragment(), MainActivity.OnBackPressedListener, ComponentSearchView {
    private var defaultImageId: Int = -1

    private var keepVisible = true
    private lateinit var searchText: String

    private lateinit var currentFragment: View
    private lateinit var filterDialog: FilterDialogFragment

    private lateinit var rvComponents: RecyclerView
    private lateinit var searchString: EditText
    private lateinit var tvNotFound: TextView
    private lateinit var pbLoading: ProgressBar
    private lateinit var fragmentListener: OnFragmentInteractionListener

    private lateinit var presenter: ComponentSearchPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_component_search, container, false)

        rvComponents = currentFragment.fragment_component_search_rv
        tvNotFound = currentFragment.fragment_component_search_tv_not_found
        pbLoading = currentFragment.fragment_component_search_pb_loading

        // Поисковая строка
        searchString = currentFragment.fragment_component_search_goods_et_search
        searchString.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ -> // Реакация на кнопку submit
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchText = searchString.text.toString()
                presenter.searchComponent(searchText)
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        // Диалог для фильтров
        filterDialog = FilterDialogFragment(::filtersDialogListener)

        currentFragment.fragment_component_search_goods_img_filters.setOnClickListener { showFiltersDialog() }

        presenter = ComponentSearchPresenter(this, App.app.resourceProvider)
        presenter.init()

        return currentFragment
    }



    /**
     * Решение проблемы с анимацией, по скольку вызывается 2 popBackStack метода то и анимация играет дважды,
     * из-за чего появлялось мерцание
     */
    override fun onBackPressed(): Boolean {
        view?.visibility = View.VISIBLE
        keepVisible = true
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !keepVisible) {
            view?.visibility = View.GONE
        }
        keepVisible = false
        super.onHiddenChanged(hidden)
    }

    override fun onDestroy() {
        rvComponents.adapter?.notifyItemRangeChanged(0, 0)
        if (filterDialog.isAdded) {
            filterDialog.dismiss()
        }
        presenter.finish()

        super.onDestroy()
    }

    override fun setDefaultImageByCategory(id: Int) {
        defaultImageId = id
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showNotFoundMessage() {
        tvNotFound.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
    }

    override fun openProgressBar() {
        tvNotFound.visibility = View.GONE
        pbLoading.visibility = View.VISIBLE
    }

    override fun closeProgressBar() {
        pbLoading.visibility = View.GONE
    }

    /**
     * Иниициализация (отрисовка) комплектующих, создает адаптек для RecyclerView
     */
    override fun setComponents(components: List<Component>, trackPrices: HashMap<Int, Int>) {
        rvComponents.layoutManager = LinearLayoutManager(context)
        rvComponents.adapter = ComponentRecyclerAdapter(components, trackPrices, defaultImageId, ::onClickItem, ::onFavoriteClick)
    }

    /**
     * Обновление комплектующих в списке. Тяжелое обновление, используется при сортировуке
     */
    override fun updateComponentList() {
        rvComponents.adapter?.notifyDataSetChanged()
    }

    override fun updateSingleComponent(index: Int) {
        rvComponents.adapter?.notifyItemChanged(index)
    }

    override fun removeSingleComponent(index: Int) {
        rvComponents.adapter?.notifyItemRemoved(index)
    }

    private fun showFiltersDialog() {
        if (filterDialog.isAdded) {
            filterDialog.dialog?.show()
        } else {
            activity?.let { filterDialog.show(it.supportFragmentManager, "filters") }
        }

    }

    private fun filtersDialogListener() {
        presenter.filterComponents()
    }

    /** Переход к полной информации о комплектующем
     * Метод используется в адаптере
     * @param component - комплектующее
     */
    private fun onClickItem (component: Component) {
        presenter.saveChosenComponent(component)
        fragmentListener.nextFragment(this, ComponentInfoFragment(), BackStackTag.COMPONENT_INFO)
    }

    private fun onFavoriteClick(id: Int) {
        presenter.toggleFavoriteStatus(id)
    }
}