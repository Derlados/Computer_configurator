package com.derlados.computer_conf.views.pageFragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.derlados.computer_conf.App
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import com.derlados.computer_conf.models.Build
import com.derlados.computer_conf.models.UserData
import com.derlados.computer_conf.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BuildsFragment : PageFragment(), View.OnClickListener {
    private var frListener: OnFragmentInteractionListener? = null
    private var fragment: View? = null
    private var buildsContainer: LinearLayout? = null // Основной контейнер для бланков сборок

    // Данные для модификации после возврата с режима сборки
    private var blankToModify: LinearLayout? = null
    private var addToParent: Boolean = false // true - новая сборка и её необходимо добавить, false - старая и надо лишь обновить = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragment = inflater.inflate(R.layout.fragment_builds, container, false)

        buildsContainer = this.fragment?.findViewById(R.id.fragment_builds_builds_container)

        // Кнопка в левом нижнем углу экрана для создания новых сборок
        val addBuildBt: FloatingActionButton = this.fragment?.findViewById(R.id.fragment_builds_float_bt)!!
        addBuildBt.setOnClickListener(this)

        // TODO (Тут был хендлер. Создание списка сохраненных сборок (последняя изменяемая сборка является текущей, потому находится сверху списка)

        return fragment
    }

    //TODO(Логика презентера и модели)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        // Если сборка модифицировалась - данные обновляются, если сборка создавалась - добавляется в список
        if (!hidden && blankToModify != null) {

            // Если сборка только создавалась и пользователь её не захотел сохранить - все данные уничтожаются и сборка не добавляется
            if (addToParent && !UserData.isCurrentBuildIsSaved) {
                UserData.discardCurrentBuild(true) // Когда пользователь выходит в меню, текущая сборка сбрасывается
                blankToModify = null
                return
            }

            if (UserData.isCurrentBuildIsSaved)
                setBuildBlank(UserData.currentBuild!!, blankToModify!!, addToParent)
            UserData.discardCurrentBuild(false) // Когда пользователь выходит в меню, текущая сборка сбрасывается
            blankToModify = null
        }
    }

    /* Создание бланка
    * Параметры:
    * build - сборка с которой берутся данные
    * buildBlank - бланк (контейнер LinearLayout) в котором будут происходить изменения
    * */
    @SuppressLint("SetTextI18n")
    private fun setBuildBlank(build: Build, buildBlank: LinearLayout, addToParent: Boolean) {

        // Установка превью бланка сборки
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_name) as TextView).text = build.name
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_price) as TextView).text = "${build.price} грн"
        val tvComplete = buildBlank.findViewById<TextView>(R.id.inflate_build_blank_tv_complete)
        if (build.isComplete) {
            tvComplete.text = resources.getString(R.string.complete)
            tvComplete.setTextColor(resources.getColor(R.color.green, App.app.theme))
        } else {
            tvComplete.text = resources.getString(R.string.not_complete)
            tvComplete.setTextColor(resources.getColor(R.color.red, App.app.theme))
        }

        // Установка комплектующих в раскрывающемся списке (названия комплектующих)
        buildBlank.findViewById<TextView>(R.id.inflate_build_blank_tv_chosen_CPU).text = getGoodStr(build, ComponentCategory.CPU)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_GPU) as TextView).text = getGoodStr(build, ComponentCategory.GPU)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_motherboard) as TextView).text = getGoodStr(build, ComponentCategory.MOTHERBOARD)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_RAM) as TextView).text = getGoodStr(build, ComponentCategory.RAM)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_HDD) as TextView).text = getGoodStr(build, ComponentCategory.HDD)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_SSD) as TextView).text = getGoodStr(build, ComponentCategory.SSD)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_power_supply) as TextView).text = getGoodStr(build, ComponentCategory.POWER_SUPPLY)
        (buildBlank.findViewById<View>(R.id.inflate_build_blank_tv_chosen_case) as TextView).text = getGoodStr(build, ComponentCategory.CASE)

        // Кнопка для сворачивания/раскрытия списка комлектующих в сборке
        buildBlank.findViewById<View>(R.id.inflate_build_blank_ibt_hide).setOnClickListener { view ->
            val previewInfo = buildBlank.findViewById<LinearLayout>(R.id.inflate_build_blank_ll_preview_info)
            val ibt = view as ImageButton
            if (previewInfo.visibility == View.GONE) {
                previewInfo.visibility = View.VISIBLE
                ibt.setImageResource(R.drawable.ic_arrow_up_36)
            } else {
                previewInfo.visibility = View.GONE
                ibt.setImageResource(R.drawable.ic_arrow_down_36)
            }
        }

        // Обработка на нажатие по всему бланку
        buildBlank.findViewById<View>(R.id.inflate_build_blank_ll_preview_info_header).setOnClickListener(this)
        buildBlank.findViewById<View>(R.id.inflate_build_blank_ibt_delete).setOnClickListener {
            UserData.deleteBuildByIndex(buildsContainer!!.indexOfChild(buildBlank)) // Индекс опеределяется текущим положением бланка в списке
            buildsContainer!!.removeView(buildBlank) // Удаление бланка
        }

        // Установка изображения сборки
        if (build.getComponent(ComponentCategory.CASE) != null) {
            val caseImg = build.getComponent(ComponentCategory.CASE)?.image
            (buildBlank.findViewById<View>(R.id.inflate_build_blank_img) as ImageView).setImageBitmap(caseImg)
        } else (buildBlank.findViewById<View>(R.id.inflate_build_blank_img) as ImageView).setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_case_24, App.app.theme))

        // Добавление бланка
        if (addToParent) buildsContainer!!.addView(buildBlank, buildsContainer!!.childCount - 1)
    }

    // Формирование строки списка комплектующих
    private fun getGoodStr(build: Build, componentCategory: ComponentCategory): String {
        val good = build.getComponent(componentCategory)
        return good?.name ?: "--"
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fragment_builds_float_bt -> {
                UserData.addNewBuild()
                blankToModify = layoutInflater.inflate(R.layout.inflate_build_blank, buildsContainer, false) as LinearLayout
                addToParent = true
                //TODO (Раскомментировать когда появится фрагмент)
              //  frListener!!.nextFragment(this, BuildFullFragment(), null, "Build")
            }
            R.id.inflate_build_blank_ll_preview_info_header -> {
                val listBuildsContainer = fragment!!.findViewById<LinearLayout>(R.id.fragment_builds_builds_container)
                val index = listBuildsContainer.indexOfChild(view.parent as LinearLayout)

                // Получение данных для будущей модификации
                blankToModify = view.parent as LinearLayout
                addToParent = false
                UserData.setCurrentBuild(index) // Выбор идет по индексу положения в списке
                //TODO (Раскомментировать когда появится фрагмент)
               // frListener!!.nextFragment(this, BuildFullFragment(), null, "Build")
            }
        }
    }
}