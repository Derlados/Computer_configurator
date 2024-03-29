package com.derlados.computer_configurator.ui.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.entities.build.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.ui.decorators.AnimOnTouchListener
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_build_item.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class  BuildRecyclerAdapter <T : Build> (protected open val builds: ArrayList<T>, protected open val onItemClick: (id: String) -> Unit,
                                              protected open val removeItem: (id: String) -> Unit, protected open val publishBuild: (id: String) -> Unit):
        RecyclerView.Adapter<BuildRecyclerAdapter.BuildHolder>() {

    class BuildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComponents: HashMap<ComponentCategory, TextView> = hashMapOf(
                ComponentCategory.CPU to itemView.inflate_build_item_tv_chosen_cpu,
                ComponentCategory.MOTHERBOARD to itemView.inflate_build_item_tv_chosen_mb,
                ComponentCategory.GPU to itemView.inflate_build_item_tv_chosen_gpu,
                ComponentCategory.RAM to itemView.inflate_build_item_tv_chosen_ram,
                ComponentCategory.HDD to itemView.inflate_build_item_tv_chosen_hdd,
                ComponentCategory.SSD to itemView.inflate_build_item_tv_chosen_ssd,
                ComponentCategory.POWER_SUPPLY to itemView.inflate_build_item_tv_chosen_power_supply,
                ComponentCategory.CASE to itemView.inflate_build_item_tv_chosen_case,
        )

        val tvName: TextView = itemView.inflate_build_item_tv_name
        val tvPrice: TextView = itemView.inflate_build_item_tv_price
        val tvUserOrStatusValue: TextView = itemView.inflate_build_item_tv_status_or_user_value
        val tvUserOrStatusHeader: TextView = itemView.inflate_build_item_tv_status_or_user_head

        val img: ImageView = itemView.inflate_build_item_img
        val llActionBtns: LinearLayout = itemView.inflate_build_item_ll_action_btns
        val btPublish: ImageView = itemView.inflate_build_item_ibt_publish
        val btDelete: ImageButton = itemView.inflate_build_item_ibt_delete
        val btShare: ImageButton = itemView.inflate_build_item_ibt_share
        val btReport: ImageButton = itemView.inflate_build_item_ibt_report
        val tvPublishDate: TextView = itemView.inflate_build_item_tv_date
        val btComponentList: ImageButton = itemView.inflate_build_item_ibt_hide
        val llComponentList: ExpandableLinearLayout = itemView.inflate_build_item_component_list

        init {
            btComponentList.setOnClickListener {
                if (llComponentList.currentPosition != 0) {
                    btComponentList.setImageResource(R.drawable.ic_arrow_down_36)
                } else {
                    btComponentList.setImageResource(R.drawable.ic_arrow_up_36)
                }

                llComponentList.toggle()
            }
        }


        /**
         * Инициализация ExpandedLayout. По сути является костылем, так как пересчет размера
         * сложного контейнера выполняется не сразу - необходимо выполнить его с задержкой. Так же
         * появляется баг при первом перемыкании, потому необходимо его вызвать без анимации1
         */
        fun initExpandLayout() {
            Handler(getMainLooper()).postDelayed({
                llComponentList.initLayout()
                llComponentList.setDuration(0)
                llComponentList.toggle()
                llComponentList.setDuration(300)
            }, 100)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_build_item, parent, false)
        return BuildHolder(itemView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BuildHolder, position: Int) {
        val build: Build = builds[position]

        holder.btShare.visibility = View.GONE
        holder.tvName.text = build.name
        holder.tvPrice.text = App.app.getString(R.string.component_price, build.price)

        // Сообщение о статусе готовности и совместимости сборки
        holder.tvUserOrStatusHeader.setText(R.string.status)
        if (!build.isCompatibility) {
            holder.tvUserOrStatusValue.text = App.app.resourceProvider.getString(ResourceProvider.ResString.NOT_COMPATIBILITY)
            holder.tvUserOrStatusValue.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.RED))
        } else if (!build.isComplete) {
            holder.tvUserOrStatusValue.text = App.app.resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE)
            holder.tvUserOrStatusValue.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.RED))
        } else {
            holder.tvUserOrStatusValue.text = App.app.resourceProvider.getString(ResourceProvider.ResString.COMPLETE)
            holder.tvUserOrStatusValue.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.GREEN))
        }

        // Список комплектующих присутствующих в сборках
        build.image?.let {
            Picasso.get().load(it).into(holder.img)
        }
        for ((key, value) in holder.tvComponents) {
            val buildComponents = build.components[key]
            if (buildComponents != null) {
                for (i in 0 until buildComponents.size) {
                    value.text = buildComponents[i].component.name
                }
            } else {
                value.text = App.app.applicationContext.getString(R.string.not_chosen)
            }
        }

        // Настройка изображения статуса публикации
        if (build.isPublic) {
            holder.btPublish.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, R.drawable.ic_internet_on_24, App.app.theme))
            holder.btPublish.setOnClickListener(null)
        } else {
            holder.btPublish.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, R.drawable.ic_internet_off_24, App.app.theme))
            holder.btPublish.setOnClickListener { publishBuild(build.localId) }
        }

        // Настройка обработчиков нажаатий
        holder.itemView.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
            onItemClick(build.localId)
            return@OnTouchListener true
        }))

        holder.btDelete.setOnClickListener {
            removeItem(build.localId)
        }

        holder.initExpandLayout()
    }

    override fun getItemCount(): Int = builds.size
}