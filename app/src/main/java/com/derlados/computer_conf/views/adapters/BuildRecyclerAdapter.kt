package com.derlados.computer_conf.views.adapters

import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_build_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class  BuildRecyclerAdapter <T : BuildData> (private val builds: ArrayList<T>, private val onItemClick: (id: String) -> Unit,
                                             private val removeItem: (id: String) -> Unit, private val changePublicStatus: (id: String) -> Unit):
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
        val tvStatus: TextView = itemView.inflate_build_item_tv_status
        val img: ImageView = itemView.inflate_build_item_img
        val btPublish: ImageView = itemView.inflate_build_item_ibt_publish
        val btDelete: ImageButton = itemView.inflate_build_item_ibt_delete
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

    override fun onBindViewHolder(holder: BuildHolder, position: Int) {
        val build: BuildData = builds[position]

        holder.tvName.text = build.name
        holder.tvPrice.text = App.app.getString(R.string.component_price, build.price)

        // Сообщение о статусе готовности и совместимости сборки
        if (!build.isCompatibility) {
            holder.tvStatus.text = App.app.resourceProvider.getString(ResourceProvider.ResString.NOT_COMPATIBILITY)
            holder.tvStatus.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.RED))
        } else if (!build.isComplete) {
            holder.tvStatus.text = App.app.resourceProvider.getString(ResourceProvider.ResString.NOT_COMPLETE)
            holder.tvStatus.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.RED))
        } else {
            holder.tvStatus.text = App.app.resourceProvider.getString(ResourceProvider.ResString.COMPLETE)
            holder.tvStatus.setTextColor(App.app.resourceProvider.getColor(ResourceProvider.ResColor.GREEN))
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
        } else {
            holder.btPublish.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, R.drawable.ic_internet_off_24, App.app.theme))
        }

        // Настройка обработчиков нажаатий
        holder.itemView.setOnClickListener {
            onItemClick(build.id)
        }
        holder.btDelete.setOnClickListener {
            removeItem(build.id)
        }
        holder.btPublish.setOnClickListener {
            changePublicStatus(build.id)
        }

        holder.initExpandLayout()
    }

    override fun getItemCount(): Int = builds.size
}