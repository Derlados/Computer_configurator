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
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.interfaces.ComponentSearchView
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.ComponentSearchPresenter
import com.derlados.computer_conf.views.adapters.ComponentRecyclerAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_component_search.view.*
import java.util.*
import kotlin.collections.ArrayList

class ComponentSearchFragment : Fragment(), MainActivity.OnBackPressedListener, ComponentSearchView {
    private lateinit var presenter: ComponentSearchPresenter

    private var keepVisible = true
    private lateinit var category: ComponentCategory
    private lateinit var searchText: String

    private lateinit var rvComponents: RecyclerView
    private lateinit var currentFragment: View
    private lateinit var searchString: EditText
    private lateinit var tvNotFound: TextView
    private lateinit var pbLoading: ProgressBar
    private lateinit var fragmentListener: OnFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_component_search, container, false)
        category = requireArguments()["category"] as ComponentCategory
        rvComponents = currentFragment.fragment_component_search_rv
        tvNotFound = currentFragment.fragment_component_search_tv_not_found
        pbLoading = currentFragment.fragment_component_search_pb_loading

        // Поисковая строка
        searchString = currentFragment.fragment_shop_search_goods_et_search
        searchString.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ -> // Реакация на кнопку submit
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchText = searchString.text.toString()

                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        presenter = ComponentSearchPresenter(this, category)
        presenter.init()

        return currentFragment
    }

    // Решение проблемы с анимацией, по скольку вызывается 2 popBackStack метода то и анимация играет дважды, из-за чего появлялось мерцание
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

    override fun onStop() {
        super.onStop()
        presenter.finish()
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

    /**
     * Иниициализация (отрисовка) комплектующих, создает адаптек для RecyclerView
     */
    override fun setComponents(components: ArrayList<Component>) {
        rvComponents.layoutManager = LinearLayoutManager(context)
        rvComponents.adapter = ComponentRecyclerAdapter(components, ::onClickItem)
    }

    override fun updateComponents() {
        pbLoading.visibility = View.GONE
        val adapter: RecyclerView.Adapter<*>? = rvComponents.adapter
        adapter?.notifyItemRangeChanged(0, adapter.itemCount)
    }

    /** Отправка комплектующего в следующий фрагмет для отображения полной информации о нем
     * Метод используется
     * @param component - комплектующее
     */
    private fun onClickItem (component: Component) {
        val data = Bundle()
        data.putString("component", Gson().toJson(component)) // Объект передается в виде json
        data.putSerializable("category", category)
        //fragmentListener.nextFragment(this, FullGoodDataFragment(), data, null)
    }
}