package com.derlados.computer_conf.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.BuildData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_build_item.view.*

class  BuildRecyclerAdapter <T : BuildData> (private val builds: ArrayList<T>, private val onItemClick: (id: String) -> Unit, private val removeItem: (id: String) -> Unit):
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
        val tvStatus: TextView = itemView.inflate_build_item_tv_price
        val img: ImageView = itemView.inflate_build_item_img
        val btDelete: ImageButton = itemView.inflate_build_item_ibt_delete
        private val btComponentList: ImageButton = itemView.inflate_build_item_ibt_hide
        private val llComponentList: LinearLayout = itemView.inflate_build_item_component_list

        init {
            btComponentList.setOnClickListener {
                toggleComponentList()
            }
        }

        private fun toggleComponentList() {
            if (llComponentList.visibility == View.GONE) {
                llComponentList.visibility = View.VISIBLE
            } else {
                llComponentList.visibility = View.GONE
            }
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
        //TODO holder.tvStatus
        build.components[ComponentCategory.CASE]?.let {
            Picasso.get().load(it.imageUrl).into(holder.img)
        }

        for ((key, value) in holder.tvComponents) {
            build.components[key]?.let {
                value.text = it.name
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(build.id)
        }
        holder.btDelete.setOnClickListener {
            removeItem(build.id)
        }
    }

    override fun getItemCount(): Int = builds.size
}