package com.derlados.computer_conf.vIews.adapters

import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.R
import com.derlados.computer_conf.models.Component
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.component_item.view.*
import org.w3c.dom.Attr

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
            val tlData: TableLayout = itemView.findViewById(R.id.component_item_tl_data)
            for (i in 1..tlData.childCount) {
                val row: TableRow = tlData.getChildAt(i) as TableRow

                if (i % 2 == 0) {
                    for (j in 1..row.childCount)
                        tvValues.add(row.getChildAt(j) as TextView)
                } else {
                    for (j in 1..row.childCount)
                        tvHeaders.add(row.getChildAt(j) as TextView)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.component_item, parent, false);
        return ComponentHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComponentHolder, position: Int) {
        val component: Component = components[position]

        holder.tvName.text = component.name
        holder.tvPrice.text = component.price.toString()
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


//    /* Обработчик нажатий. Есть два места для обработки - один из бланков комплектующего и панель прокрутки страниц
//    * Нажатие на бланк - вызов подробной информации о комплектующем в новом фрагменте
//    * Нажатие на панель страниц - загрузка страницы в соответствии с выбором пользователя
//    * */
//    fun onClick(view: View) {
//        when (view.id) {
//            R.id.inflate_good_blank_rl_blank -> {
//                // Отправка объекта в следующий фрагмет для отображения полной информации о нем
//                val data = Bundle()
//                val gson = Gson()
//                val sendGood = goodsList[goodsContainer.indexOfChild(view)] // Объект получается по индексу вьюшки бланка в списке
//                data.putString("good", gson.toJson(sendGood)) // Объект передается в виде json строки
//                data.putSerializable("typeGood", category)
//                //fragmentListener.nextFragment(this, FullGoodDataFragment(), data, null)
//            }
//            R.id.inflate_flip_page_navigator_ibt_next -> downloadPage(category, ComponentSearchFragment.Direction.NEXT, null)
//            R.id.inflate_flip_page_navigator_ibt_back -> downloadPage(category, ComponentSearchFragment.Direction.BACK, null)
//            else -> {
//                val numPage = (view as TextView).text.toString()
//                // Если нажатое поле является "..", это событие никак не должно обрабатываться
//                if (numPage != "..") downloadPage(category, ComponentSearchFragment.Direction.CHOSEN_PAGE, numPage.toInt())
//            }
//        }
//    }
}
