package com.derlados.computer_conf.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.models.Component
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_component_item.view.*

class ComponentRecyclerAdapter(private  val components: List<Component>, private val onItemClicked: (Component) -> Unit):
        RecyclerView.Adapter<ComponentRecyclerAdapter.ComponentHolder>() {

    class ComponentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView
        val tvPrice: TextView
        val img: ImageView
        val tvHeaders: ArrayList<TextView> = ArrayList()
        val tvValues: ArrayList<TextView> = ArrayList()

        init {
            itemView.findViewById<TableLayout>(R.id.component_item_tl_data)
            tvName = itemView.findViewById(R.id.component_item_tv_name)
            tvPrice = itemView.findViewById(R.id.component_item_tv_price)
            img = itemView.findViewById(R.id.component_item_img)

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
        Picasso.get().load(component.imageUrl).into(holder.img)

        val attributes: List<Component.Attribute> = component.getPreviewAttributes()
        for (i in attributes.indices) {
            holder.tvHeaders[i].text = attributes[i].name
            holder.tvValues[i].text = attributes[i].value
        }

        holder.itemView.setOnClickListener {
            onItemClicked(component)
        }
    }

    override fun getItemCount(): Int = components.size

}
