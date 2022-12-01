package com.derlados.computer_configurator.ui.pages.component_list

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
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.ui.adapters.ComponentRecyclerAdapter
import com.derlados.computer_configurator.ui.pages.component_info.ComponentInfoFragment
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.derlados.computer_configurator.ui.pages.filters.FilterDialogFragment
import kotlinx.android.synthetic.main.fragment_component_search.view.*

class ComponentSearchFragment : Fragment(), MainActivity.OnBackPressedListener,
    ComponentSearchView {

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
                presenter.searchComponentByText(searchText)
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
        activity?.title = arguments?.getString("title")
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

    override fun setTitleByCategory(category: ComponentCategory) {
        when(category) {
            ComponentCategory.CPU -> activity?.title = getString(R.string.CPUs)
            ComponentCategory.MOTHERBOARD -> activity?.title = getString(R.string.motherboards)
            ComponentCategory.HDD -> activity?.title = getString(R.string.HDDs)
            ComponentCategory.RAM -> activity?.title = getString(R.string.RAMs)
            ComponentCategory.SSD -> activity?.title = getString(R.string.SSDs)
            ComponentCategory.POWER_SUPPLY -> activity?.title = getString(R.string.power_supplies)
            ComponentCategory.GPU -> activity?.title = getString(R.string.GPUs)
            ComponentCategory.CASE -> activity?.title = getString(R.string.pc_cases)
            ComponentCategory.FAVOURITE -> activity?.title = getString(R.string.favourite)
            else -> {}
        }
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

    override fun closeFilters() {
        currentFragment.fragment_component_search_goods_img_filters.visibility = View.GONE
    }

    /**
     * Иниициализация (отрисовка) комплектующих, создает адаптек для RecyclerView
     */
    override fun setComponents(components: List<Component>, favoriteComponents: List<Component>,) {
        rvComponents.layoutManager = LinearLayoutManager(context)
        rvComponents.adapter = ComponentRecyclerAdapter(components, favoriteComponents, defaultImageId, ::onClickItem, ::onFavoriteClick)
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