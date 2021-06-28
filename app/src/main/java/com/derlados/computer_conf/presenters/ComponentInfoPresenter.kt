package com.derlados.computer_conf.presenters

import android.widget.Toast
import com.derlados.computer_conf.App
import com.derlados.computer_conf.interfaces.ComponentInfoView
import com.derlados.computer_conf.models.ComponentModel

class ComponentInfoPresenter(private val view: ComponentInfoView) {

    fun init() {
        view.setComponentInfo(ComponentModel.chosenComponentToView)
        view.initMarkBt("Add to favorite", ::addToFavorite)

        //TODO("Нужно проверять выбирает ли юзер комплектующее в сборку")
       // view.initMarkBt("Add to build", ::addToBuilds)
    }


    private fun addToFavorite() {
        Toast.makeText(App.app.applicationContext, "favorite", Toast.LENGTH_SHORT).show()
       // TODO("Сделать избранное")
    }

    private fun addToBuilds() {
       // TODO("Вернуть добавление в сборку")
    }

}