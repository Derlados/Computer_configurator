package com.derlados.computerconf.VIews

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.derlados.computerconf.Constants.TypeComp
import com.derlados.computerconf.Internet.GsonSerializers.HashMapDeserializer
import com.derlados.computerconf.Internet.RequestAPI
import com.derlados.computerconf.MainActivity.OnBackPressedListener
import com.derlados.computerconf.Objects.Component
import com.derlados.computerconf.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ShopSearchFragment : Fragment(), View.OnClickListener, OnBackPressedListener {
    private var keepVisible = true

    // Направление движения по страницам
    private enum class Direction {
        NEXT, BACK, START, CURRENT, CHOSEN_PAGE
    }

    private var currentPage = 1
    private var maxPages = 0
    private var goodsDownloader: GoodsDownloader? = null
    private var goodsContainer // XML контейнер (лаяут) в который ложаться все товары
            : LinearLayout? = null
    private var typeComp // Тип комплектующего на текущей странице
            : TypeComp? = null
    private var goodsList = ArrayList<Component>() // Список с комплектующими
    private val blanks = ArrayList<RelativeLayout>() // Список бланков комплектующих
    private var searchString // Поисковая строка
            : EditText? = null
    private var searchText // Текст поисковой строки
            : String? = null
    private var progressBar // Прогресс бар который крутится пока идет скачивание
            : ProgressBar? = null
    private var tvNotFound // Текст с сообщением о том что ничего не найдено
            : TextView? = null
    private var fragmentListener: OnFragmentInteractionListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentFragment = inflater.inflate(R.layout.fragment_shop_search, container, false)
        goodsContainer = currentFragment.findViewById(R.id.fragment_shop_search_goods_container)
        typeComp = arguments!!["typeGood"] as TypeComp?
        progressBar = currentFragment.findViewById(R.id.fragment_shop_search_pb)
        tvNotFound = currentFragment.findViewById(R.id.fragment_shop_search_tv_not_found)


        // Поисковая строка
        searchString = currentFragment.findViewById(R.id.fragment_shop_search_goods_et_search)
        searchString.setOnEditorActionListener(OnEditorActionListener { textView, actionId, keyEvent -> // Реакация на кнопку submit
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Страницы возвращаются к первой и устанавливается сама строка поиска
                maxPages = 0
                currentPage = 1
                searchText = searchString.getText().toString()
                if (searchText == "") // Если строка пустая - передается null
                    searchText = null
                downloadPage(typeComp, Direction.CURRENT, null)
                return@OnEditorActionListener true
            }
            false
        })
        downloadPage(typeComp, Direction.CURRENT, null)
        return currentFragment
    }

    override fun onPause() {
        goodsDownloader!!.cancel(false)
        super.onPause()
    }

    // Решение проблемы с анимацией, по скольку вызывается 2 popBackStack метода то и анимация играет дважды, из-за чего появлялось мерцание
    override fun onBackPressed(): Boolean {
        view!!.visibility = View.VISIBLE
        keepVisible = true
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !keepVisible) view!!.visibility = View.GONE
        keepVisible = false
        super.onHiddenChanged(hidden)
    }

    // Загрузка страницы (загрузка всех превью данных для отображения на странице)
    private fun downloadPage(typeComp: TypeComp?, dir: Direction, page: Int) {
        goodsContainer!!.removeAllViews() // Очистка фрагмента фрагмента
        progressBar!!.visibility = View.VISIBLE // Прогресс бар снова открыт
        tvNotFound!!.visibility = View.GONE

        // Очистка данных
        goodsList.clear()
        blanks.clear()
        when (dir) {
            Direction.BACK -> --currentPage
            Direction.NEXT -> ++currentPage
            Direction.START -> currentPage = 1
            Direction.CHOSEN_PAGE -> currentPage = page
        }
        goodsDownloader = GoodsDownloader()
        goodsDownloader!!.execute(typeComp.toString(), Integer.toString(currentPage), searchText)
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private fun createGoodUI(component: Component) {
        val blank = layoutInflater.inflate(R.layout.inflate_good_blank, goodsContainer, false) as RelativeLayout
        blank.setOnClickListener(this)
        //Взятие основной таблицы информации об комплектующем
        val tableData = blank.findViewById<View>(R.id.inflate_good_blank_tr_data) as TableLayout

        // Установка имени
        val nameText = (tableData.getChildAt(0) as TableRow).getChildAt(0) as TextView
        nameText.text = component.name

        // Установка самих характеристик
        // Preview - таблица 5x2 (1 строка - название, 2 и 4 - характеристики, 3 и 5 - значения)
        val previewData = component.previewData
        val row1 = tableData.getChildAt(1) as TableRow
        val row2 = tableData.getChildAt(2) as TableRow
        val row3 = tableData.getChildAt(3) as TableRow
        val row4 = tableData.getChildAt(4) as TableRow

        // key - характеристика, value - значение
        var count = 0
        for ((key, value) in previewData) {
            if (count < 2) {
                (row1.getChildAt(count) as TextView).text = key
                (row2.getChildAt(count) as TextView).text = value
            } else {
                (row3.getChildAt(count - 2) as TextView).text = key
                (row4.getChildAt(count - 2) as TextView).text = value
            }
            ++count
        }

        // Установка рейтинга и цены
        (blank.findViewById<View>(R.id.inflate_good_blank_tv_price) as TextView).setText(String.format(Locale.getDefault(), "%.2f ГРН", component.price))
        blanks.add(blank)
        goodsContainer!!.addView(blank)
    }

    /* Загрузка панели для выбора страниц и её настройка
    * Вид панели для выбора страницы:
    * 1 и последний элемент - кнопки '<', '>'
    * 2 и предпоследний жлемент - текстовые поля, начальная страница и последняя соответственно
    * остальные - промежуточные страницы
    * */
    private fun createGoodsFlipPager() {
        val flipPager = layoutInflater.inflate(R.layout.inflate_flip_page_navigator, goodsContainer, false) as LinearLayout
        val COUNT_ELEMENTS = 7
        val texts = arrayOfNulls<String>(COUNT_ELEMENTS)
        texts[0] = "1"
        texts[COUNT_ELEMENTS - 1] = Integer.toString(maxPages)

        // Если страниц меньше чем вся полоска - ненужные текст вью скрываются
        if (maxPages <= COUNT_ELEMENTS) {
            for (i in 1..maxPages) (flipPager.getChildAt(i) as TextView).text = Integer.toString(i)
            for (i in maxPages + 1 until flipPager.childCount - 1) flipPager.getChildAt(i).visibility = View.GONE
        }

        // Если текущая страница меньше 5, нужно показать страницы без ".." с левой части
        if (currentPage < 5) {
            for (i in 2..5) texts[i - 1] = Integer.toString(i)
            texts[COUNT_ELEMENTS - 2] = ".."
        } else if (maxPages - currentPage < 4) { // Если текущая страница отличается менее чем на 4 от максимальной, нужно показать страницы без ".." с правой части
            texts[1] = ".."
            for (i in 1..4) texts[COUNT_ELEMENTS - i - 1] = Integer.toString(maxPages - i)
        } else { // если страница в середине всего количества, показывается в стандартном виде с двумя пропусками ".." на обоих сторонах
            texts[1] = ".."
            texts[2] = Integer.toString(currentPage - 1)
            texts[3] = Integer.toString(currentPage)
            texts[4] = Integer.toString(currentPage + 1)
            texts[COUNT_ELEMENTS - 2] = ".."
        }

        // Установка всего текста в соответствующие поля и обработчиков нажатия
        for (i in 1 until flipPager.childCount - 1) {
            val tv = flipPager.getChildAt(i) as TextView
            tv.visibility = View.VISIBLE
            tv.text = texts[i - 1]
            tv.setOnClickListener(this)

            // Выделение страницы которая была выделена соответствующим цветом
            if (texts[i - 1] == Integer.toString(currentPage)) tv.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(context), R.color.main_theme_1))
        }
        flipPager.findViewById<View>(R.id.inflate_flip_page_navigator_ibt_back).setOnClickListener(this)
        flipPager.findViewById<View>(R.id.inflate_flip_page_navigator_ibt_next).setOnClickListener(this)

        // Если текущая страница находится на краю списка (слева или справа), в одноий из кнопок для движения впереж/назад нету необходимости
        if (currentPage == 1) flipPager.removeViewAt(0) else if (currentPage == maxPages) flipPager.removeViewAt(flipPager.childCount - 1)
        goodsContainer!!.addView(flipPager)
    }

    // Загрузка изображений для комплектующих
    fun loadImages() {
        for (i in goodsList.indices) {
            val blank = blanks[i]
            val imageView = blank.findViewById<ImageView>(R.id.inflate_good_blank_img)
            Picasso.get().load(goodsList[i].imageUrl).into(imageView, object : Callback {
                override fun onSuccess() {
                    blank.findViewById<View>(R.id.inflate_good_blank_pb).visibility = View.GONE // Скрытие прогресс бара
                }

                override fun onError(e: Exception) {}
            })
        }
    }

    /* Обработчик нажатий. Есть два места для обработки - один из бланков комплектующего и панель прокрутки страниц
    * Нажатие на бланк - вызов подробной информации о комплектующем в новом фрагменте
    * Нажатие на панель страниц - загрузка страницы в соответствии с выбором пользователя
    * */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.inflate_good_blank_rl_blank -> {
                // Отправка объекта в следующий фрагмет для отображения полной информации о нем
                val data = Bundle()
                val gson = Gson()
                val sendGood = goodsList[goodsContainer!!.indexOfChild(view)] // Объект получается по индексу вьюшки бланка в списке
                data.putString("good", gson.toJson(sendGood)) // Объект передается в виде json строки
                data.putSerializable("typeGood", typeComp)
                fragmentListener!!.nextFragment(this, FullGoodDataFragment(), data, null)
            }
            R.id.inflate_flip_page_navigator_ibt_next -> downloadPage(typeComp, Direction.NEXT, null)
            R.id.inflate_flip_page_navigator_ibt_back -> downloadPage(typeComp, Direction.BACK, null)
            else -> {
                val numPage = (view as TextView).text.toString()
                // Если нажатое поле является "..", это событие никак не должно обрабатываться
                if (numPage != "..") downloadPage(typeComp, Direction.CHOSEN_PAGE, numPage.toInt())
            }
        }
    }

    // Класс для загрузки превью информации о комплектующих
    private inner class GoodsDownloader : AsyncTask<String?, Int?, Boolean>() {
        var retrofit: Retrofit? = null
        var requestAPI: RequestAPI? = null
        val SET_GOODS = 0
        val SET_FLIP_PAGER = 1
        override fun onPreExecute() {
            super.onPreExecute()

            // Создание gson и регистрация собственного сериализатора для HashMap
            val gson = GsonBuilder()
                    .registerTypeAdapter(HashMap::class.java, HashMapDeserializer())
                    .create()

            // Для работы с сетью
            retrofit = Retrofit.Builder()
                    .baseUrl("http://buildpc.netxisp.host")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            requestAPI = retrofit.create<RequestAPI>(RequestAPI::class.java)
        }

        /* Прорисовка элементов
        * SET_GOODS - прорисовка комплектующих
        * SET_FLIP_PAGER - прорисовка
        * */
        protected override fun onProgressUpdate(vararg values: Int) {
            super.onProgressUpdate(*values)
            progressBar!!.visibility = View.GONE // Прогресс скрывается
            val action = values[0]
            if (action == SET_GOODS) {
                for (i in goodsList.indices) {
                    if (isCancelled) return
                    createGoodUI(goodsList[i])
                }
                loadImages()
            } else if (action == SET_FLIP_PAGER) createGoodsFlipPager()
        }

        // Получение всего списка товаров. Параметры: 0 - тип комплектующего, 1 - страница которую необходимо загрузить
        // Так же если необходимо загрузить количество страниц - загружает количество страниц
        protected override fun doInBackground(vararg values: String): Boolean {
            // Необходимые данные для формирования запросов
            val typeGood = values[0]
            val page: Int = values[1].toInt()
            val search = values[2]

            // Загрузка списка комплектующих выбранной категории]
            val callGoods = requestAPI!!.getGoodsPage(typeGood, page, search)
            val response1: Response<ArrayList<Component>>
            try {
                response1 = callGoods.execute()
                if (response1.isSuccessful) {
                    goodsList = response1.body()!!
                    publishProgress(SET_GOODS)
                } else return false
            } catch (e: Exception) {
                Log.e(LogsKeys.ERROR_LOG.toString(), e.toString())
            }

            // Если неизвестно максимальное количество страниц в поиске
            if (maxPages == 0) {
                val callMaxPages = requestAPI!!.getMaxPages(typeGood, search)
                val response2: Response<Int>
                try {
                    response2 = callMaxPages.execute()
                    if (response2.isSuccessful) {
                        maxPages = response2.body()!!
                        publishProgress(SET_FLIP_PAGER)
                    }
                } catch (e: Exception) {
                    Log.e(LogsKeys.ERROR_LOG.toString(), e.toString())
                }
            } else publishProgress(SET_FLIP_PAGER)
            return true
        }

        // Отрисовка всех комплектующих
        override fun onPostExecute(success: Boolean) {
            if (!success) {
                progressBar!!.visibility = View.GONE
                tvNotFound!!.visibility = View.VISIBLE
            }
            super.onPostExecute(success)
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }
}