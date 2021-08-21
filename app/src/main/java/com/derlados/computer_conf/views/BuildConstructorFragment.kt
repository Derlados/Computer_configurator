package com.derlados.computer_conf.views

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import com.derlados.computer_conf.App
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.view_interfaces.BuildConstructorView
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.BuildConstructorPresenter
import com.derlados.computer_conf.views.dialog_fragments.SaveDialogFragment
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.google.android.material.navigation.NavigationBarView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.util.*
import kotlin.collections.HashMap

class BuildConstructorFragment : Fragment(), TextWatcher, MainActivity.OnBackPressedListener, BuildConstructorView, FragmentResultListener {
    companion object  {
        const val SAVE_BUILD = "SAVE_BUILD"
        const val WITHOUT_SAVE = "WITHOUT_SAVE"
    }

    private var componentContainers: HashMap<ComponentCategory, Pair<Int, Int>> = hashMapOf(
            ComponentCategory.CPU to Pair(R.id.fragment_build_bt_head_cpu, R.id.fragment_build_expll_cpu),
            ComponentCategory.MOTHERBOARD to Pair(R.id.fragment_build_bt_head_mb, R.id.fragment_build_expll_mb),
            ComponentCategory.GPU to Pair(R.id.fragment_build_bt_head_gpu, R.id.fragment_build_expll_gpu),
            ComponentCategory.RAM to Pair(R.id.fragment_build_bt_head_ram, R.id.fragment_build_expll_ram),
            ComponentCategory.HDD to Pair(R.id.fragment_build_bt_head_hdd, R.id.fragment_build_expll_hdd),
            ComponentCategory.SSD to Pair(R.id.fragment_build_bt_head_ssd, R.id.fragment_build_expll_ssd),
            ComponentCategory.POWER_SUPPLY to Pair(R.id.fragment_build_bt_head_ps, R.id.fragment_build_expll_power_supply),
            ComponentCategory.CASE to Pair(R.id.fragment_build_bt_head_case, R.id.fragment_build_expll_case),
    )
    private var componentsTvCount: HashMap<Int, TextView> = HashMap()

    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    // Поля для модифицакции после возврата с меню выбора комплектующего
    private lateinit var etName: EditText
    private lateinit var tvPrice: TextView // Текстовое поле с ценой
    private lateinit var tvStatus: TextView
    private lateinit var etDesc: EditText
    private lateinit var tvCompatibility: TextView
    private lateinit var imgBuild: ImageView

    private lateinit var presenter: BuildConstructorPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        currentFragment = inflater.inflate(R.layout.fragment_build, container, false)
        setFragmentResultListener(SAVE_BUILD, ::onFragmentResult)
        setFragmentResultListener(WITHOUT_SAVE, ::onFragmentResult)

        etName = currentFragment.fragment_build_et_name
        etName.addTextChangedListener(this)
        tvPrice = currentFragment.fragment_build_tv_price
        imgBuild = currentFragment.fragment_build_img
        tvCompatibility = currentFragment.fragment_build_tv_compatibility
        tvStatus = currentFragment.fragment_build_tv_status
        etDesc = currentFragment.fragment_build_et_desc
        etDesc.addTextChangedListener(this)

        for ((key, value ) in componentContainers) {
            val btHeader: Button = currentFragment.findViewById(value.first)
            btHeader.setOnClickListener{
                pickComponent(key)
            }
        }

        presenter = BuildConstructorPresenter(this, App.app.resourceProvider)
        presenter.init()
        return currentFragment
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.build_menu, menu)
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
        presenter.saveBuildOnServer()
        presenter.finish()
        return false
    }

    /**
     * Отрыктие диалога с предложением сохранить изменения
     */
    override fun showSaveDialog() {
        val dialogFragment: DialogFragment = SaveDialogFragment()
        activity?.let { dialogFragment.show(it.supportFragmentManager, "build") }
    }

    override fun showToast(message: String) {
        Toast.makeText(App.app.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == SAVE_BUILD) {
            presenter.saveBuildOnServer()
        } else {
            exitView()
        }
    }

    /**
     * Установка заголовочных данных (название сборки и её описание)
     */
    override fun setHeaderData(name: String, desc: String) {
        etName.setText(name)
        etDesc.setText(desc)
    }

    override fun setImage(image: Bitmap) {
        imgBuild.setImageBitmap(image)
    }

    override fun setImage(url: String) {
        Picasso.get().load(url).into(imgBuild)
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

    override fun updatePrice(price: Int) {
        tvPrice.text = App.app.resources.getString(R.string.component_price, price)
    }

    /**
     * Добавление нового комплектующего в сборку
     * ExpandableLinearLayout работает не корректно при изменении в нем содержимого. ExpandableLinearLayout
     * необходимо переинициализировать каждый раз когда пользователь выбирает новое комплектующее
     * и только в этом случае !!!!
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param init - флаг, необходимо ли переинициализировать ExpandableLinearLayout
     */
    override fun addNewComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean) {
        componentContainers[category]?.let { (btId, containerId) ->
            val btHeader: Button = currentFragment.findViewById(btId)
            val expandContainer: ExpandableLinearLayout = currentFragment.findViewById(containerId)
            createComponentCard(category, isMultiple, buildComponent, expandContainer.getChildAt(0) as LinearLayout)
            if (init) {
                expandContainer.initLayout()
                expandContainer.expand()
                btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_36, 0)
            } else {
                btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
            }

            // Вместо перехода к поиску комплектующего, кнопка раскрывает список с комплектуюшими
            btHeader.setOnClickListener { toggleCompListVisibility(btHeader, expandContainer) }
        }
    }

    override fun exitView() {
        frListener.popBackStack()
    }

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    private fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, parent: LinearLayout) {
        val card = layoutInflater.inflate(R.layout.inflate_component_item, parent, false) as LinearLayout

        val component = buildComponent.component
        val count = buildComponent.count

        card.component_item_tv_name.text = component.name
        card.component_item_tv_price.text = App.app.resources.getString(R.string.component_price, component.price)
        Picasso.get().load(component.imageUrl).into(card.component_item_img)

        val tvHeaders: ArrayList<TextView> = ArrayList()
        val tvValues: ArrayList<TextView> = ArrayList()
        val tlData: TableLayout = card.findViewById(R.id.component_item_tl_data)
        for (i in 1 until tlData.childCount) {
            val row: TableRow = tlData.getChildAt(i) as TableRow

            if (i % 2 == 0) {
                for (j in 0 until row.childCount)
                    tvValues.add(row.getChildAt(j) as TextView)
            } else {
                for (j in 0 until row.childCount)
                    tvHeaders.add(row.getChildAt(j) as TextView)
            }
        }

        val attributes: List<Component.Attribute> = component.getPreviewAttributes()
        for (i in attributes.indices) {
            tvHeaders[i].text = attributes[i].name
            tvValues[i].text = attributes[i].value
        }

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
            card.inflate_component_item_ll_count.visibility = View.VISIBLE
            card.inflate_component_item_tv_count.text = count.toString()
            card.inflate_component_item_bt_increase.setOnClickListener { presenter.increaseComponent(category, component) }
            card.inflate_component_item_bt_reduce.setOnClickListener { presenter.reduceComponent(category, component) }

            componentsTvCount[component.id] = card.inflate_component_item_tv_count
        }

        parent.addView(card)
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////

    /**
     * Открытие/скрытие списка комплектуюзих в определенной категории
     */
    private fun toggleCompListVisibility(btHeader: Button, container: ExpandableLinearLayout) {
        // Поворот стрелки на 180 градусов. Относительно текущего состояния
        if (container.isExpanded) {
            btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
        } else {
            btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_36, 0)
        }

        container.toggle()
    }

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