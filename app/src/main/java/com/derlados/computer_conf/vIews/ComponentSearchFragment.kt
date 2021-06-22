package com.derlados.computerconf.VIews

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.GONE
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.derlados.computer_conf.Constants.ComponentCategory
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.VIews.OnFragmentInteractionListener
import com.derlados.computer_conf.interfaces.ComponentSearchView
import com.derlados.computer_conf.models.Component
import com.derlados.computer_conf.vIews.adapters.ComponentRecyclerAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_component_search.view.*
import java.util.*
import kotlin.collections.ArrayList

class ComponentSearchFragment : Fragment(), MainActivity.OnBackPressedListener, ComponentSearchView {
    private var keepVisible = true
    private lateinit var category: ComponentCategory // Категория комплектующих на текущей странице
    private lateinit var searchText: String // Текст поисковой строки

    private lateinit var rvComponents: RecyclerView
    private lateinit var currentFragment: View
    private lateinit var searchString: EditText // Поисковая строка
    private lateinit var tvNotFound: TextView
    private lateinit var pbLoading: ProgressBar // Поисковая строка
    private lateinit var fragmentListener: OnFragmentInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentFragment = inflater.inflate(R.layout.fragment_component_search, container, false)
        category = requireArguments()["category"] as ComponentCategory
        rvComponents = currentFragment.fragment_component_search_rv
        tvNotFound = currentFragment.fragment_component_search_tv_not_found
        pbLoading = currentFragment.fragment_component_search_pb_loading

        // Поисковая строка
        searchString = currentFragment.fragment_shop_search_goods_et_search
        searchString.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ -> // Реакация на кнопку submit
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchText = searchString.text.toString()

                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })
        return currentFragment
    }

    // Решение проблемы с анимацией, по скольку вызывается 2 popBackStack метода то и анимация играет дважды, из-за чего появлялось мерцание
    override fun onBackPressed(): Boolean {
        view?.visibility = View.VISIBLE
        keepVisible = true
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !keepVisible) {
            view?.visibility = View.GONE
        }
        keepVisible = false
        super.onHiddenChanged(hidden)
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showNotFoundMessage() {
        tvNotFound.visibility = View.VISIBLE
        pbLoading.visibility = View.GONE
    }

    override fun openProgressBar() {
        tvNotFound.visibility = View.GONE
        pbLoading.visibility = View.VISIBLE
    }

    /**
     * Иниициализация (отрисовка) комплектующих, создает адаптек для RecyclerView
     */
    override fun setComponents(components: ArrayList<Component>) {
        rvComponents.layoutManager = LinearLayoutManager(context)
        rvComponents.adapter = ComponentRecyclerAdapter(components, ::onClickItem)
    }

    override fun updateComponents() {
        val adapter: RecyclerView.Adapter<*>? = rvComponents.adapter
        adapter?.notifyItemRangeInserted(0, adapter.itemCount)
    }

    /** Отправка комплектующего в следующий фрагмет для отображения полной информации о нем
     * Метод используется
     * @param component - комплектующее
     */
    private fun onClickItem (component: Component) {
        val data = Bundle()
        data.putString("component", Gson().toJson(component)) // Объект передается в виде json
        data.putSerializable("category", category)
        //fragmentListener.nextFragment(this, FullGoodDataFragment(), data, null)
    }

    // Класс для загрузки превью информации о комплектующих
    /* private inner class GoodsDownloader : AsyncTask<String?, Int?, Boolean>() {

        var retrofit: Retrofit? = null
        var componentAPI: ComponentAPI? = null
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
            componentAPI = retrofit.create<ComponentAPI>(ComponentAPI::class.java)
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
            val callGoods = componentAPI!!.getGoodsPage(typeGood, page, search)
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
                val callMaxPages = componentAPI!!.getMaxPages(typeGood, search)
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
    }*/
}