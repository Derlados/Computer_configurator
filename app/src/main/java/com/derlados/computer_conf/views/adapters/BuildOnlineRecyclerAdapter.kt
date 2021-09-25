package com.derlados.computer_conf.views.adapters

import android.annotation.SuppressLint
import android.view.View
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.models.entities.BuildData
import com.derlados.computer_conf.views.decorators.AnimOnTouchListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class BuildOnlineRecyclerAdapter<T : BuildData>(override val builds: ArrayList<T>, val onServerBuildChoose: (id: Int) -> Unit):
        BuildRecyclerAdapter<T>(builds, fun(_: String) = Unit, fun(_: String) = Unit, fun(_: String) = Unit) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BuildHolder, position: Int) {
        val build: BuildData = builds[position]

        // Установка всех заголовочных данных
        holder.tvName.text = build.name
        holder.tvPrice.text = App.app.getString(R.string.component_price, build.price)
        holder.tvUserOrStatusHeader.setText(R.string.creator)
        holder.tvUserOrStatusValue.text = build.username
        holder.llActionBtns.visibility = View.GONE
        holder.tvPublishDate.visibility = View.VISIBLE

        val formatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        holder.tvPublishDate.text = formatter.format(build.publishDate)

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
        holder.itemView.setOnTouchListener(AnimOnTouchListener(View.OnTouchListener { _, _ ->
            onServerBuildChoose(build.serverId)
            return@OnTouchListener true
        }))

        holder.initExpandLayout()
    }
}