package com.derlados.computer_conf.views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.derlados.computer_conf.App
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.view_interfaces.BuildConstructorView
import com.derlados.computer_conf.models.entities.BuildData
import com.derlados.computer_conf.models.entities.Component
import com.derlados.computer_conf.presenters.BuildConstructorPresenter
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import kotlinx.android.synthetic.main.inflate_component_item.view.*

open class BuildConstructorFragment : BuildViewFragment(), TextWatcher, MainActivity.OnBackPressedListener, BuildConstructorView {
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

    override fun initFields() {
        super.initFields()
        etName.addTextChangedListener(this)
        etDesc.addTextChangedListener(this)

        for ((key, value ) in componentContainers) {
            val btHeader: Button = currentFragment.findViewById(value.first)
            btHeader.setOnClickListener{
                pickComponent(key)
            }
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
        }
    }

    /**
     * Управление выходом из экрана переходит под управление презентера
     */
    override fun onBackPressed(): Boolean {
        presenter.finish()
        return true
    }

    override fun showToast(message: String) {
        Toast.makeText(App.app.applicationContext, message, Toast.LENGTH_SHORT).show()
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
    override fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, parent: LinearLayout): View {
        val card = super.createComponentCard(category, isMultiple, buildComponent, parent)
        val component = buildComponent.component

        // Кнопка удалить комплектующее
        val ibtDelete = card.inflate_component_item_bt_favorite
        ibtDelete.setOnClickListener {
            removeComponent(category, component)
            parent.removeView(card) // Удаление комплектующего
        }
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, App.app.theme)) // Отрисовка значка

        card.setOnClickListener {
            openComponentInfo(category, component)
        }

        // Открытие блока на изменение количества комплектующего (для ОЗУ, накопителей и т.д.)
        if (isMultiple) {
            card.inflate_component_item_bt_increase.setOnClickListener { presenter.increaseComponent(category, component) }
            card.inflate_component_item_bt_reduce.setOnClickListener { presenter.reduceComponent(category, component) }
        }

        return card
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////

    /**
     * Обработчик кнопки "+" для выбора комплектующих в сборку
     */
    private fun pickComponent(category: ComponentCategory) {
        presenter.selectCategoryToSearch(category)
        frListener.nextFragment(this, ComponentSearchFragment(),  BackStackTag.COMPONENT_SEARCH)
    }

    private fun removeComponent(category: ComponentCategory, component: Component) {
        presenter.removeComponent(category, component)

        componentContainers[category]?.let { (btId, containerId) ->
            val btHeader: Button = currentFragment.findViewById(btId)
            currentFragment.findViewById<ExpandableLinearLayout>(containerId).initLayout()

            // Вместо перехода к поиску комплектующего, кнопка раскрывает список с комплектуюшими
            btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_24, 0)
            btHeader.setOnClickListener {
                pickComponent(category)
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