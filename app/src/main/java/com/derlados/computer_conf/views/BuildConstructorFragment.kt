package com.derlados.computerconf.VIews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.interfaces.BuildConstructorView
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.presenters.BuildConstructorPresenter
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import com.derlados.computer_conf.views.SaveDialogFragment
import com.derlados.computer_conf.views.pageFragment.SearchFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_build.view.*
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.util.*
import kotlin.collections.HashMap

class BuildConstructorFragment : Fragment(), TextWatcher, MainActivity.OnBackPressedListener, BuildConstructorView {
    companion object {
        const val SAVE_DIALOG_FRAGMENT = 1
    }

    private var componentContainers: HashMap<ComponentCategory, Int> = hashMapOf(
            ComponentCategory.CPU to R.id.fragment_build_ll_cpu,
            ComponentCategory.MOTHERBOARD to R.id.fragment_build_ll_mb,
            ComponentCategory.GPU to R.id.fragment_build_ll_gpu,
            ComponentCategory.RAM to R.id.fragment_build_ll_ram,
            ComponentCategory.HDD to R.id.fragment_build_ll_hdd,
            ComponentCategory.SSD to R.id.fragment_build_ll_ssd,
            ComponentCategory.POWER_SUPPLY to R.id.fragment_build_ll_power_supply,
            ComponentCategory.CASE to R.id.fragment_build_ll_case,
    )

    private lateinit var frListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    // Поля для модифицакции после возврата с меню выбора комплектующего
    private lateinit var tvName: TextView // Текстовое поле с ценой
    private lateinit var tvPrice: TextView // Текстовое поле с ценой
    private lateinit var tvStatus: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvCompatibility: TextView
    private lateinit var imgBuild: ImageView
    private lateinit var llComponents: LinearLayout

    private lateinit var presenter: BuildConstructorPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_build, container, false)
        //(currentFragment.findViewById<View>(R.id.fragment_build_full_menu_bottom_navigator) as BottomNavigationView).setOnNavigationItemSelectedListener(this)

        tvName = currentFragment.fragment_build_et_name
        tvPrice = currentFragment.fragment_build_tv_price
        imgBuild = currentFragment.fragment_build_img
        tvCompatibility = currentFragment.fragment_build_tv_compatibility
        tvStatus = currentFragment.fragment_build_tv_status
        tvDesc = currentFragment.fragment_build_et_desc
        llComponents = currentFragment.fragment_build_ll_component_list

        presenter = BuildConstructorPresenter(this)

        // Установка обработчиков нажатий на кнопку "+" для добавления комплектующих
        for ((category, value) in componentContainers) {
            val btAdd = (currentFragment.findViewById<LinearLayout>(value)).getChildAt(0) as Button
            btAdd.setOnClickListener {
                pickComponent(category)
            }
        }

        // Каждая вторая кнопка является заголовком категории
        // TODO (Нужно придумать лучший способ организации)
        for (i in 0 until llComponents.childCount step 2) {
            llComponents.getChildAt(i).setOnClickListener(::toggleCompListVisibility)
        }

        return currentFragment
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
        return false
    }

    //TODO Этот способ устарел, новый через setFragmentResultListener
    /**
     *  Для обработки диалога который появляется, если пользователь захотел выйти из фрагмента сборки не сохранившись
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SAVE_DIALOG_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Toast.makeText(App.app.applicationContext, "Сохранено", Toast.LENGTH_SHORT).show()
        }
        frListener.popBackStack()
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Отрыктие диалога с предложением сохранить изменения
    override fun showSaveDialog() {
        val dialogFragment: DialogFragment = SaveDialogFragment()
        dialogFragment.setTargetFragment(this, SAVE_DIALOG_FRAGMENT)
        activity?.let { dialogFragment.show(it.supportFragmentManager, "hello") }
    }

    override fun setBuildData(build: BuildData) {
        tvName.text = build.name
        tvPrice.text = App.app.getString(R.string.component_price, build.price)
        tvDesc.text = build.description

        for ((category, value) in build.components) {
            addNewComponent(category, value)
        }
    }

    override fun setImage(image: Bitmap) {
        imgBuild.setImageBitmap(image)
    }

    override fun setImage(url: String) {
        Picasso.get().load(url).into(imgBuild)
    }

    override fun setStatus(status: BuildConstructorPresenter.StatusBuild, message: String?) {
        when(status) {
            BuildConstructorPresenter.StatusBuild.COMPATIBILITY_ERROR -> {
                tvStatus.text = resources.getString(R.string.not_compatibility)
                tvCompatibility.text = message
            }
            BuildConstructorPresenter.StatusBuild.IS_NOT_COMPLETE -> {
                tvStatus.text = resources.getString(R.string.not_complete)
            }
            BuildConstructorPresenter.StatusBuild.COMPLETE -> {
                tvStatus.text = resources.getString(R.string.complete)
            }
        }
    }

    override fun updatePrice(price: Float) {
        tvPrice.text = App.app.resources.getString(R.string.component_price, price)
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private fun createComponentCard(component: Component, category: ComponentCategory, container: LinearLayout) {
        val card = layoutInflater.inflate(R.layout.inflate_component_item, container, false) as LinearLayout

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
        val ibtDelete = card.component_item_bt_corner
        ibtDelete.setOnClickListener {
            presenter.removeComponent(category, component)
            container.removeView(card) // Удаление комплектующего
            container.getChildAt(0).visibility = View.VISIBLE // Кнопка добавить становится активной
        }
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, App.app.theme)) // Отрисовка значка

        card.setOnClickListener {
            openComponentInfo(component)
        }

        //TODO реализовать в будущем
        // Если это ОЗУ или внешняя память - открывается блок для изменения количества в сборке
//        if (currentBuild!!.isMultipleGood(typeGoodBlank)) {
//            card.findViewById<View>(R.id.inflate_good_blank_ll_count).visibility = View.VISIBLE
//            val tvCount = card.findViewById<TextView>(R.id.inflate_good_blank_tv_count)
//            tvCount.text = Integer.toString(currentBuild!!.getCountComponents(typeGoodBlank))
//            card.findViewById<View>(R.id.inflate_good_blank_bt_increase_count).setOnClickListener { view -> countChange(view, typeGoodBlank, tvCount) }
//            card.findViewById<View>(R.id.inflate_good_blank_bt_reduce_count).setOnClickListener { view -> countChange(view, typeGoodBlank, tvCount) }
//        }

        // Скрытие кнопки "+" (явлеяется первым элементом в LinearLayout)
        container.getChildAt(0).visibility = View.GONE
        container.addView(card)
    }

    override fun addNewComponent(category: ComponentCategory, component: Component) {
        componentContainers[category]?.let {
            val container: LinearLayout = currentFragment.findViewById(it)
            createComponentCard(component, category, container)
        }
    }

    override fun exitView() {
        frListener.popBackStack()
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////

    /**
     * Открытие/скрытие списка комплектуюзих в определенной категории
     */
    private fun toggleCompListVisibility(view: View) {
        // Необходимый список находится сразу под кнопкой, потому сначала находится индекс кнопки и потом спиок как <индекс кнопки> + 1
        val index = currentFragment.fragment_build_ll_component_list.indexOfChild(view)
        val blockList = currentFragment.fragment_build_ll_component_list.getChildAt(index + 1) as LinearLayout

        if (blockList.visibility == View.GONE) {
            blockList.visibility = View.VISIBLE
            (view as Button).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
        } else {
            blockList.visibility = View.GONE
            (view as Button).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_36, 0)
        }
    }

    /**
     * Обработчик кнопки "+" для выбора комплектующих в сборку
     */
    private fun pickComponent(category: ComponentCategory) {
        presenter.selectCategoryToSearch(category)
    }

    private fun openComponentInfo(component: Component) {
        presenter.selectComponentToVIew(component)
        frListener.nextFragment(this, SearchFragment(), null, BackStackTag.COMPONENT_SEARCH)
    }

//    // Уменьшение или увелечение количества комплектующих одного типа в сборке (Доступно только для SSD, HDD, RAM)
//    fun countChange(view: View, typeComp: TypeComp?, tvCount: TextView) {
//        when (view.id) {
//            R.id.inflate_good_blank_bt_increase_count -> currentBuild!!.increaseCountGoods(typeComp)
//            R.id.inflate_good_blank_bt_reduce_count -> currentBuild!!.reduceCountGoods(typeComp)
//        }
//        tvCount.text = Integer.toString(currentBuild!!.getCountComponents(typeComp))
//        setHeaderData()
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.build_full_menu_bottom_navigator_action_brain_com -> {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://brain.com.ua/"))
//                startActivity(browserIntent)
//            }
//            R.id.build_full_menu_bottom_navigator_action_save -> {
//                isSaved = true
//                UserData.userData.saveCurrentBuild() // Сохранение сборки перед выходом
//                Toast.makeText(App.app.applicationContext, "Сохранено", Toast.LENGTH_SHORT).show()
//                currentBuild = UserData.userData.currentBuild
//            }
//        }
//        return false
//    }
//

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //TODO
    // Может необходимо производить контроль и ограничить количество символов для имени сборки
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        // Нахождение нужного EditText
        val view = activity?.currentFocus
        val text = (view as EditText?)?.text.toString()

        // Определение того, какое поле меняет юзер
        if (view?.id == R.id.fragment_build_et_name)
            presenter.setName(text)
        else if (view?.id == R.id.fragment_build_et_desc) {
            presenter.setDescription(text)
        }
    }

}