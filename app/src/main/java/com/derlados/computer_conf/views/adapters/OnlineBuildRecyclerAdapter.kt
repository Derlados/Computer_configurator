package com.derlados.computer_conf.views.adapters

import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.models.BuildData
import com.squareup.picasso.Picasso

class OnlineBuildRecyclerAdapter <T : BuildData> (override val builds: ArrayList<T>, override val onItemClick: (id: String) -> Unit):
        BuildRecyclerAdapter<T>(builds, onItemClick, fun(_:String) = Unit, fun(_:String) = Unit) {

    override fun onBindViewHolder(holder: BuildHolder, position: Int) {
        val build: BuildData = builds[position]

        // Установка всех заголовочных данных
        holder.tvName.text = build.name
        holder.tvPrice.text = App.app.getString(R.string.component_price, build.price)
        holder.tvUserOrStatusHeader.setText(R.string.user)
        holder.tvUserOrStatusValue.text = build.username
        holder.tvPublishDate.text = build.publishDate

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

        // Настройка обработчиков нажаатий
        holder.itemView.setOnClickListener {
            onItemClick(build.id)
        }

        holder.initExpandLayout()
    }
}