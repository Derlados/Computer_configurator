package com.derlados.computer_conf.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.OnlineBuildsPresenter
import com.derlados.computer_conf.view_interfaces.BuildOnlineView
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*

class BuildOnlineViewFragment : BuildViewFragment(), BuildOnlineView {
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var presenter: OnlineBuildsPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        currentFragment = inflater.inflate(R.layout.fragment_build, container, false)

        initFields()

        presenter = OnlineBuildsPresenter(this, App.app.resourceProvider)
        presenter.init()
        return currentFragment
    }

    //    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    //        inflater.inflate(R.menu.build_menu, menu)
    //    }

    override fun initFields() {
        super.initFields()

        // Отключение полей EditText которые используются в конструкторе
        disableEditText(etName)
        disableEditText(etDesc)
        currentFragment.fragment_build_tv_status_or_user_value.setTextColor(Color.WHITE)

        currentFragment.fragment_build_tv_status_or_user_head.setText(R.string.creator)
        tvCompatibility.visibility = View.GONE
    }

    override fun deleteEmptyLists() {
        // Все пустые группы комплектующих скрываются
        for ((_, value ) in componentContainers) {
            val componentContainer = currentFragment.findViewById<ExpandableLinearLayout>(value.second)
            val componentBt = currentFragment.findViewById<Button>(value.first)
            val componentList = componentContainer.getChildAt(0) as LinearLayout

            if (componentList.childCount == 0) {
                componentContainer.visibility = View.GONE
                componentBt.visibility = View.GONE
            }
        }
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null

        if (editText.background is InsetDrawable) {
            val insetDrawable = editText.background as InsetDrawable
            val originalDrawable = insetDrawable.drawable!!
            editText.background = InsetDrawable(originalDrawable,0, 0, 0, 0)
            editText.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    override fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, parent: LinearLayout): View {
        val card = super.createComponentCard(category, isMultiple, buildComponent, parent)
        val component = buildComponent.component

        card.inflate_component_item_bt_favorite.visibility = View.GONE

        card.setOnClickListener {
            openComponentInfo(category, component)
        }

        // Открытие блока на изменение количества комплектующего (для ОЗУ, накопителей и т.д.)
        if (isMultiple) {
            card.inflate_component_item_bt_increase.visibility = View.GONE
            card.inflate_component_item_bt_reduce.visibility = View.GONE
        }

        return card
    }

    private fun openComponentInfo(category: ComponentCategory, component: Component) {
        presenter.selectComponentToVIew(category, component)
        frListener.nextFragment(this, ComponentInfoFragment(),  BackStackTag.COMPONENT_INFO)
    }

    override fun setPrice(price: Int) {
        tvPrice.text =  App.app.resources.getString(R.string.component_price, price)
    }

    override fun setUsername(username: String) {
        currentFragment.fragment_build_tv_status_or_user_value.text = username
    }
}