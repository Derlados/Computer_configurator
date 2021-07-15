package com.derlados.computer_conf.views.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.managers.FileManager
import com.derlados.computer_conf.models.Component
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.inflate_component_item.view.*
import java.lang.Exception

class ComponentRecyclerAdapter(private  val components: List<Component>, private val trackPrices: HashMap<Int, Int>, private val defaultImageId: Int,
                               private val onItemClicked: (Component) -> Unit, private val onBtFavoriteClick: (Int) -> Unit)
    : RecyclerView.Adapter<ComponentRecyclerAdapter.ComponentHolder>() {

    class ComponentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.component_item_tv_name
        val tvPrice: TextView = itemView.component_item_tv_price
        val img: ImageView = itemView.component_item_img
        val btFavorite: ImageButton = itemView.inflate_component_item_bt_favorite

        val llMonitoring: LinearLayout = itemView.inflate_component_item_ll_track_block
        val tvTrackPrice: EditText = itemView.inflate_component_item_et_track_price

        val tvHeaders: ArrayList<TextView> = ArrayList()
        val tvValues: ArrayList<TextView> = ArrayList()

        init {
            itemView.findViewById<TableLayout>(R.id.component_item_tl_data)

            // Опеределение полей атрибутов и их значения (четный индекс - значение, нечетный - атрибут)
            val tlData: TableLayout = itemView.component_item_tl_data
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
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_component_item, parent, false);
        return ComponentHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComponentHolder, position: Int) {
        val component: Component = components[position]

        holder.tvName.text = component.name
        holder.tvPrice.text = App.app.resources.getString(R.string.component_price, component.price)

        // Если изображение не было закешировано, будет скачано новое и сохранено. Пока изображения нету - устанавливается по умолчанию

        Picasso.get().load(component.imageUrl).into(holder.img, object : Callback {
            override fun onSuccess() { }

            override fun onError(e: Exception?) {
                holder.img.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, defaultImageId, App.app.theme))
            }
        })

        val attributes: List<Component.Attribute> = component.getPreviewAttributes()
        for (i in attributes.indices) {
            holder.tvHeaders[i].text = attributes[i].name
            holder.tvValues[i].text = attributes[i].value
        }

        holder.itemView.setOnClickListener {
            onItemClicked(component)
        }

        holder.btFavorite.setOnClickListener {
            onBtFavoriteClick(component.id)
        }
        if (trackPrices.containsKey(component.id)) {
            holder.btFavorite.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, R.drawable.ic_active_star_24, App.app.theme))
        } else {
            holder.btFavorite.setImageDrawable(ResourcesCompat.getDrawable(App.app.resources, R.drawable.ic_inactive_star_24, App.app.theme))
        }

        trackPrices[component.id]?.let {
            holder.llMonitoring.visibility = View.VISIBLE
            holder.tvTrackPrice.setText(it.toString())
        }
    }

    override fun getItemCount(): Int = components.size
}
