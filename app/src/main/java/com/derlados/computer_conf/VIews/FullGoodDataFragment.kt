package com.derlados.computerconf.VIews

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.derlados.computerconf.App
import com.derlados.computerconf.Constants.TypeComp
import com.derlados.computerconf.Internet.RequestAPI
import com.derlados.computerconf.Objects.Component
import com.derlados.computerconf.Objects.UserData
import com.derlados.computerconf.R
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class FullGoodDataFragment : Fragment(), View.OnClickListener {
    private var currentComponent // Текущий товар который отображается
            : Component? = null
    private var dataContainer // Контейнер в который помещается все характеристики товара
            : LinearLayout? = null
    private var typeComp // Тип товара
            : TypeComp? = null
    private var fragmentListener: OnFragmentInteractionListener? = null
    private var userData: UserData? = null
    private var currentFragment: View? = null
    private val ADD_TO_BUILD = 0
    private val NOTHING = 1
    private val ADD_TO_FAVORITE = 2
    private var clickAction // Переменной присваивается значение одной из констант в зависмость от который будет происходить то или иное действие = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentFragment = inflater.inflate(R.layout.fragment_full_data, container, false)
        userData = UserData.userData
        if (arguments != null) {
            val jsonGood = arguments!!.getString("good")
            currentComponent = Gson().fromJson<Component>(jsonGood, Component::class.java)
            dataContainer = currentFragment.findViewById(R.id.fragment_full_data_main_container)
            typeComp = arguments!!["typeGood"] as TypeComp?
        }
        setPreviewData() // Установка превью характеристик

        // Если данных полных нету - они загружаются, иначе можно сразу же их отрисовывать
        if (currentComponent!!.fullData == null) downloadFullData() else setFullData()
        return currentFragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun downloadFullData() {
        GoodFullDataDownloader().execute(currentComponent)
    }

    private fun setPreviewData() {
        // Установка изображения (Picasso кеширует запросы, так что если ранее было загружено изображение - оно возьмется из кеша)
        val imageView = currentFragment!!.findViewById<ImageView>(R.id.fragment_full_data_img)
        Picasso.get().load(currentComponent!!.imageUrl).into(imageView, object : Callback {
            override fun onSuccess() {
                currentComponent!!.image = (imageView.drawable as BitmapDrawable).bitmap
            }

            override fun onError(e: Exception) {}
        })
        (currentFragment!!.findViewById<View>(R.id.fragment_full_data_name) as TextView).text = currentComponent!!.name
        (currentFragment!!.findViewById<View>(R.id.fragment_full_data_price) as TextView).setText(String.format(Locale.getDefault(), "%.2f ГРН", currentComponent!!.price))
    }

    private fun setFullData() {
        val fullData = currentComponent!!.fullData
        for (i in fullData.indices) {
            val header = layoutInflater.inflate(R.layout.inflate_full_data_block_header, dataContainer, false) as TextView
            header.text = fullData[i].header
            dataContainer!!.addView(header)

            // Строка характеристики (Имя:значение)
            for ((key, value) in fullData[i].data) {
                val dataDesc = layoutInflater.inflate(R.layout.inflate_goods_datablock_description_string, dataContainer, false) as LinearLayout
                (dataDesc.getChildAt(0) as TextView).text = key // Установка имени
                (dataDesc.getChildAt(1) as TextView).text = value // Установка значения характеристики
                dataContainer!!.addView(dataDesc)
            }
        }

        // Скрытие прогресс баров
        currentFragment!!.findViewById<View>(R.id.fragment_full_data_pb_data).visibility = View.GONE
        currentFragment!!.findViewById<View>(R.id.fragment_full_data_pb_button).visibility = View.GONE
        val btAction = currentFragment!!.findViewById<Button>(R.id.fragment_full_data_bt_add_to_build)
        // Если сборки текущей нету, значит пользователь просто смотрит каталог
        // Если сборка есть, но текущего комплектующего нету, значит идет выбор товара для сборки
        // В другом случае пользователь просто смотрит описание того комплектующего, что он выбрал
        clickAction = if (userData!!.currentBuild == null) {
            btAction.setText(R.string.add_favorite)
            ADD_TO_FAVORITE
        } else if (userData!!.currentBuild.getGood(typeComp) == null) {
            btAction.setText(R.string.add_to_build)
            ADD_TO_BUILD
        } else {
            btAction.setText(R.string.in_build)
            NOTHING
        }
        btAction.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (clickAction) {
            ADD_TO_BUILD -> {
                userData!!.currentBuild.addToBuild(typeComp, currentComponent)
                Toast.makeText(App.app.applicationContext, "Добавлено в сборку", Toast.LENGTH_SHORT).show()
                fragmentListener!!.popBackStack("Build")
            }
            ADD_TO_FAVORITE -> {
            }
            NOTHING -> {
            }
        }
    }

    // Класс для загрузки информации
    inner class GoodFullDataDownloader : AsyncTask<Component?, Int?, String?>() {
        var retrofit: Retrofit? = null
        var requestAPI: RequestAPI? = null
        override fun onPreExecute() {
            super.onPreExecute()
            // Для работы с сетью
            retrofit = Retrofit.Builder()
                    .baseUrl("http://buildpc.netxisp.host")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            requestAPI = retrofit.create<RequestAPI>(RequestAPI::class.java)
        }

        /* Прорисовка элементов
         * SET_GOODS - прорисовка комплектующих
         * SET_FLIP_PAGER - прорисовка
         * */
        protected override fun onProgressUpdate(vararg values: Int) {
            super.onProgressUpdate(*values)
        }

        // Получение полной информации о комплектующем, если их нету. Параметры: 0 - тип комплектующего
        protected override fun doInBackground(vararg values: Component): String? {
            val good = values[0]
            if (good.fullData == null) {
                val call = requestAPI!!.getGoodFullData(good.urlFullData)
                try {
                    val response = call.execute()
                    if (response.isSuccessful) good.fullData = response.body()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        // Отрисовка полной информации
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            setFullData()
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }
}