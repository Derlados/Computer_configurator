package com.derlados.computer_conf.views

import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.entities.BuildData
import com.derlados.computer_conf.models.entities.Component
import com.derlados.computer_conf.view_interfaces.BaseBuildView
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.util.ArrayList

abstract class BuildViewFragment: Fragment(), BaseBuildView {
    protected lateinit var currentFragment: View

    // Поля для модифицакции после возврата с меню выбора комплектующего
    protected lateinit var etName: EditText
    protected lateinit var tvPrice: TextView // Текстовое поле с ценой
    protected lateinit var tvStatus: TextView
    protected lateinit var etDesc: EditText
    protected lateinit var tvCompatibility: TextView
    protected lateinit var imgBuild: ImageView

    protected var componentContainers: HashMap<ComponentCategory, Pair<Int, Int>> = hashMapOf(
            ComponentCategory.CPU to Pair(R.id.fragment_build_bt_head_cpu, R.id.fragment_build_expll_cpu),
            ComponentCategory.MOTHERBOARD to Pair(R.id.fragment_build_bt_head_mb, R.id.fragment_build_expll_mb),
            ComponentCategory.GPU to Pair(R.id.fragment_build_bt_head_gpu, R.id.fragment_build_expll_gpu),
            ComponentCategory.RAM to Pair(R.id.fragment_build_bt_head_ram, R.id.fragment_build_expll_ram),
            ComponentCategory.HDD to Pair(R.id.fragment_build_bt_head_hdd, R.id.fragment_build_expll_hdd),
            ComponentCategory.SSD to Pair(R.id.fragment_build_bt_head_ssd, R.id.fragment_build_expll_ssd),
            ComponentCategory.POWER_SUPPLY to Pair(R.id.fragment_build_bt_head_ps, R.id.fragment_build_expll_power_supply),
            ComponentCategory.CASE to Pair(R.id.fragment_build_bt_head_case, R.id.fragment_build_expll_case),
    )
    protected var componentsTvCount: HashMap<Int, TextView> = HashMap()

    protected open fun initFields() {
        etName = currentFragment.fragment_build_et_name
        tvPrice = currentFragment.fragment_build_tv_price
        imgBuild = currentFragment.fragment_build_img
        tvCompatibility = currentFragment.fragment_build_tv_compatibility
        tvStatus = currentFragment.fragment_build_tv_status_or_user_value
        etDesc = currentFragment.fragment_build_et_desc
    }

    /**
     * Установка заголовочных данных (название сборки и её описание)
     */
    override fun setHeaderData(name: String, desc: String) {
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
    override fun addComponent(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, init: Boolean) {
        componentContainers[category]?.let { (btId, containerId) ->
            val btHeader: Button = currentFragment.findViewById(btId)
            val expandContainer: ExpandableLinearLayout = currentFragment.findViewById(containerId)

            val parent = expandContainer.getChildAt(0) as LinearLayout
            val card = createComponentCard(category, isMultiple, buildComponent, expandContainer.getChildAt(0) as LinearLayout)
            parent.addView(card)

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

    /**
     * Отрисовка "карточки" комплектюущего. Модифицированный аналог того, что появляется при поиске
     * @param category - категория комплектующего
     * @param isMultiple - флаг, если установлено в true, к блоку будет добавлен интерфейс для изменения количества комплектующего
     * @param buildComponent - комплектующее из сборки (расширенный объект с количеством)
     * @param parent - отсовский лаяут, куда будет прекреплена "карточка"
     */
    protected open fun createComponentCard(category: ComponentCategory, isMultiple: Boolean, buildComponent: BuildData.BuildComponent, parent: LinearLayout): View {
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
}