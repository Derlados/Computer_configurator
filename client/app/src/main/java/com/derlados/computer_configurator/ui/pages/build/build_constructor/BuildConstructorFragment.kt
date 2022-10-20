package com.derlados.computer_configurator.ui.pages.build.build_constructor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.models.entities.BuildData
import com.derlados.computer_configurator.models.entities.Component
import com.derlados.computer_configurator.ui.pages.build.BuildViewFragment
import com.derlados.computer_configurator.ui.decorators.AnimOnTouchListener
import com.derlados.computer_configurator.ui.pages.component_info.ComponentInfoFragment
import com.derlados.computer_configurator.ui.pages.component_list.ComponentSearchFragment
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_build.*
import kotlinx.android.synthetic.main.inflate_build_section.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*

open class BuildConstructorFragment : BuildViewFragment(), TextWatcher, MainActivity.OnBackPressedListener,
    BuildConstructorView {
    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var presenter: BuildConstructorPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        currentFragment = inflater.inflate(R.layout.fragment_build, container, false)

        initFields()

        presenter = BuildConstructorPresenter(this, App.app.resourceProvider)
        presenter.init()
        return currentFragment
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initFields() {
        super.initFields()
        etName.addTextChangedListener(this)
        etDesc.addTextChangedListener(this)

        for ((key, value ) in componentContainers) {
            value.inflate_build_section_bt.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
                presenter.selectCategoryToSearch(key)
                return@OnTouchListener true
            }))

            value.inflate_build_section_bt_add_more.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
                presenter.selectCategoryToSearch(key)
                return@OnTouchListener true
            }))
        }
    }

    /**
     * Каждый раз когда пользователь возвращается к экрану, необходимо проверять его выбор.
     * Возврат возможен только из поиска комплектующих
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            presenter.checkUserChoice()
            activity?.title = getString(R.string.configurator)
        }
    }

    override fun setHeaderData(name: String, desc: String) {
        super.setHeaderData(name, desc)
        activity?.title = getString(R.string.configurator)
    }

    /**
     * Управление выходом из экрана переходит под управление презентера
     */
    override fun onBackPressed(): Boolean {
        presenter.finish()
        super.onBackPressed()
        return true
    }

    override fun showToast(message: String) {
        Toast.makeText(App.app.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun prohibitPickComponent(category: ComponentCategory) {
        componentContainers[category]?.let {
            it.inflate_build_section_bt_add_more.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
                Toast.makeText(context, context?.getString(R.string.cannot_add_more), Toast.LENGTH_SHORT).show()
                return@OnTouchListener true
            }))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun allowPickComponent(category: ComponentCategory) {
        componentContainers[category]?.let {
            it.inflate_build_section_bt_add_more.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
                presenter.selectCategoryToSearch(category)
                return@OnTouchListener true
            }))
        }
    }

    /**
     * Установка статуса сборки.
     * @param status - короткое описание статуса
     * @param message - сообщение о не корректности сборки, не совместимости
     */
    override fun setStatus(status: String, colorStatus: Int, message: String?) {
        tvStatus.text = status
        tvStatus.setTextColor(colorStatus)
        tvCompatibility.text = message
    }

    override fun setCountComponents(id: Int, count: Int) {
        componentsTvCount[id]?.let {
            it.text = count.toString()
        }
    }

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, parent: LinearLayout): View {
        val card = super.createComponentCard(category, isMultiple, buildComponent, parent)
        val component = buildComponent.component

        // Кнопка удалить комплектующее
        val ibtDelete = card.inflate_component_item_bt_favorite
        ibtDelete.setOnClickListener {
            parent.removeView(card) // Удаление комплектующего
            removeComponent(category, component)
        }
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, App.app.theme)) // Отрисовка значка

        card.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
            openComponentInfo(category, component)
            return@OnTouchListener true
        }))


        // Открытие блока на изменение количества комплектующего (для ОЗУ, накопителей и т.д.)
        if (isMultiple) {
            card.inflate_component_item_bt_increase.setOnClickListener { presenter.increaseComponent(category, component) }
            card.inflate_component_item_bt_reduce.setOnClickListener { presenter.reduceComponent(category, component) }
        }

        return card
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////

    override fun openComponentSearch() {
        frListener.nextFragment(this, ComponentSearchFragment(),  BackStackTag.COMPONENT_SEARCH)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun removeComponent(category: ComponentCategory, component: Component) {
        presenter.removeComponent(category, component)

        componentContainers[category]?.let { container ->
            val btHeader = container.inflate_build_section_bt
            val componentContainer = container.inflate_build_section_ll_components_cont

            if (componentContainer.childCount == 0) {
                // Вместо перехода к поиску комплектующего, кнопка раскрывает список с комплектуюшими
                btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_24, 0)
                btHeader.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
                    presenter.selectCategoryToSearch(category)
                    return@OnTouchListener true
                }))
                container.inflate_build_section_ell_components.initLayout()
            } else {
                val isExpanded = container.inflate_build_section_ell_components.isExpanded
                container.inflate_build_section_ell_components.initLayout()
                if (isExpanded) {
                    container.inflate_build_section_ell_components.expand()
                }
            }

        }
    }

    private fun openComponentInfo(category: ComponentCategory, component: Component) {
        presenter.selectComponentToVIew(category, component)
        frListener.nextFragment(this, ComponentInfoFragment(),  BackStackTag.COMPONENT_INFO)
    }

    //////////////////////////////////////////////Обработчик ввода текста//////////////////////////////////////////////////
    //TODO
    // Может необходимо производить контроль и ограничить количество символов для имени сборки
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        // Нахождение нужного EditText
        val view = activity?.currentFocus
        val text = (view as? EditText)?.text.toString()

        // Определение того, какое поле меняет юзер
        if (view?.id == R.id.fragment_build_et_name)
            presenter.setName(text)
        else if (view?.id == R.id.fragment_build_et_desc) {
            presenter.setDescription(text)
        }
    }
}