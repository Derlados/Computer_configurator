package com.derlados.computer_configurator.ui.pages.build

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.stores.entities.build.Build
import com.derlados.computer_configurator.stores.entities.Component
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_build_section.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.util.ArrayList

abstract class BuildViewFragment: Fragment(), BaseBuildView, MainActivity.OnBackPressedListener {
    protected lateinit var currentFragment: View

    // Поля для модифицакции после возврата с меню выбора комплектующего
    protected lateinit var etName: EditText
    protected lateinit var tvPrice: TextView // Текстовое поле с ценой
    protected lateinit var tvStatus: TextView
    protected lateinit var etDesc: EditText
    protected lateinit var tvCompatibility: TextView
    protected lateinit var imgBuild: ImageView

    protected lateinit var componentContainers: HashMap<ComponentCategory, View>
    protected var componentsTvCount: HashMap<Int, TextView> = HashMap()

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }

    protected open fun initFields() {
        etName = currentFragment.fragment_build_et_name
        tvPrice = currentFragment.fragment_build_tv_price
        imgBuild = currentFragment.fragment_build_img
        tvCompatibility = currentFragment.fragment_build_tv_compatibility
        tvStatus = currentFragment.fragment_build_tv_status_or_user_value
        etDesc = currentFragment.fragment_build_et_desc

        componentContainers = hashMapOf(
            ComponentCategory.CPU to currentFragment.fragment_build_ll_cpu_section,
            ComponentCategory.MOTHERBOARD to currentFragment.fragment_build_ll_mb_section,
            ComponentCategory.GPU to currentFragment.fragment_build_ll_gpu_section,
            ComponentCategory.RAM to currentFragment.fragment_build_ll_ram_section,
            ComponentCategory.HDD to currentFragment.fragment_build_ll_hdd_section,
            ComponentCategory.SSD to currentFragment.fragment_build_ll_ssd_section,
            ComponentCategory.POWER_SUPPLY to currentFragment.fragment_build_ll_ps_section,
            ComponentCategory.CASE to currentFragment.fragment_build_ll_case_section,
        )

        // TODO databind в xml не сработал, потому приходится делать эту дичь вручную, необходимо выяснить
        currentFragment.fragment_build_ll_cpu_section.inflate_build_section_bt.setText(R.string.cpu)
        currentFragment.fragment_build_ll_mb_section.inflate_build_section_bt.setText(R.string.motherboard)
        currentFragment.fragment_build_ll_gpu_section.inflate_build_section_bt.setText(R.string.gpu)
        currentFragment.fragment_build_ll_ram_section.inflate_build_section_bt.setText(R.string.ram)
        currentFragment.fragment_build_ll_hdd_section.inflate_build_section_bt.setText(R.string.hdd)
        currentFragment.fragment_build_ll_ssd_section.inflate_build_section_bt.setText(R.string.ssd)
        currentFragment.fragment_build_ll_ps_section.inflate_build_section_bt.setText(R.string.power_supply)
        currentFragment.fragment_build_ll_case_section.inflate_build_section_bt.setText(R.string.pc_case)
    }

    /**
     * Установка заголовочных данных (название сборки и её описание)
     */
    override fun setHeaderData(name: String, desc: String) {
        activity?.title = name
        etName.setText(name)
        etDesc.setText(desc)
    }

    override fun setImage(url: String) {
        Picasso.get().load(url).into(imgBuild)
    }

    override fun setPrice(price: Int) {
        tvPrice.text = App.app.resources.getString(R.string.component_price, price)
    }

    /**
     * Добавление комплектующего в сборку
     * ExpandableLinearLayout работает не корректно при изменении в нем содержимого. ExpandableLinearLayout
     * необходимо переинициализировать каждый раз когда пользователь выбирает новое комплектующее
     * и только в этом случае !!!!
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param init - флаг, необходимо ли переинициализировать ExpandableLinearLayout
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: Build.BuildComponent, init: Boolean) {
        componentContainers[category]?.let { container ->
            val btHeader = container.inflate_build_section_bt
            val expandContainer = container.inflate_build_section_ell_components
            val componentContainer = container.inflate_build_section_ll_components_cont

            val card = createComponentCard(category, isMultiple, buildComponent, expandContainer.getChildAt(0) as LinearLayout)
            componentContainer.addView(card, componentContainer.childCount - 1)

            if (init) {
                updatedExpandLayout(expandContainer, true)
                btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_36, 0)
            } else {
                btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
            }

            // Вместо перехода к поиску комплектующего, кнопка раскрывает список с комплектуюшими
            btHeader.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    toggleCompListVisibility(btHeader, expandContainer)
                }

                return@setOnTouchListener true
            }
        }
    }

    override fun changeVisibilityAddMoreBt(isVisible: Boolean, category: ComponentCategory) {
        componentContainers[category]?.let {
            it.inflate_build_section_bt_add_more.visibility = View.VISIBLE
            if (isVisible) {
                it.inflate_build_section_bt_add_more.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                it.inflate_build_section_bt_add_more.text = "+"
            } else {
                it.inflate_build_section_bt_add_more.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                it.inflate_build_section_bt_add_more.text = "Все порты заняты !"
            }
        }
    }

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    protected open fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: Build.BuildComponent, parent: LinearLayout): View {
        val card = layoutInflater.inflate(R.layout.inflate_component_item, parent, false) as LinearLayout

        val component = buildComponent.component
        val count = buildComponent.count

        card.component_item_tv_name.text = component.name
        card.component_item_tv_price.text = App.app.resources.getString(R.string.component_price, component.price)
        Picasso.get().load(component.imageUrl).into(card.component_item_img)

        // Создание короткого описания комплектующего
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
            if (i == tvHeaders.size) {
                break
            }

            tvHeaders[i].text = attributes[i].name
            tvValues[i].text = attributes[i].value
        }

        // Открытие блока на изменение количества комплектующего (для ОЗУ, накопителей и т.д.)
        if (isMultiple) {
            card.inflate_component_item_ll_count.visibility = View.VISIBLE
            card.inflate_component_item_tv_count.text = count.toString()
            componentsTvCount[component.id] = card.inflate_component_item_tv_count
        }


        return card
    }

    /**
     * Открытие/скрытие списка комплектуюзих в определенной категории
     */
    protected open fun toggleCompListVisibility(btHeader: Button, container: ExpandableLinearLayout) {
        // Поворот стрелки на 180 градусов. Относительно текущего состояния
        if (container.isExpanded) {
            btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
        } else {
            btHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_36, 0)
        }

        container.toggle()
    }

    protected open fun updatedExpandLayout(ell: ExpandableLinearLayout, isExpanded: Boolean) {
        ell.initLayout()
        if (isExpanded) {
            ell.expand()
        }
    }
}